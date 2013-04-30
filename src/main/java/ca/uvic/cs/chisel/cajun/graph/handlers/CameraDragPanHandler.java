package ca.uvic.cs.chisel.cajun.graph.handlers;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PPanEventHandler;

/**
 * Handles panning of a {@link PCamera}.
 *
 * @author Chris
 * @since  9-Nov-07
 */
public class CameraDragPanHandler extends PPanEventHandler {

	public CameraDragPanHandler() {
		super();
		setAutopan(false);
	}

	// OVERRIDES

	@Override
    protected void drag(PInputEvent e) {
        if (e.getPickedNode() instanceof PCamera) {
        	// PAN canvas
            super.drag(e);
        }
    }
    
}
