package ca.uvic.cs.chisel.cajun.graph.node;

import java.util.Collection;

/**
 * Contains the added nodes and removed nodes from a {@link NodeCollection}.
 *
 * @author Chris
 * @since  20-Nov-07
 */
public class GraphNodeCollectionEvent {

	private NodeCollection nodeCollection;
	private Collection<GraphNode> newNodes;
	private Collection<GraphNode> oldNodes;
	
	public GraphNodeCollectionEvent(NodeCollection nodeCollection, Collection<GraphNode> oldNodes, Collection<GraphNode> newNodes) {
		this.nodeCollection = nodeCollection;
		this.oldNodes = oldNodes;
		this.newNodes = newNodes;
	}
	
	public NodeCollection getNodeCollection() {
		return nodeCollection;
	}
	
	public Collection<GraphNode> getNewNodes() {
		return newNodes;
	}
	
	public Collection<GraphNode> getOldNodes() {
		return oldNodes;
	}

	@Override
	public String toString() {
		return "GraphNodeCollectionEvent: " + oldNodes + " -> " + newNodes;
	}
}
