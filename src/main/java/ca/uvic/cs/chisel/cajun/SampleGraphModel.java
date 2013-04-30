package ca.uvic.cs.chisel.cajun;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ImageIcon;

import ca.uvic.cs.chisel.cajun.graph.DefaultGraphModel;
import ca.uvic.cs.chisel.cajun.graph.arc.DefaultGraphArc;
import ca.uvic.cs.chisel.cajun.graph.arc.GraphArc;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;
import ca.uvic.cs.chisel.cajun.resources.ResourceHandler;

/**
 * Sample graph model - creates a simple graph with one root with 4 children, and a
 * random number (0 - 3) of grand children on each child.
 * Also randomly chooses one of three arc types - is_a, part_of, or develops_from.
 *
 * @author Chris
 * @since  8-Nov-07
 */
class SampleGraphModel extends DefaultGraphModel {

	public static final String DEVELOPS_FROM = "develops_from";
	public static final String PART_OF = "part_of";
	public static final String IS_A = "is_a";

	private static final String CLASS = "class";
	private static final String INSTANCE = "instance";

	private final ImageIcon ICON;
	
	private long nextNodeID = 0;
	private long nextArcID = 0;
	
	public SampleGraphModel() {
		ICON = ResourceHandler.getIcon("eclipse.gif");
		loadSampleData();
	}

	private void loadSampleData() {
		GraphNode root = addNode("Root");
		
		// testing self arcs
//		addArc(nextArcID(), root, root);
//		addArc(nextArcID(), root, root);
//		addArc(nextArcID(), root, root);
		
		int count1 = 4;
		for (int i = 1; i <= count1; i++) {
			GraphNode node = addNode("Child #" + i);
			addArc(root, node);
			
			// test multiple arcs from same src to dest nodes
			if (i == 1) {
				//addArc(root, node);
				//addArc(node, root);
				//addArc(node, root);
			}
			
			int count2 = (int)(Math.random()*4);
			for (int j = 1; j < count2; j++) {
				GraphNode node2 = addNode("Grand Child #" + j);
				addArc(node, node2);
			}
		}
	}
	
	private GraphNode addNode(String name) {
		return addNode(nextNodeID(), name, ICON, getRandomNodeType());
	}

	private Object getRandomNodeType() {
		int random = (int)Math.floor(Math.random()*2);
		switch (random) {
			case 1 :
				return INSTANCE;
			default :
				return CLASS;
		}
	}
	
	public Collection<Object> getNodeTypes() {
		ArrayList<Object> types = new ArrayList<Object>(2);
		types.add(CLASS);
		types.add(INSTANCE);
		return types;
	}
	
	private GraphArc addArc(GraphNode src, GraphNode dest) {
		DefaultGraphArc arc = (DefaultGraphArc) addArc(nextArcID(), src, dest, getRandomArcType());
		// for this sample the arc types work backworks (is_a, etc)
		arc.setInverted(true);
		return arc;
	}

	private Object getRandomArcType() {
		int random = (int)Math.floor(Math.random()*3);
		switch (random) {
			case 1 :
				return PART_OF;
			case 2 :
				return DEVELOPS_FROM;
			default :
				return IS_A;
		}
	}
	
	public Collection<Object> getArcTypes() {
		ArrayList<Object> types = new ArrayList<Object>(3);
		types.add(IS_A);
		types.add(PART_OF);
		types.add(DEVELOPS_FROM);
		return types;
	}

	private Long nextNodeID() {
		Long id = new Long(nextNodeID);
		nextNodeID++;
		return id;
	}
	
	private Long nextArcID() {
		Long id = new Long(nextArcID);
		nextArcID++;
		return id;
	}
	
}
