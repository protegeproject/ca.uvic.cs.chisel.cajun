/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.graph.handlers;

import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import edu.umd.cs.piccolo.PCamera;

/**
 * Supports the basic zooming over a nested hierarchy.
 *
 * Note: this class is hacked on Bederson's ZoomEventHandler.
 *
 * @author Chris Callendar
 */
public class PNormalZoomHandler implements Runnable {
	
	/** The default minimum magnification */
	public static final int MIN_MAG = 0;
	/** The default maximum magnification */
	public static final int MAX_MAG = 10;

	/** The default zooming in scale value. */
	public static final double MAGNIFY_SCALE = 1.03f;

	/** The default zooming out scale value. */
	public static final double SHRINK_SCALE = 0.97f;

	/** The default pause between zooming steps */
	public static final int DEFAULT_ZOOM_PAUSE = 0;
	public static final int MIN_ZOOM_PAUSE = 0;
	public static final int MAX_ZOOM_PAUSE = 100;
	
	// The amount to pause between zooming steps
	private int zoomPause = DEFAULT_ZOOM_PAUSE;

	// The scale amount to zoom by.
	private double scaleValue;

	// The camera we are zooming within.
	private PCamera camera;

	// True while zooming
	private boolean zooming = false;

	// The minimum allowed magnification
	private double minMag = MIN_MAG;

	// The maximum allowed magnification (or disabled if less than 0)
	private double maxMag = MAX_MAG;
	
	// the point around which to zoom
	private double zoomX, zoomY;
	
	private double magnifyScale = MAGNIFY_SCALE;
	private double shrinkScale = SHRINK_SCALE;

	/**
	 * Constructs a new NormalZoomHandler.
	 */
	public PNormalZoomHandler(PCamera camera) {
		this.camera = camera;
	}

	/**
	 * Set the minimum magnification that the camera can be set to
	 * with this event handler. Setting the min mag to <= 0 disables
	 * this feature. If the min mag if set to a value which is greater
	 * than the current camera magnification, then the camera is left
	 * at its current magnification.
	 * @param newMinMag the new minimum magnification
	 */
	public void setMinMagnification(double newMinMag) {
		minMag = newMinMag;
	}

	/**
	 * Set the maximum magnification that the camera can be set to
	 * with this event handler. Setting the max mag to <= 0 disables
	 * this feature. If the max mag if set to a value which is less
	 * than the current camera magnification, then the camera is left
	 * at its current magnification.
	 * @param newMaxMag the new maximum magnification
	 */
	public void setMaxMagnification(double newMaxMag) {
		maxMag = newMaxMag;
	}

	/**
	 * Set the zoom speed.
	 * @param pause The amount of pause between zooming steps. Should be between 0(fast) and 100(slow).
	 */
	public void setZoomSpeed(int pause) {
		if (pause < MIN_ZOOM_PAUSE) {
			zoomPause = MIN_ZOOM_PAUSE;
		} else if (pause > MAX_ZOOM_PAUSE) {
			zoomPause = MAX_ZOOM_PAUSE;
		} else {
			zoomPause = pause;
		}
	}

	/**
	 * Get the zoom speed.
	 * @return The zoom speed.
	 */
	public int getZoomSpeed() {
		return zoomPause;
	}
	
	/**
	 * Sets the default amount to zoom in.  Should be greater than 1!
	 * @see PNormalZoomHandler#startZoomingIn()
	 * @see PNormalZoomHandler#startZoomingIn(double, double)
	 */
	public void setMagnificationScale(double magScale) {
		this.magnifyScale = magScale;
	}
	
	public double getMagnificationScale() {
		return magnifyScale;
	}

	/**
	 * Sets the default scale used when zooming out - should be less than 1!
	 * @see PNormalZoomHandler#startZoomingOut()
	 * @see PNormalZoomHandler#startZoomingOut(double, double)
	 */
	public void setShrinkScale(double shrinkScale) {
		this.shrinkScale = shrinkScale;
	}
	
	public double getShrinkScale() {
		return shrinkScale;
	}

	public void zoomInOneStep() {
		startZoomingIn();
		stopZooming();
	}
	
	public void zoomOutOneStep() {
		startZoomingOut();
		stopZooming();
	}
	
	/**
	 * Zooms in on the center of the camera bounds using the default magnification scale.
	 * @see PNormalZoomHandler#setMagnificationScale(double)
	 */
	public void startZoomingIn() {
		Point2D center = camera.getBounds().getCenter2D();
		startZoomingIn(center.getX(), center.getY());
	}

	/**
	 *  Start zooming in, at the position expressed as x, y using the default magnification factor
	 * @see PNormalZoomHandler#setMagnificationScale(double)
	 */
	public void startZoomingIn(double x, double y) {
		startZoomingIn(x, y, magnifyScale);
	}
	
	public void startZoomingIn(double x, double y, double scale) {
		this.zoomX = x;
		this.zoomY = y;
		this.scaleValue = scale;
		startZooming();
	}
	
	/**
	 * Zooms out on the center of the camera bounds using the default shrink scale.
	 * @see PNormalZoomHandler#setShrinkScale(double)
	 */
	public void startZoomingOut() {
		Point2D center = camera.getBounds().getCenter2D();
		startZoomingOut(center.getX(), center.getY());
	}

	/**
	 *  Start zooming Out using the default shrink scale.
	 * @see PNormalZoomHandler#setShrinkScale(double)
	 */
	public void startZoomingOut(double x, double y) {
		startZoomingOut(x, y, shrinkScale);
	}
	
	/**
	 *  Start zooming Out
	 */
	public void startZoomingOut(double x, double y, double scale) {
		this.zoomX = x;
		this.zoomY = y;
		// Set the shrink scale.
		scaleValue = scale;
		startZooming();
	}

	/**
	 * Start animated zooming.
	 */
	private void startZooming() {
		zooming = true;
		zoomOneStep();
	}

	/**
	 * Stop animated zooming.
	 */
	public void stopZooming() {
		zooming = false;
	}
	
	public void noZoom() {
		Point2D center = camera.getBounds().getCenter2D();
		noZoom(center.getX(), center.getY());
	}

	public void noZoom(double x, double y) {
		this.zoomX = x;
		this.zoomY = y;
		if (camera.getViewScale() != 1) {
			this.scaleValue = 1 / camera.getViewScale();
			this.zooming = true;
			zoomOneStep();
			this.zooming = false;
		}
	}

	/**
	 * Do one basic zooming step and schedule the next zooming step.
	 */
	private void zoomOneStep() {
		if (zooming) {
			long startTime = System.currentTimeMillis();

			// Check for magnification bounds.
			double newMag, currentMag;
			currentMag = camera.getViewScale();
			newMag = currentMag * scaleValue;
			if (newMag < minMag) {
				scaleValue = minMag / currentMag;
			}
			if ((maxMag > 0) && (newMag > maxMag)) {
				scaleValue = maxMag / currentMag;
			}
			
			// Now, go ahead and zoom one step
			camera.scaleViewAboutPoint(scaleValue, zoomX, zoomY);

			long sleepTime = zoomPause - (System.currentTimeMillis() - startTime);
			// don't want to zoom too fast, so take a little nap if needed
			while (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				sleepTime = zoomPause - (System.currentTimeMillis() - startTime);
			}

			SwingUtilities.invokeLater(this); // calls the run method
		}
	}

	public void run() {
		PNormalZoomHandler.this.zoomOneStep();
	}
	
}
