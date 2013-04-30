package ca.uvic.cs.chisel.cajun.actions;

import ca.uvic.cs.chisel.cajun.graph.DefaultGraphModel;
import ca.uvic.cs.chisel.cajun.graph.Graph;
import ca.uvic.cs.chisel.cajun.graph.GraphModel;
import ca.uvic.cs.chisel.cajun.graph.arc.GraphArc;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;
import ca.uvic.cs.chisel.cajun.resources.ResourceHandler;

public class ClearOrphansAction extends CajunAction {
	private static final long serialVersionUID = 2406231898001180745L;

	private static final String ACTION_NAME = "Remove Orphan Nodes";
	
	private DefaultGraphModel model;
	private Graph graph;
	
	public ClearOrphansAction(GraphModel model, Graph graph) {
		super(ACTION_NAME, ResourceHandler.getIcon("chart_line_delete.png"));
		this.model = (DefaultGraphModel)model;
		this.graph = graph;
	}
	
	@Override
	public void doAction() {
		boolean graphChanged = false;
		
		GraphNode nodes[] = model.getAllNodes().toArray(new GraphNode[model.getAllNodes().size()]);
		
		// goes through all nodes and hides any that have no arcs or have no visible arcs
		for(GraphNode node : nodes) {
			if(node.getArcs().size() == 0) {
				graphChanged = true;
				model.removeNode(node.getUserObject());
			}
			else {
				boolean found = false;
				for(GraphArc arc : node.getArcs()) {
					if(arc.isVisible()) {
						found = true;
					}
				}
				if(!found) {
					graphChanged = true;
					model.removeNode(node.getUserObject());
				}
			}
		}
		
		// re-layout the graph if it changed
		if(graphChanged) graph.performLayout();
	}
}
