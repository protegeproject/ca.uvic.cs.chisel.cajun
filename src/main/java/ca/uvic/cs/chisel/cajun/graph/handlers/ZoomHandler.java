package ca.uvic.cs.chisel.cajun.graph.handlers;

import java.awt.event.KeyEvent;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;

/**
 * Handles zooming.
 *
 * @author Chris
 * @since  8-Nov-07
 */
public class ZoomHandler extends PBasicInputEventHandler {
	
	private PNormalZoomHandler zoom;
	//private PCamera camera;
	
	public ZoomHandler(PCamera camera) {
		super();
		//this.camera = camera;
		zoom = new PNormalZoomHandler(camera);
		
		PInputEventFilter filter = new PInputEventFilter();
		filter.rejectAllEventTypes();
		filter.setAcceptsKeyPressed(true);
		filter.setAcceptsKeyReleased(true);
		filter.setAcceptsMouseWheelRotated(true);
		setEventFilter(filter);
	}

	// OVERRIDES
	
	@Override
	public void mouseWheelRotated(PInputEvent event) {
		if (event.getWheelRotation() < 0) {
			// zoom in
			zoomIn();
			stopZoom();
		} else if (event.getWheelRotation() > 0) {
			// zoom out
			zoomOut();
			stopZoom();
		}
	}

	@Override
	public void keyPressed(PInputEvent event) {
		switch (event.getKeyCode()) {
			case KeyEvent.VK_EQUALS :
			case KeyEvent.VK_PLUS :
				zoomIn();
				break;
			case KeyEvent.VK_MINUS :
				zoomOut();
				break;
		}
	}
	

	@Override
	public void keyReleased(PInputEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.VK_EQUALS :
		case KeyEvent.VK_PLUS :
		case KeyEvent.VK_MINUS :
			stopZoom();
			break;
		}
	}
	
	public void zoomOut() {
		// zoom out on the center of the canvas
		zoom.startZoomingOut();
	}

	public void zoomIn() {
		// zoom in on the center of the canvas
		zoom.startZoomingIn();
	}
	
	public void stopZoom() {
		zoom.stopZooming();
	}
}
