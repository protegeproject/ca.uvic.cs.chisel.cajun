package ca.uvic.cs.chisel.cajun.graph.handlers;

import ca.uvic.cs.chisel.cajun.graph.arc.GraphArc;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;

/**
 * Highlights {@link GraphNode} and {@link GraphArc} objects on mouse over.
 * 
 * @author Chris
 * @since  9-Nov-07
 */
public class HighlightHandler extends PBasicInputEventHandler {

	private PNode currentTarget;

	public HighlightHandler() {
		super();
		PInputEventFilter filter = new PInputEventFilter();
		filter.rejectAllEventTypes();
		filter.setAcceptsMouseMoved(true);
		setEventFilter(filter);
	}

	// OVERRIDES

	@Override
	public void mouseMoved(PInputEvent event) {
		PNode target = event.getPickedNode();
		if (target != currentTarget) {
			highlightTarget(currentTarget, false);
			this.currentTarget = target;
			highlightTarget(target, true);
		}
	}

	protected void highlightTarget(PNode target, boolean highlight) {
		if (highlight) {
			target.moveToFront();
		}
		if (target instanceof GraphNode) {
			GraphNode node = (GraphNode) target;
			node.setHighlighted(highlight);
		} else if (target instanceof GraphArc) {
			GraphArc arc = (GraphArc) target;
			arc.setHighlighted(highlight);
		}
	}
}
