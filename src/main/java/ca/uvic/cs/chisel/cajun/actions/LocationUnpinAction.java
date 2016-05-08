package ca.uvic.cs.chisel.cajun.actions;

import ca.uvic.cs.chisel.cajun.graph.AbstractGraph;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;
import ca.uvic.cs.chisel.cajun.resources.ResourceHandler;

import java.awt.event.ActionEvent;

public class LocationUnpinAction extends CajunAction {
    private static final long serialVersionUID = 3267547497320917811L;
    private AbstractGraph graph;

    public LocationUnpinAction(AbstractGraph graph) {
        super("Unpin nodes' location", ResourceHandler.getIcon("icon_unpin_location.png"));
        this.graph = graph;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (GraphNode node : graph.getModel().getAllNodes()) {
            node.setFixedLocation(false);
        }
        graph.performLayout();
    }

}
