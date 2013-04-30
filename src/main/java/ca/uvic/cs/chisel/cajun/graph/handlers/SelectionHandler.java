package ca.uvic.cs.chisel.cajun.graph.handlers;

import java.awt.event.InputEvent;

import ca.uvic.cs.chisel.cajun.graph.arc.GraphArc;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;
import ca.uvic.cs.chisel.cajun.graph.node.NodeCollection;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;

/**
 * Handles node selection - listens for mouse pressed events on the canvas
 * and updates the selection accordingly.
 *
 * @author Chris
 * @since  8-Nov-07
 */
public class SelectionHandler extends PBasicInputEventHandler {
	
	private NodeCollection selectedNodes;

	public SelectionHandler(NodeCollection selectedNodes) {
		super();
		this.selectedNodes = selectedNodes;
		PInputEventFilter filter = new PInputEventFilter();
		filter.rejectAllEventTypes();
		filter.setOrMask(InputEvent.BUTTON1_MASK | InputEvent.BUTTON3_MASK);
		filter.setAcceptsMousePressed(true);
		setEventFilter(filter);
	}

	// OVERRIDES
    
	@Override
    public void mousePressed(PInputEvent e) {
    	PNode node = e.getPickedNode();
		if (node instanceof GraphNode) {
			node.moveToFront();
    		nodePressed(e, (GraphNode)node);
    	} else if (node instanceof GraphArc) {
			node.moveToFront();
    		arcPressed(e, (GraphArc)node);
    	} else if (node instanceof PCamera) {
    		cameraPressed(e, (PCamera)node);
    	}
		
    	super.mousePressed(e);
    }

	private void arcPressed(PInputEvent e, GraphArc arc) {

	}

	private void cameraPressed(PInputEvent e, PCamera camera) {
		// clear selection
		selectedNodes.clear();
	}

	private void nodePressed(PInputEvent e, GraphNode displayNode) {
		if (e.isControlDown()) {
			selectedNodes.addOrRemoveNode(displayNode);
		} else if (e.isShiftDown()) {
			selectedNodes.addNode(displayNode);
		} else {
			if (e.isRightMouseButton()) {
				// right click - only set if the node isn't already selected
				if (!selectedNodes.containsNode(displayNode)) {
					selectedNodes.setNode(displayNode);
				}
			} else {
				// left click - always select just this node
				selectedNodes.setNode(displayNode);
			}
		}
	}
        
}
