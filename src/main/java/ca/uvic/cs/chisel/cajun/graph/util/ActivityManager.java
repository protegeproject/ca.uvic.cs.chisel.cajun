/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.graph.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.zest.layouts.progress.ProgressEvent;
import org.eclipse.zest.layouts.progress.ProgressListener;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivityScheduler;
import edu.umd.cs.piccolo.util.PUtil;

/**
 * Schedules and managers activities (listens for completion).
 * 
 * @author Chris Callendar
 */
public class ActivityManager implements PActivity.PActivityDelegate {
	
	private static final long MIN_PAINT_INTERVAL = 10;
	
	private PCanvas canvas;
	private PActivityScheduler scheduler;
	private boolean activitiesFinished;
	private HashSet<PActivity> activitiesSet;
	
	private List<ProgressListener> progressListeners;
	
	/**
	 * Schedules multiple activities.  If the scheduler is null the activities will
	 * not be scheduled.
	 * @param scheduler the scheduler, can be null
	 */
	public ActivityManager(PCanvas canvas, PActivityScheduler scheduler, PActivity activity) {
		this.canvas = canvas;
		this.scheduler = scheduler;
		
		this.progressListeners = new ArrayList<ProgressListener>();
		
		setActivities(makeList(activity));
	}

	/**
	 * Schedules multiple activities.  If the scheduler is null the activities will
	 * not be scheduled.
	 * @param scheduler the scheduler, can be null
	 */
	public ActivityManager(PCanvas canvas, PActivityScheduler scheduler) {
		this.canvas = canvas;
		this.scheduler = scheduler;
		
		this.progressListeners = new ArrayList<ProgressListener>();
		
//		for (PActivity activity : activitiesSet) {
//			activity.setDelegate(this);
//			if (scheduler != null) {
//				scheduler.addActivity(activity, true);
//			}
//		}
	}
	
	public void setActivities(Collection<PActivity> activities) {
		scheduler.removeAllActivities();
		
		activitiesFinished = (activities.size() == 0);
		activitiesSet = new HashSet<PActivity>(activities);
		
		for (PActivity activity : activitiesSet) {
			activity.setDelegate(this);
			if (scheduler != null) {
				scheduler.addActivity(activity, true);
			}
		}
	}
	
	public void addProgressListener(ProgressListener listener) {
		progressListeners.add(listener);
	}
	
	private void fireProgressEnded() {
		ProgressEvent event = new ProgressEvent(0, 0);
		for(ProgressListener listener : progressListeners) {
			listener.progressEnded(event);
		}
	}

	private static Collection<PActivity> makeList(PActivity activity) {
		ArrayList<PActivity> list = new ArrayList<PActivity>(1);
		list.add(activity);
		return list;
	}
	
	public void activityStarted(PActivity activity) {
		activitiesFinished = false;
		if (!activitiesSet.contains(activity)) {
			activitiesSet.add(activity);
		}
	}

	public void activityStepped(PActivity activity) {}

	public void activityFinished(PActivity activity) {
		boolean removed = activitiesSet.remove(activity);
		// if all activities have finished then we are done
		if (removed && (activitiesSet.size() == 0)) {
			activitiesFinished = true;
			
			fireProgressEnded();
		}
	}

	/** If all the activities have finished. */
	public boolean isFinished () {
		return activitiesFinished;
	}
	
	public void setFinished(boolean activitiesFinished) {
		this.activitiesFinished = activitiesFinished;
	}
	
	/**
	 * Sleeps until all the activities have finished.
	 */
	public void sleepUntilFinished() {
		while (!isFinished()) {
			try {
				Thread.sleep(PUtil.DEFAULT_ACTIVITY_STEP_RATE);
			} catch (InterruptedException ignore) {}
		}
	}

	/**
	 * Waits for the activities to be finished, painting the canvas until they are done.
	 */
	public void waitForActivitiesToFinish() {
		long lastPaint = 0;
		while (!isFinished()) {
			try {
				long now = System.currentTimeMillis();
				scheduler.processActivities(now);
				if ((canvas != null) && (canvas.getParent() != null)) {
					if ((now - lastPaint) > MIN_PAINT_INTERVAL) {
						lastPaint = now;
						canvas.paintImmediately(canvas.getParent().getBounds());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				setFinished(true);
			}
		}
	}
}
