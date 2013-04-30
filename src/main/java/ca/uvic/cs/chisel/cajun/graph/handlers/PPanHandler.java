/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.graph.handlers;


import javax.swing.SwingUtilities;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * NoramlPanHandler supports the basic panning over a nested hierarchy.
 *
 * David Perrin Oct 12, 2001
 */
public class PPanHandler implements Runnable {
    /**
     * The default panning scale value as a portion of the visible zCanvas.
     */
    public static final double PANNING_SCALE = 0.01f;

    /**
     * Constants for the direction we want to pan.
     */
    public static final int NORTH = 1;
    public static final int SOUTH = 2;
    public static final int EAST  = 3;
    public static final int WEST  = 4;
    
    /**
     * The default pause between panning steps
     */
    public static final int DEFAULT_PAN_PAUSE = 0;

	// The amount to pause between panning steps
    private int panPause = DEFAULT_PAN_PAUSE;
    
    // The amount to pan by.
    private double incrementNS = 1;
    private double incrementEW = 1;

    // True when event handlers are attached to a node.
    private boolean active = false;

    // The camera we are panning within.
    private PCamera camera = null;

    // True while panning
    private boolean panning = false;

    private int direction;

    /**
     * Constructs a new PNormalPanHandler.
     */
    public PPanHandler(PCamera camera) {
		this.camera = camera;
    }
    
    /**
     * Sets this event handler active or not.
     * @param active <code>true</code> to make this event handler active.
     */
    public void setActive(boolean active) {
		if (this.active && !active) {
		    // Turn off event handlers.
		    this.active = false;
		} else if (!this.active && active) {
		    // Turn on event handlers.
		    this.active = true;
		}
    }

    public boolean isActive(){
		return active;
    }

    /**
     * Start animated panning.
     */
    public void startPanning(int direction) {
		panning = true;
		this.direction = direction;
		panOneStep();
    }

    /**
     * Stop animated panning.
     */
    public void stopPanning() {
		panning = false;
    }

    /**
     * Set the pan speed.
     * @param pause The amount to pause between pan steps. Should be between 0(fast) and 100(slow).
     */
    public void setPanSpeed(int pause) {
	    if (pause < 0) {
			panPause = 0;
		} else if (pause > 100) {
			panPause = 100;
		} else {
			panPause = pause;
		}
    }

    /**
     * Get the pan speed.
     * @return The pan speed.
     */
    public int getPanSpeed() {
		return panPause;
    }

    /**
     * Do one basic panning step and schedule the next panning step.
     */
    private void panOneStep() {
		if (panning) {
			long startTime = System.currentTimeMillis();
			
			// @tag Shrimp.Bugs_Fixed.Panning : panning causes parts of graph to be cutoff
			// Calling camera.translate(x,y) causes the visible nodes to be cutoff
			// changed to translateView(x,y) like Piccolo's PPanEventHandler class does
			PDimension delta = new PDimension(0, 0);
			
		    // Decide which way to pan
		    if (direction == NORTH) {
		    	//camera.translate( 0, -incrementNS );	// causes nodes to be cutoff...
		    	delta.setSize(0, -incrementNS);
		    } else if (direction == SOUTH){
		    	//camera.translate( 0, incrementNS );
		    	delta.setSize(0, incrementNS);
		    } else if (direction == EAST){
		    	//camera.translate( incrementEW, 0 );
		    	delta.setSize(incrementEW, 0);
		    } else if (direction == WEST){
		    	//camera.translate( -incrementEW, 0 );
		    	delta.setSize(-incrementEW, 0);
		    }
		    
		    camera.localToView(delta);
		    if ((delta.width != 0) || (delta.height != 0)) {
		    	camera.translateView(delta.width, delta.height);
		    }
            
            long finishTime = System.currentTimeMillis();
            long sleepTime = panPause - (finishTime - startTime);
            // don't want to pan too fast, so take a little nap if needed
            if (sleepTime > 0) {
	            try {
	            	Thread.sleep(sleepTime);
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	        }
			SwingUtilities.invokeLater(this);	// calls the run method
		}
    }

    public void run() {
		PPanHandler.this.panOneStep();
    }
        
}
