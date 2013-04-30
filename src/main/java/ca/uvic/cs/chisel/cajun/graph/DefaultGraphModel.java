package ca.uvic.cs.chisel.cajun.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import ca.uvic.cs.chisel.cajun.graph.arc.DefaultGraphArc;
import ca.uvic.cs.chisel.cajun.graph.arc.GraphArc;
import ca.uvic.cs.chisel.cajun.graph.node.DefaultGraphNode;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;

public class DefaultGraphModel implements GraphModel {

	private Collection<GraphModelListener> listeners;

	private Map<Object, GraphNode> nodes;
	private Map<Object, GraphArc> arcs;

	private Set<Object> nodeTypes;
	private Set<Object> arcTypes;
	
	public DefaultGraphModel() {
		this.listeners = new ArrayList<GraphModelListener>();
		this.nodes = new HashMap<Object, GraphNode>();
		this.arcs = new HashMap<Object, GraphArc>();
		this.nodeTypes = new HashSet<Object>();
		this.arcTypes = new HashSet<Object>();
	}

	public void clear() {
		if ((nodes.size() > 0) || (arcs.size() > 0)) {
			nodes.clear();
			arcs.clear();
			nodeTypes.clear();
			arcTypes.clear();
			fireGraphClearedEvent();
		}
	}

	public void addGraphModelListener(GraphModelListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeGraphModelListener(GraphModelListener listener) {
		listeners.remove(listener);
	}

	protected void fireGraphClearedEvent() {
		if (listeners.size() > 0) {
			ArrayList<GraphModelListener> clonedListeners = new ArrayList<GraphModelListener>(listeners);
			for (GraphModelListener gml : clonedListeners) {
				gml.graphCleared();
			}
		}
	}
	
	protected void fireNodeTypeAddedEvent(Object nodeType) {
		if (listeners.size() > 0) {
			ArrayList<GraphModelListener> clonedListeners = new ArrayList<GraphModelListener>(listeners);
			for (GraphModelListener gml : clonedListeners) {
				gml.graphNodeTypeAdded(nodeType);
			}
		}
	}
	
	protected void fireNodeAddedEvent(GraphNode node) {
		if (listeners.size() > 0) {
			ArrayList<GraphModelListener> clonedListeners = new ArrayList<GraphModelListener>(listeners);
			for (GraphModelListener gml : clonedListeners) {
				gml.graphNodeAdded(node);
			}
		}
	}

	protected void fireNodeRemovedEvent(GraphNode node) {
		if (listeners.size() > 0) {
			ArrayList<GraphModelListener> clonedListeners = new ArrayList<GraphModelListener>(listeners);
			for (GraphModelListener gml : clonedListeners) {
				gml.graphNodeRemoved(node);
			}
		}
	}

	protected void fireArcTypeAddedEvent(Object arcType) {
		if (listeners.size() > 0) {
			ArrayList<GraphModelListener> clonedListeners = new ArrayList<GraphModelListener>(listeners);
			for (GraphModelListener gml : clonedListeners) {
				gml.graphArcTypeAdded(arcType);
			}
		}
	}
	
	protected void fireArcAddedEvent(GraphArc arc) {
		if (listeners.size() > 0) {
			ArrayList<GraphModelListener> clonedListeners = new ArrayList<GraphModelListener>(listeners);
			for (GraphModelListener gml : clonedListeners) {
				gml.graphArcAdded(arc);
			}
		}
	}

	protected void fireArcRemovedEvent(GraphArc arc) {
		if (listeners.size() > 0) {
			ArrayList<GraphModelListener> clonedListeners = new ArrayList<GraphModelListener>(listeners);
			for (GraphModelListener gml : clonedListeners) {
				gml.graphArcRemoved(arc);
			}
		}
	}

	public Collection<GraphNode> getAllNodes() {
		return nodes.values();
	}
	
	public Collection<GraphNode> getVisibleNodes() {
		ArrayList<GraphNode> visibleNodes = new ArrayList<GraphNode>();
		for (GraphNode node : nodes.values()) {
			if (node.isVisible()) {
				visibleNodes.add(node);
			}
		}
		return visibleNodes;
	}

	public GraphNode getNode(Object userObject) {
		if (userObject != null) {
			return nodes.get(userObject);
		}
		return null;
	}

	public boolean containsNode(GraphNode node) {
		if (node != null) {
			return nodes.containsKey(node.getUserObject());
		}
		return false;
	}

	public Collection<GraphNode> getConnectedNodes(Object nodeUserObject) {
		if (nodes.containsKey(nodeUserObject)) {
			GraphNode node = nodes.get(nodeUserObject);
			return node.getConnectedNodes();
		}
		// not sure if this is necessary or a good idea?
		if (nodeUserObject instanceof GraphNode) {
			GraphNode node = (GraphNode) nodeUserObject;
			return node.getConnectedNodes();
		}

		// return null or empty list?
		return Collections.emptyList();
	}

	public Collection<GraphArc> getArcs(Object nodeUserObject) {
		if (nodes.containsKey(nodeUserObject)) {
			GraphNode node = nodes.get(nodeUserObject);
			return node.getArcs();
		}

		// not sure if this is necessary or a good idea
		if (nodeUserObject instanceof GraphNode) {
			GraphNode node = (GraphNode) nodeUserObject;
			return node.getArcs();
		}

		// return null or empty list?
		return Collections.emptyList();
	}
	
	public Collection<Object> getNodeTypes() {
		return new HashSet<Object>(nodeTypes);
	}
	
	// ARCS

	public Collection<GraphArc> getAllArcs() {
		return arcs.values();
	}

	public Collection<GraphArc> getVisibleArcs() {
		ArrayList<GraphArc> visibleArcs = new ArrayList<GraphArc>();
		for (GraphArc arc : arcs.values()) {
			if (arc.isVisible()) {
				visibleArcs.add(arc);
			}
		}
		return visibleArcs;
	}
	
	public GraphArc getArc(Object userObject) {
		if (userObject != null) {
			return arcs.get(userObject);
		}
		return null;
	}

	public boolean containsArc(GraphArc arc) {
		if (arc != null) {
			return arcs.containsKey(arc.getUserObject());
		}
		return false;
	}

	public Object getArcType(Object arcUserObject) {
		return GraphItem.UNKNOWN_TYPE;
	}

	public GraphNode getSourceNode(Object arcUserObject) {
		if (arcs.containsKey(arcUserObject)) {
			GraphArc arc = arcs.get(arcUserObject);
			return arc.getSource();
		}

		// not sure if this is necessary or a good idea
		if (arcUserObject instanceof GraphArc) {
			GraphArc arc = (GraphArc) arcUserObject;
			return arc.getSource();
		}

		return null;
	}

	public GraphNode getDestinationNode(Object arcUserObject) {
		if (arcs.containsKey(arcUserObject)) {
			GraphArc arc = arcs.get(arcUserObject);
			return arc.getDestination();
		}

		// not sure if this is necessary or a good idea
		if (arcUserObject instanceof GraphArc) {
			GraphArc arc = (GraphArc) arcUserObject;
			return arc.getDestination();
		}

		return null;
	}
	
	public Collection<Object> getArcTypes() {
		return new HashSet<Object>(arcTypes);
	}

	// Add/Remove methods

	protected void addNodeInternal(GraphNode node) {
		if ((node != null) && !nodes.containsKey(node.getUserObject())) {
			// this should be the ONLY place where nodes are added to the map
			nodes.put(node.getUserObject(), node);
			if (!nodeTypes.contains(node.getType())) {
				nodeTypes.add(node.getType());
				fireNodeTypeAddedEvent(node.getType());
			}
			fireNodeAddedEvent(node);
		}
	}

	public GraphNode addNode(Object userObject) {
		return addNode(userObject, String.valueOf(userObject), null, null);
	}

	public GraphNode addNode(Object userObject, String text) {
		return addNode(userObject, text, null, null);
	}

	public GraphNode addNode(Object userObject, String text, Icon icon) {
		return addNode(userObject, text, icon, null);
	}

	public GraphNode addNode(Object userObject, String text, Icon icon, Object type) {
		if (userObject == null) {
			throw new NullPointerException("All graph nodes must have a user object.");
		}
		if (!nodes.containsKey(userObject)) {
			if (type == null) {
				type = GraphItem.UNKNOWN_TYPE;
			}
			DefaultGraphNode node = new DefaultGraphNode(userObject, text, icon, type);
			addNodeInternal(node);
		}
		return nodes.get(userObject);
	}

	protected void removeNodeInternal(GraphNode node) {
		if (nodes.containsKey(node.getUserObject())) {
			// remove the arcs for this node first
			GraphArc[] arcs = node.getArcs().toArray(new GraphArc[node.getArcs().size()]);

			for (GraphArc arc : arcs) {
				removeArc(arc.getUserObject());
			}

			// now remove the node from this model
			nodes.remove(node.getUserObject());
			fireNodeRemovedEvent(node);
		}
	}

	public void removeNode(Object userObject) {
		if ((userObject != null) && nodes.containsKey(userObject)) {
			removeNodeInternal(nodes.get(userObject));
		}
	}

	/**
	 * Clears the node types, then iterates through all the nodes and adds the
	 * node types back in.
	 */
	public void recalculateNodeTypes() {
		nodeTypes.clear();
		for (GraphNode node : nodes.values()) {
			Object nodeType = node.getType();
			if (!nodeTypes.contains(nodeType)) {
				nodeTypes.add(nodeType);
				fireNodeTypeAddedEvent(nodeType);
			}
		}
	}
	
	/**
	 * Clears the arc types, then iterates through all the arcs and adds the
	 * arc types back in.
	 */
	public void recalculateArcTypes() {
		arcTypes.clear();
		for (GraphArc arc: arcs.values()) {
			Object arcType = arc.getType();
			if (!arcTypes.contains(arcType)) {
				arcTypes.add(arcType);
				fireArcTypeAddedEvent(arcType);
			}
		}
	}
	
	public void recalculateArcStyles() {
		for (GraphArc arc: arcs.values()) {
			arc.getArcStyle().setTypes(arcTypes);
		}
	}
	
	protected void addArcInternal(DefaultGraphArc arc) {
		// this is the ONLY place where arcs are added
		arcs.put(arc.getUserObject(), arc);
		if (!arcTypes.contains(arc.getType())) {
			arcTypes.add(arc.getType());
			fireArcTypeAddedEvent(arc.getType());
		}

		// add this arc to the source and destination nodes
		arc.getSource().addArc(arc);
		arc.getDestination().addArc(arc);

		fireArcAddedEvent(arc);
	}
	
	public GraphArc addArc(Object userObject, GraphNode src, GraphNode dest, Object type, Icon icon) {
		if (userObject == null) {
			throw new NullPointerException("All graph arcs must have a user object.");
		}
		if (!arcs.containsKey(userObject)) {
			addNodeInternal(src);
			addNodeInternal(dest);
			DefaultGraphArc arc = new DefaultGraphArc(userObject, src, dest, icon, type);
			addArcInternal(arc);
			arrangeArcs(arc.getSource(), arc.getDestination());
		}
		return arcs.get(userObject);
	}
	
	public GraphArc addArc(Object userObject, GraphNode src, GraphNode dest, Object type) {
		return addArc(userObject, src, dest, type, null);
	}

	protected void removeArcInternal(GraphArc arc) {
		if ((arc != null) && arcs.containsKey(arc.getUserObject())) {
			// remove this arc from the source and destination nodes
			arc.getSource().removeArc(arc);
			arc.getDestination().removeArc(arc);
			// now remove the arc from the model and fire the event
			arcs.remove(arc.getUserObject());
			fireArcRemovedEvent(arc);
		}
	}

	public void removeArc(Object userObject) {
		if ((userObject != null) && arcs.containsKey(userObject)) {
			removeArcInternal(arcs.get(userObject));
		}
	}

	/**
	 * Arranges all arcs going between the source and destination nodes so that they do not overlap.
	 * Sets the curve factor on each arc.
	 * 
	 * @see GraphArc#setCurveFactor(int)
	 * @param src the source node
	 * @param dest the destination node
	 */
	public void arrangeArcs(GraphNode src, GraphNode dest) {
		ArrayList<GraphArc> srcToDestArcs = new ArrayList<GraphArc>();
		ArrayList<GraphArc> destToSrcArcs = new ArrayList<GraphArc>();
		for (GraphArc arc : src.getArcs()) {
			if ((src == arc.getSource()) && (dest == arc.getDestination())) {
				srcToDestArcs.add(arc);
			} else if ((src == arc.getDestination()) && ((dest == arc.getSource()))) {
				destToSrcArcs.add(arc);
			}
		}

		// the initial curve factor
		// if arcs are only in one direction then we use a straight line for the first arc
		int startingCurve = 1;
		if ((srcToDestArcs.size() == 0) || (destToSrcArcs.size() == 0)) {
			startingCurve = 0;
		}

		int curveFactor = startingCurve;
		for (GraphArc arc : srcToDestArcs) {
			arc.setCurveFactor(curveFactor);
			curveFactor++;
		}

		curveFactor = startingCurve;
		for (GraphArc arc : destToSrcArcs) {
			arc.setCurveFactor(curveFactor);
			curveFactor++;
		}
	}

}
