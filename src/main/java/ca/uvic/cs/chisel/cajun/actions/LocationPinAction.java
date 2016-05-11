package ca.uvic.cs.chisel.cajun.actions;

import ca.uvic.cs.chisel.cajun.graph.AbstractGraph;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;
import ca.uvic.cs.chisel.cajun.resources.ResourceHandler;

import java.awt.event.ActionEvent;

public class LocationPinAction extends CajunAction {
    private static final long serialVersionUID = -1259172075912890902L;
    private AbstractGraph graph;

    public LocationPinAction(AbstractGraph graph) {
        super("Pin Nodes Location", ResourceHandler.getIcon("icon_pin_location.png"));
        this.graph = graph;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (GraphNode node : graph.getModel().getAllNodes()) {
            node.setFixedLocation(true);
        }
    }

}
