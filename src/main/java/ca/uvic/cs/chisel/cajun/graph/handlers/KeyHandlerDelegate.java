package ca.uvic.cs.chisel.cajun.graph.handlers;

import java.awt.event.KeyEvent;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PInputManager;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.event.PInputEventListener;

/**
 * This class is a complicated way of delegating key events.
 * The {@link PInputManager} doesn't have a default key listener so this class is it.
 *
 * @author Chris
 * @since  9-Nov-07
 */
public class KeyHandlerDelegate extends PBasicInputEventHandler {

	private PCamera camera;

	public KeyHandlerDelegate(PCamera camera) {
		this.camera = camera;
		
		PInputEventFilter filter = new PInputEventFilter();
		filter.rejectAllEventTypes();
		filter.setAcceptsKeyPressed(true);
		filter.setAcceptsKeyReleased(true);
		filter.setAcceptsKeyTyped(true);
		setEventFilter(filter);
	}
		
	@Override
	public void keyPressed(PInputEvent event) {
		Object[] listeners = camera.getListenerList().getListenerList();
		if ((listeners.length > 0) && !event.isHandled()) {
			for (int i = 0; i < listeners.length; i++) {
				Object o = listeners[i];
				if (o instanceof PInputEventListener) {
					PInputEventListener listener = (PInputEventListener) o;
					listener.processEvent(event, KeyEvent.KEY_PRESSED);
				}
			}
		}
	}
	
	@Override
	public void keyReleased(PInputEvent event) {
		Object[] listeners = camera.getListenerList().getListenerList();
		if ((listeners.length > 0) && !event.isHandled()) {
			for (int i = 0; i < listeners.length; i++) {
				Object o = listeners[i];
				if (o instanceof PInputEventListener) {
					PInputEventListener listener = (PInputEventListener) o;
					listener.processEvent(event, KeyEvent.KEY_RELEASED);
				}
			}
		}
	}
	
	@Override
	public void keyTyped(PInputEvent event) {
		Object[] listeners = camera.getListenerList().getListenerList();
		if ((listeners.length > 0) && !event.isHandled()) {
			for (int i = 0; i < listeners.length; i++) {
				Object o = listeners[i];
				if (o instanceof PInputEventListener) {
					PInputEventListener listener = (PInputEventListener) o;
					listener.processEvent(event, KeyEvent.KEY_TYPED);
				}
			}
		}
	}

}
