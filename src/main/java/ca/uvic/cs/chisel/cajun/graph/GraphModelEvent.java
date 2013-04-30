package ca.uvic.cs.chisel.cajun.graph;

import java.util.ArrayList;
import java.util.Collection;

import ca.uvic.cs.chisel.cajun.graph.arc.GraphArc;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;

public class GraphModelEvent {

	private Collection<GraphNode> nodes;
	private Collection<GraphArc> arcs;

	public GraphModelEvent(Collection<GraphArc> arcs) {
		this(new ArrayList<GraphNode>(0), arcs);
	}
	
	public GraphModelEvent(Collection<GraphNode> nodes, Collection<GraphArc> arcs) {
		this.nodes = nodes;
		this.arcs = arcs;
	}
	
	public Collection<GraphNode> getNodes() {
		return nodes;
	}
	
	public Collection<GraphArc> getArcs() {
		return arcs;
	}
	
	@Override
	public String toString() {
		return "GraphModelEvent: nodes=" + nodes + ", arcs=" + arcs;
	}
	
}
