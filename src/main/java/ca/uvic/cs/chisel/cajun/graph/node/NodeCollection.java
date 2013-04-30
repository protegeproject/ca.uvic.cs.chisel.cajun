package ca.uvic.cs.chisel.cajun.graph.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Holds a list of {@link GraphNode}s.
 * Handles additions and removals and fires changes to any {@link GraphNodeCollectionListener} listeners.
 *
 * @see GraphNodeCollectionListener
 * @see GraphNodeCollectionEvent
 * @author Chris
 * @since  1-Nov-07
 */
public class NodeCollection {

	private List<GraphNode> nodes;
	private List<GraphNodeCollectionListener> listeners;
	
	public NodeCollection() {
		nodes = new ArrayList<GraphNode>();
		listeners = new ArrayList<GraphNodeCollectionListener>();
	}
	
	public void addCollectionListener(GraphNodeCollectionListener nsl) {
		if (!listeners.contains(nsl)) {
			listeners.add(nsl);
		}
	}
	
	public boolean removeCollectionListener(GraphNodeCollectionListener nsl) {
		return listeners.remove(nsl);
	}
	
	protected void fireCollectionChange(Collection<GraphNode> oldNodes, Collection<GraphNode> newNodes) {
		List<GraphNodeCollectionListener> nsls = new ArrayList<GraphNodeCollectionListener>(listeners);
		GraphNodeCollectionEvent evt = new GraphNodeCollectionEvent(this, oldNodes, newNodes);
		for (GraphNodeCollectionListener nsl : nsls) {
			nsl.collectionChanged(evt);
		}
	}
	
	/**
	 * Returns the collection of {@link GraphNode} objects.
	 * Modifications will not cause events to be fired.
	 * @return the collection
	 */
	public Collection<GraphNode> getNodes() {
		return nodes;
	}

	/**
	 * Returns the first node in the list or null if empty.
	 * @return the first node or null if empty
	 */
	public GraphNode getFirstNode() {
		return (nodes.isEmpty() ? null : nodes.get(0));
	}
	
	/**
	 * @return true if this collection has no nodes in it
	 */
	public boolean isEmpty() {
		return (nodes.size() == 0);
	}
	
	/**
	 * @return the size of this collection (how many nodes)
	 */
	public int size() {
		return nodes.size();
	}

	/**
	 * Checks if the given node is in this collection.
	 * @param node the node to check
	 * @return true of this collection contains the node
	 */
	public boolean containsNode(GraphNode node) {
		return (node != null ? nodes.contains(node) : false);
	}

	/**
	 * Clears this collection.
	 * Fires a collection change event if any nodes were
	 * cleared from this collection.
	 */
	public void clear() {
		if (!this.nodes.isEmpty()) {
			List<GraphNode> oldNodes = this.nodes;
			nodes = new ArrayList<GraphNode>();
			fireCollectionChange(oldNodes, this.nodes);
		}
	}
	
	/**
	 * Sets this collection to contain the one given node.
	 * If null then the collection is cleared.
	 * Fires a collection change event if the collection
	 * doesn't already contain only this node.
	 * @param node the node to set as the collection
	 */
	public void setNode(GraphNode node) {
		// no change - still empty
		if ((node == null) && this.nodes.isEmpty()) {
			return;
		}
		// no change - already contains only this node
		if ((node != null) && (this.nodes.size() == 1) && (this.nodes.get(0) == node)) {
			return;
		}
		// change
		List<GraphNode> oldNodes = this.nodes;
		nodes = new ArrayList<GraphNode>();
		if (node != null) {
			nodes.add(node);
		}
		fireCollectionChange(oldNodes, this.nodes);
	}
	
	/**
	 * Sets the nodes in this collection.
	 * Fires a collection change event
	 * @param nodes the nodes to set, if null then the nodes are cleared
	 */
	public void setNodes(Collection<GraphNode> nodes) {
		List<GraphNode> oldNodes = this.nodes;
		if ((nodes == null) || nodes.isEmpty()) {
			this.nodes = new ArrayList<GraphNode>();
		} else {
			this.nodes = new ArrayList<GraphNode>(nodes);
		}
		fireCollectionChange(oldNodes, this.nodes);
	}

	/**
	 * If the node already exists in this collection, then it is removed.
	 * Otherwise it is added.
	 * Fires a collection change event.
	 * @param node the node to add or remove
	 */
	public void addOrRemoveNode(GraphNode node) {
		if (node != null) {
			List<GraphNode> oldNodes = this.nodes;
			if (nodes.contains(node)) {
				nodes.remove(node);
			} else {
				nodes.add(0, node);
			}
			fireCollectionChange(oldNodes, this.nodes);
		}
	}
	
	/**
	 * Adds the node to the front of this collection.
	 * If the node already exists, it is removed and then added at the
	 * front of the list and no collection change event is fired.
	 * @param node the node to add
	 * @return true if the node was added (didn't exist in this in this collection)
	 */
	public boolean addNode(GraphNode node) {
		boolean added = false;
		if (node != null) {
			List<GraphNode> oldNodes = this.nodes;
			added = !nodes.remove(node);
			nodes.add(0, node);
			if (added) {
				fireCollectionChange(oldNodes, this.nodes);
			}
		}
		return added;
	}
	
	/**
	 * Removes the given node from this collection.
	 * If the node was removed, a collection change event is fired.
	 * @param node the node to remove
	 * @return true if the node was removed
	 */
	public boolean removeNode(GraphNode node) {
		boolean removed = false;
		if (node != null) {
			List<GraphNode> oldNodes = this.nodes;
			removed = nodes.remove(node);
			if (removed) {
				fireCollectionChange(oldNodes, this.nodes);
			}
		}
		return removed;
	}
	
	@Override
	public String toString() {
		return "NodeCollection: " + nodes.toString();
	}
	
	
}
