package ca.uvic.cs.chisel.cajun.graph.handlers;

import java.awt.event.KeyEvent;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;

/**
 * Handles panning of a {@link PCamera} when the left, right, up, or down keys are pressed.
 *
 * @author Chris
 * @since  9-Nov-07
 */
public class CameraKeyPanHandler extends PBasicInputEventHandler {

	private PPanHandler panHandler;
	
	public CameraKeyPanHandler(PCamera camera) {
		super();
		this.panHandler = new PPanHandler(camera);
		
		PInputEventFilter filter = new PInputEventFilter();
		filter.rejectAllEventTypes();
		filter.setAcceptsKeyPressed(true);
		filter.setAcceptsKeyReleased(true);
		filter.setAcceptsKeyTyped(true);
		setEventFilter(filter);
	}

	// OVERRIDES

	@Override
	public void keyPressed(PInputEvent event) {
		switch (event.getKeyCode()) {
			case KeyEvent.VK_LEFT :
				panHandler.startPanning(PPanHandler.WEST);
				break;
			case KeyEvent.VK_RIGHT :
				panHandler.startPanning(PPanHandler.EAST);
				break;
			case KeyEvent.VK_UP :
				panHandler.startPanning(PPanHandler.NORTH);
				break;
			case KeyEvent.VK_DOWN :
				panHandler.startPanning(PPanHandler.SOUTH);
				break;
		}
	}
	
	@Override
	public void keyReleased(PInputEvent event) {
		panHandler.stopPanning();
	}
    
}
