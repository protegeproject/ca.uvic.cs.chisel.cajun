package ca.uvic.cs.chisel.cajun.graph;

import ca.uvic.cs.chisel.cajun.graph.arc.GraphArc;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;

/**
 * Handles events relating to a {@link GraphModel}.
 *
 * @author Chris
 * @since  9-Nov-07
 */
public interface GraphModelListener {

	public void graphCleared();
	
	public void graphNodeAdded(GraphNode node);
	public void graphNodeRemoved(GraphNode node);

	public void graphArcAdded(GraphArc arc);
	public void graphArcRemoved(GraphArc arc);
	
	public void graphNodeTypeAdded(Object nodeType);
	public void graphArcTypeAdded(Object arcType);
	
}
