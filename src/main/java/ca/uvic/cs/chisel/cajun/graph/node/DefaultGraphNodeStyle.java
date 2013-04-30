package ca.uvic.cs.chisel.cajun.graph.node;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import ca.uvic.cs.chisel.cajun.util.GraphicsUtils;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

/**
 * Provides the default styles for {@link DefaultGraphNode}s. Supports different styles based on
 * whether the node is highlighted, selected, or matching. Also has support for coloring nodes based
 * on node type.
 * 
 * @author Chris
 * @since  08-Nov-07
 */
public class DefaultGraphNodeStyle implements GraphNodeStyle {
	
	protected static final Color BG = new Color(192, 192, 224);
	protected static final int SHAPE_ARC = 5;

	protected Paint bgPaint;
	protected Paint borderPaint;
	protected Paint borderHighlightPaint;
	protected Paint borderSelectionPaint;
	protected Paint borderMatchingPaint;
	protected Paint textPaint;
	protected Color tooltipTextColor;
	protected Color tooltipBackground;

	protected Stroke borderStroke;
	protected Stroke borderSelectionStroke;
	protected Stroke borderHighlightStroke;

	protected Font textFont;
	protected Font textHighlightFont;
	protected Font textSelectionFont;
	protected Font tooltipFont;

	protected Icon overlayIcon;

	protected Point2D overlayIconPosition;

	protected Color[] defaultColors;
	protected int nextColorIndex;
	protected Color defaultNodeColor;
	protected Map<Object, Paint> nodeTypeToPaint;

	public DefaultGraphNodeStyle() {
		loadDefaultColors();
		this.nextColorIndex = 0;
		this.nodeTypeToPaint = new HashMap<Object, Paint>();
		this.defaultNodeColor = BG;
		this.bgPaint = new GradientPaint(0, 0, Color.white, 0, 20, defaultNodeColor, true);

		borderPaint = Color.black;
		borderHighlightPaint = Color.black;
		borderSelectionPaint = Color.blue;
		borderMatchingPaint = new Color(0, 224, 0);
		textPaint = Color.black;
		tooltipBackground = defaultColors[0];
		tooltipTextColor = GraphicsUtils.getTextColor(tooltipBackground);

		borderStroke = new PFixedWidthStroke(1f);
		borderHighlightStroke = new PFixedWidthStroke(2f);
		borderSelectionStroke = borderHighlightStroke;

		textFont = PText.DEFAULT_FONT;
		textHighlightFont = textFont;
		textSelectionFont = textFont; //.deriveFont(Font.BOLD);
		tooltipFont = textFont;
	}

	protected void loadDefaultColors() {
		defaultColors = new Color[24];
		// mostly lighter pastel colors
		defaultColors[0] = new Color(165, 195, 210); //darker blue
		defaultColors[1] = new Color(219, 193, 181); //light browny
		defaultColors[2] = new Color(169, 192, 177); //darker green
		defaultColors[3] = new Color(255, 251, 204); //cream
		defaultColors[4] = new Color(184, 183, 204); //darker purple
		defaultColors[5] = new Color(203, 195, 122); //greeny yellow
		defaultColors[6] = new Color(224, 222, 239); //lighter purple
		defaultColors[7] = new Color(212, 208, 179); //tan
		defaultColors[8] = new Color(212, 239, 252); //lighter blue
		defaultColors[9] = new Color(252, 211, 193); //just peachy
		defaultColors[10] = new Color(204, 231, 211); //lighter green
		defaultColors[11] = new Color(217, 194, 206); //pinky purply
		defaultColors[12] = new Color(204, 204, 255); //medium purple
		defaultColors[13] = new Color(255, 160, 122); //light salmon
		defaultColors[14] = new Color(176, 196, 222); //light steel blue
		defaultColors[15] = new Color(119, 136, 153); //light slate gray
		defaultColors[16] = new Color(240, 128, 128); //light coral
		defaultColors[17] = new Color(221, 160, 221); //plum
		defaultColors[18] = new Color(255, 222, 173); //navajo white
		defaultColors[19] = new Color(169, 252, 169); //pale green
		defaultColors[20] = new Color(204, 204, 204); //lightgray
		defaultColors[21] = new Color(255, 204, 255); //lightpink
		defaultColors[22] = new Color(220, 252, 172); //lightgreenyellow
		defaultColors[23] = new Color(204, 255, 255); //lightcyan
	}

	private Color getNextDefaultColor() {
		if (nextColorIndex >= defaultColors.length) {
			nextColorIndex = 0;
		}
		Color color = defaultColors[nextColorIndex];
		nextColorIndex++;
		return color;
	}

	/**
	 * Sets the node types - this is done to map a background color/gradient to each node type.
	 * 
	 * @param nodeTypes the node types to add
	 */
	public void setNodeTypes(Collection<? extends Object> nodeTypes) {
		for (Object type : nodeTypes) {
			addNodeType(type);
		}
	}

	/**
	 * Sets the node types - this maps a background color/gradient to each type.
	 * 
	 * @param types
	 */
	public void setTypes(Collection<? extends Object> types) {
		setNodeTypes(types);
	}

	/**
	 * Returns the color associated with the node type, or the default background color.
	 * 
	 * @param nodeType
	 * @return the Paint
	 */
	public Paint getTypePaint(Object nodeType) {
		// if the node type is mapped to a color/gradient then return that instead
		if (nodeTypeToPaint.containsKey(nodeType)) {
			return nodeTypeToPaint.get(nodeType);
		}
		return bgPaint;
	}

	public Icon getThumbnail(final Object type, final int width, final int height) {
		return new Icon() {
			public int getIconWidth() {
				return width;
			}
			public int getIconHeight() {
				return height;
			}
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setPaint(getTypePaint(type));
				g2.fillRoundRect(x + 1, y + 1, width - 2, height - 2, 5, 5);
				g2.setPaint(borderPaint);
				g2.setStroke(new BasicStroke(1));
				g2.drawRoundRect(x + 1, y + 1, width - 2, height - 2, 5, 5);
			}
		};
	}

	/**
	 * Adds the node type and maps a color/gradient to it which is used as the background color for
	 * the node. If the node type already exists then nothing is done.
	 * 
	 * @param type the node type to add
	 */
	public void addNodeType(Object type) {
		if (!nodeTypeToPaint.containsKey(type)) {
			Color color = getNextDefaultColor();
			GradientPaint paint = new GradientPaint(0, 0, Color.white, 0, 20, color, true);
			nodeTypeToPaint.put(type, paint);
		}
	}

	public Shape getNodeShape(GraphNode node, Rectangle2D bounds) {
		return new RoundRectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), SHAPE_ARC, SHAPE_ARC);
	}

	/**
	 * Returns the background paint for the node. If the node's type has a mapped color/paint then
	 * that is returned, otherwise the default node background paint is returned.
	 */
	public Paint getBackgroundPaint(GraphNode node) {
		return getTypePaint(node.getType());
	}

	public Paint getBorderPaint(GraphNode node) {
		return (node.isMatching() ? borderMatchingPaint : (node.isSelected() ? borderSelectionPaint : (node.isHighlighted() ? borderHighlightPaint : borderPaint)));
	}

	public Stroke getBorderStroke(GraphNode node) {
		return (node.isSelected() ? borderSelectionStroke : (node.isHighlighted() ? borderHighlightStroke : borderStroke));
	}

	public Font getFont(GraphNode node) {
		return (node.isSelected() ? textSelectionFont : (node.isHighlighted() ? textHighlightFont : textFont));
	}

	public Paint getTextPaint(GraphNode node) {
		return textPaint;
	}

	public Color getTooltipTextColor() {
		return tooltipTextColor;
	}

	public Color getTooltipBackgroundColor() {
		return tooltipBackground;
	}

	public Font getTooltipFont() {
		return tooltipFont;
	}

	public Collection<Icon> getOverlayIcons(GraphNode graphNode) {
		return null;
	}

	public Icon getOverlayIcon(GraphNode graphNode) {
		return overlayIcon;
	}

	public Point2D getOverlayIconPosition(GraphNode graphNode, Icon icon) {
		return getOverlayIconPosition(graphNode);
	}

	public Point2D getOverlayIconPosition(GraphNode graphNode) {
		if (overlayIconPosition != null) {
			return overlayIconPosition;
		}

		// if no position is set, return a default position of top left
		return getTopLeft(graphNode.getBounds());
	}

	////////////////////////////
	// Utility methods
	////////////////////////////

	public static Point2D getTopLeft(Rectangle2D nodeBounds) {
		double x = minX(nodeBounds);
		double y = minY(nodeBounds);
		return new Point2D.Double(x, y);
	}

	public static Point2D getTop(Rectangle2D nodeBounds) {
		double x = midX(nodeBounds);
		double y = minY(nodeBounds);
		return new Point2D.Double(x, y);
	}

	public static Point2D getTopRight(Rectangle2D nodeBounds) {
		double x = maxX(nodeBounds);
		double y = minY(nodeBounds);
		return new Point2D.Double(x, y);
	}

	public static Point2D getLeft(Rectangle2D nodeBounds) {
		double x = minX(nodeBounds);
		double y = midY(nodeBounds);
		return new Point2D.Double(x, y);
	}

	public static Point2D getCenter(Rectangle2D nodeBounds) {
		double x = midX(nodeBounds);
		double y = midY(nodeBounds);
		return new Point2D.Double(x, y);
	}

	public static Point2D getRight(Rectangle2D nodeBounds) {
		double x = maxX(nodeBounds);
		double y = midY(nodeBounds);
		return new Point2D.Double(x, y);
	}

	public static Point2D getBottomLeft(Rectangle2D nodeBounds) {
		double x = minX(nodeBounds);
		double y = maxY(nodeBounds);
		return new Point2D.Double(x, y);
	}

	public static Point2D getBottom(Rectangle2D nodeBounds) {
		double x = midX(nodeBounds);
		double y = maxY(nodeBounds);
		return new Point2D.Double(x, y);
	}

	public static Point2D getBottomRight(Rectangle2D nodeBounds) {
		double x = maxX(nodeBounds);
		double y = maxY(nodeBounds);
		return new Point2D.Double(x, y);
	}

	private static double minX(Rectangle2D nodeBounds) {
		return nodeBounds.getX();// + getIconPadding();
	}

	private static double midX(Rectangle2D bounds) {
		return Math.max(0, bounds.getX() + (bounds.getWidth() / 2));// - (getIconWidth() / 2));
	}

	private static double maxX(Rectangle2D bounds) {
		return Math.max(0, bounds.getX() + bounds.getWidth());// - getIconWidth() - getIconPadding());
	}

	private static double minY(Rectangle2D nodeBounds) {
		return nodeBounds.getY();// + getIconPadding();
	}

	private static double midY(Rectangle2D bounds) {
		return Math.max(0, bounds.getY() + (bounds.getHeight() / 2));// - (getIconHeight() / 2));
	}

	private static double maxY(Rectangle2D bounds) {
		return Math.max(0, bounds.getY() + bounds.getHeight());// - getIconHeight() - getIconPadding());
	}
}
