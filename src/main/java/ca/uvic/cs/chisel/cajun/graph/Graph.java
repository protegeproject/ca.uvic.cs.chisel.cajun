package ca.uvic.cs.chisel.cajun.graph;

import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import ca.uvic.cs.chisel.cajun.actions.LayoutAction;
import ca.uvic.cs.chisel.cajun.graph.arc.GraphArcStyle;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNodeCollectionListener;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNodeStyle;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PInputEventListener;

public interface Graph {

	// Property change names
	public static final String GRAPH_MODEL_PROPERTY = "model";
	public static final String GRAPH_ARC_STYLE_PROPERTY = "arc style";
	public static final String GRAPH_NODE_STYLE_PROPERTY = "node style";
	
	public void addPropertyChangeListener(PropertyChangeListener pcl);
	public void removePropertyChangeListener(PropertyChangeListener pcl);
	
	/**
	 * Adds a graph model listener.  Use this method if you want
	 * this listener to be kept attached to current graph model,
	 * even when the graph has a new model set.
	 * @param gml the listener
	 * @see GraphModel#addGraphModelListener(GraphModelListener)
	 */
	public void addGraphModelListener(GraphModelListener gml);
	
	/**
	 * Removes a graph model listener.
	 * @param gml the listener
	 * @see GraphModel#removeGraphModelListener(GraphModelListener)
	 */
	public void removeGraphModelListener(GraphModelListener gml);
	
	/**
	 * Returns the swing graph component.
	 */
	public JComponent getGraphComponent();
	
	/**
	 * Returns the graph canvas.
	 */
	public PCanvas getCanvas();
	
	/** Returns the node right-click context menu. */
	public JPopupMenu getNodeContextMenu();
	/** Returns the arc right-click context menu. */
	public JPopupMenu getArcContextMenu();
	/** Returns the canvas right-click context menu. */
	public JPopupMenu getCanvasContextMenu();
	
	/**
	 * @return the current {@link GraphModel}, won't be null
	 */
	public GraphModel getModel();
	
	/**
	 * Sets the {@link GraphModel} and fires a PropertyChangeEvent using the
	 * {@link Graph#GRAPH_MODEL_PROPERTY} property name.
	 * @param model
	 */
	public void setModel(GraphModel model);
	
	public void addNodeSelectionListener(GraphNodeCollectionListener listener);
	public void removeNodeSelectionListener(GraphNodeCollectionListener listener);

	/**
	 * @return the collection of selected nodes
	 */
	public Collection<GraphNode> getSelectedNodes();
	
	/**
	 * Sets the selected nodes in the graph.  These nodes will most likely
	 * be rendered differently because they are selected.
	 * @param nodes the selected nodes
	 */
	public void setSelectedNodes(Collection<GraphNode> nodes);
	
	/**
	 * @return the collection of matching nodes (from the last search result)
	 */
	public Collection<GraphNode> getMatchingNodes();
	
	/**
	 * Sets the collection of matching nodes from a search result.
	 * These nodes will probably be rendered differently as a result.
	 * @param nodes the matching nodes.
	 */
	public void setMatchingNodes(Collection<GraphNode> nodes);
	
	public void addGraphInputListener(PInputEventListener listener);
	public void removeGraphInputListener(PInputEventListener listener);
	
	public void clear();
	public void repaint();
	public Rectangle2D getBounds();
	
	/**
	 * Sets the node style for all nodes in the graph.
	 * @param style the style to use
	 */
	public void setGraphNodeStyle(GraphNodeStyle style);
	
	/**
	 * Sets the arc style for all arcs in the graph.
	 * @param style the style to use
	 */
	public void setGraphArcStyle(GraphArcStyle style);
	
	/**
	 * Adds a layout to the graph.
	 * @param layout the layout to add
	 */
	public void addLayout(LayoutAction layout);
	
	/**
	 * Removes a layout from the graph.
	 * @param layout the layout to remove
	 */
	public void removeLayout(LayoutAction layout);
	
	/**
	 * Returns all the layouts currently supported on this graph.
	 * @return a collection of layouts
	 */
	public Collection<LayoutAction> getLayouts();
	
	/**
	 * Runs the layout using the last run layout.
	 * If no layouts have been performed then the default layout is used.
	 */
	public void performLayout();
	
	/**
	 * Runs the given layout on all the visible nodes in the graph.
	 * @param layout
	 */
	public void performLayout(LayoutAction layout);
	
	public void setShowNodeTooltips(boolean showNodeTooltips);
	
	public boolean isShowNodeTooltips();
	
}
