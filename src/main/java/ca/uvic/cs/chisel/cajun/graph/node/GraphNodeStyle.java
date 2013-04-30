package ca.uvic.cs.chisel.cajun.graph.node;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import javax.swing.Icon;

import ca.uvic.cs.chisel.cajun.graph.GraphItemStyle;

public interface GraphNodeStyle extends GraphItemStyle {

	/**
	 * Gets the {@link Shape} for the node. The bounds are the bounds of the node and should be used
	 * as the shape bounds.
	 * 
	 * @param bounds the node bounds
	 * @return the Shape, can't be null
	 */
	public Shape getNodeShape(GraphNode node, Rectangle2D bounds);

	/**
	 * Returns the border stroke for the node.
	 * 
	 * @return the {@link Stroke}, can be null in which case the border won't be drawn
	 */
	public Stroke getBorderStroke(GraphNode node);

	/**
	 * Returns the border paint for the node.
	 * 
	 * @return the {@link Paint}, can be null in which case the border won't be drawn
	 */
	public Paint getBorderPaint(GraphNode node);

	/**
	 * Returns the background color/paint.
	 * 
	 * @return the {@link Paint}, can be null in which case the background won't be filled
	 */
	public Paint getBackgroundPaint(GraphNode node);

	/**
	 * Returns the color/paint for the node's text.
	 * 
	 * @return the {@link Paint}, if null then {@link Color#black} will be used.
	 */
	public Paint getTextPaint(GraphNode node);

	/**
	 * Returns the font for the node's text.
	 * 
	 * @return the {@link Paint}, if null then a default font will be used.
	 */
	public Font getFont(GraphNode node);

	/**
	 * Returns the overlay icon that is drawn on top of a node.
	 * 
	 * @return {@link Icon}
	 */
	public Icon getOverlayIcon(GraphNode graphNode);

	public Collection<Icon> getOverlayIcons(GraphNode graphNode);

	/**
	 * Returns the overlay icon point position in the node's coordinate space.
	 * 
	 * @return {@link Point2D}
	 */
	public Point2D getOverlayIconPosition(GraphNode graphNode, Icon icon);

	/**
	 * Returns the overlay icon point position in the node's coordinate space.
	 * 
	 * @return {@link Point2D}
	 */
	public Point2D getOverlayIconPosition(GraphNode graphNode);
}
