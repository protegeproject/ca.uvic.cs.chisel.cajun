package ca.uvic.cs.chisel.cajun.graph.handlers;

import ca.uvic.cs.chisel.cajun.graph.util.AnimationHandler;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;

/**
 * Focuses on the extends (shows all nodes)
 * when the user middle clicks (or scroll wheel clicks) or double left clicks
 * on the canvas camera.
 *
 * @author Chris
 * @since  9-Nov-07
 */
public class FocusOnExtentsHandler extends PBasicInputEventHandler {

	private AnimationHandler handler;
	private PNode currentTarget;
	
	public FocusOnExtentsHandler(AnimationHandler handler) {
		super();
		this.handler = handler;
		
		// only accept mouse clicks
		PInputEventFilter filter = new PInputEventFilter();
		filter.rejectAllEventTypes();
		filter.setAcceptsMouseClicked(true);
		setEventFilter(filter);
	}

	// OVERRIDES
    
    @Override
    public void mouseClicked(PInputEvent event) {
    	currentTarget = event.getPickedNode();
    	if (event.getClickCount() == 1) {
    		if (event.isMiddleMouseButton()) {
    			handleMiddleMouseClick();
    		}
    	} else if (event.getClickCount() >= 2) {
    		handleDoubleClick();
    	}
    }

    private void handleDoubleClick() {
    	if (currentTarget instanceof PCamera) {
    		handler.focusOnExtents(true);
    	}
	}

	private void handleMiddleMouseClick() {
		if (currentTarget instanceof PCamera) {
    		handler.focusOnExtents(true);
    	}
	}
    
}
