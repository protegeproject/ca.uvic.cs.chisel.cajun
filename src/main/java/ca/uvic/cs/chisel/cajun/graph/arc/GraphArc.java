package ca.uvic.cs.chisel.cajun.graph.arc;

import javax.swing.Icon;

import org.eclipse.zest.layouts.LayoutRelationship;

import ca.uvic.cs.chisel.cajun.graph.GraphItem;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;

public interface GraphArc extends GraphItem, LayoutRelationship {

	/**
	 * Returns the source node for this arc.
	 * @return the source node
	 */
	public GraphNode getSource();
	
	/**
	 * Returns the destination node for this arc.
	 * @return the destination node
	 */
	public GraphNode getDestination();
	
	/**
	 * Returns the style of this arc (the color, stroke etc).
	 * @return the style
	 */
	public GraphArcStyle getArcStyle();

	/**
	 * Sets the style for this arc (color, stroke etc).
	 * @param style the style to set
	 */
	public void setArcStyle(GraphArcStyle style);
	
	/**
	 * Calculates the arc path based on the locations of the source and and destination nodes.
	 */
	public void updateArcPath();
	
	/**
	 * Sets the curve factor.  This factor is used to
	 * prevent arcs from the same source and destination nodes from overlapping.
	 * @param curveFactor an int between 0 and something bigger than 0
	 */
	public void setCurveFactor(int curveFactor);
	
	/**
	 * If an arc is inverted it will be rendered backwards - e.g.
	 * the arrow head will point from destination to source.
	 * Also the tooltip will be reversed (destination type src).
	 * The layouts need the source and destination to remain the same.
	 * @return true if inverted, false by default
	 */
	public boolean isInverted();
	
	/**
	 * Sets if the arc is inverted.  Setting an arc to inverted will cause the
	 * arc arrow head to be rendered backwards (from dest to src).
	 * The tooltip will be reversed too.
	 * Defaults to false.
	 * @param inverted if the arc should be inverted
	 */
	public void setInverted(boolean inverted);

	/**
	 * @return true if this arc is visible on the canvas
	 */
	public boolean isVisible();
	
	/**
	 * Sets this arc to be visible or hidden
	 * @param visible
	 */
	public void setVisible(boolean visible);
	
	/**
	 * Gets the overlay icon for the arc.
	 */
	public Icon getIcon();
	
	public boolean equals(Object arc);
	
	public int hashCode();
	
}
