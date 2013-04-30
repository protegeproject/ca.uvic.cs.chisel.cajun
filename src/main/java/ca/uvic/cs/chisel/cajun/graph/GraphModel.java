package ca.uvic.cs.chisel.cajun.graph;

import java.util.Collection;

import ca.uvic.cs.chisel.cajun.graph.arc.GraphArc;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;

public interface GraphModel {
	
	/**
	 * Removes all nodes and arcs from the model.
	 * The listeners aren't removed.
	 * Also fires an event.
	 */
	public void clear();
	
	public Collection<GraphNode> getAllNodes();
	public Collection<GraphNode> getVisibleNodes();
	public GraphNode getNode(Object userObject);
	public boolean containsNode(GraphNode node);
	public Collection<GraphNode> getConnectedNodes(Object nodeUserObject);
	public Collection<GraphArc> getArcs(Object nodeUserObject);
	public Collection<Object> getNodeTypes();
	
	public Collection<GraphArc> getAllArcs();
	public Collection<GraphArc> getVisibleArcs();
	public GraphArc getArc(Object userObject);
	public boolean containsArc(GraphArc arc);
	public GraphNode getSourceNode(Object arcUserObject);
	public GraphNode getDestinationNode(Object arcUserObject);
	public Collection<Object> getArcTypes();
	
	public void addGraphModelListener(GraphModelListener listener);
	public void removeGraphModelListener(GraphModelListener listener);
	
}
