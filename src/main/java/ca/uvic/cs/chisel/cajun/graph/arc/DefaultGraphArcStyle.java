package ca.uvic.cs.chisel.cajun.graph.arc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import ca.uvic.cs.chisel.cajun.util.GraphicsUtils;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

/**
 * Contains the default colors and strokes for arcs.
 * 
 * @author Chris
 * @since  8-Nov-07
 */
public class DefaultGraphArcStyle implements GraphArcStyle {

	private static final float THICK_STROKE_WIDTH = 5f;
	private static final float MEDIUM_STROKE_WIDTH = 3f;
	private static final float THIN_STROKE_WIDTH = 1f;

	protected Color tooltipBackground;
	protected Color tooltipTextColor;

	protected Font tooltipFont;

	protected Color[] defaultColors;
	protected int nextColorIndex;
	protected Color defaultArcColor;
	protected Map<Object, Paint> arcTypeToColor;

	private boolean isDashed = false;
	
	private int capSquare;
	private float dashWidth;

	public DefaultGraphArcStyle() {
		loadDefaultColors();
		this.nextColorIndex = 0;
		this.arcTypeToColor = new HashMap<Object, Paint>();
		this.defaultArcColor = getNextDefaultColor(); // new Color(64, 64, 128)
		
		this.dashWidth = 10f;
		this.capSquare = BasicStroke.CAP_SQUARE;

		tooltipBackground = defaultArcColor;
		tooltipTextColor = GraphicsUtils.getTextColor(tooltipBackground);

		tooltipFont = PText.DEFAULT_FONT;
	}

	protected void loadDefaultColors() {
		this.defaultColors = new Color[24];
		// mostly darker colors
		defaultColors[0] =  new Color(205,  92,  92); //indianred
		defaultColors[1] =  new Color( 70, 130, 180); //steelblue
		defaultColors[2] =  new Color(186,  85, 211); //mediumorchid
		defaultColors[3] =  new Color(210, 105,  30); //chocolate
		defaultColors[4] =  new Color(255, 215,   0); //gold
		defaultColors[5] =  new Color(205, 133,  63); //peru
		defaultColors[6] =  new Color(128, 128, 128); //grey
		defaultColors[7] =  new Color( 50, 205,  50); //limegreen
		defaultColors[8] =  new Color(148,   0, 211); //darkviolet
		defaultColors[9] =  new Color(128, 128,   0); //olive
		defaultColors[10] = new Color(189, 183, 107); //darkkahki
		defaultColors[11] = new Color(255, 140,   0); //darkorange
		defaultColors[12] = new Color(  0, 102,   0); //darkgreen
		defaultColors[13] = new Color(102, 102, 255); //blue-purple
		defaultColors[14] = new Color(255,   0, 102); //pink-red
		defaultColors[15] = new Color(  0, 153, 153); //turquoise
		defaultColors[16] = new Color( 51,  51,  51); //darkgray
		defaultColors[17] = new Color( 60, 179, 113); //medium sea green
		defaultColors[18] = new Color(153,  51,   0); //dark red-orange
		defaultColors[19] = new Color(  0, 204, 255); //cyany
		defaultColors[20] = new Color(153, 102,   0); //bronze
		defaultColors[21] = new Color(212, 255,   1); //yellowgreen
		defaultColors[22] = new Color(  0,  51, 102); //navy
		defaultColors[23] = new Color(153,   0,   0); //darkred
	}

	private Color getNextDefaultColor() {
		if (nextColorIndex >= defaultColors.length) {
			nextColorIndex = 0;
		}
		Color color = defaultColors[nextColorIndex];
		nextColorIndex++;
		return color;
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
				Paint paint = getTypePaint(type);
				g2.setPaint(paint);
				g2.setStroke(new BasicStroke(3));
				int midY = y + (height / 2);
				g2.drawLine(x + 2, midY, x + width - 4, midY);
			}
		};
	}
	
	/**
	 * Sets the arc types - maps a color to each arc type.
	 */
	public void setTypes(Collection<? extends Object> types) {
		setArcTypes(types);
	}

	/**
	 * Returns the color for the given type, or the default arc color.
	 */
	public Paint getTypePaint(Object type) {
		if ((type != null) && arcTypeToColor.containsKey(type)) {
			return arcTypeToColor.get(type);
		}
		return defaultArcColor;
	}
	
	/**
	 * Sets the arc types - this is done to map a background color/gradient to each arc type.
	 * @param arcTypes the node types to add
	 */
	public void setArcTypes(Collection<? extends Object> arcTypes) {
		for (Object type : arcTypes) {
			addArcType(type);
		}
	}

	/**
	 * Adds the arc type and maps a color/gradient to it which is used as the line color for the
	 * arc. If the arc type already exists then nothing is done.
	 * 
	 * @param type the node type to add
	 */
	public void addArcType(Object type) {
		if (!arcTypeToColor.containsKey(type)) {
			Color color = getNextDefaultColor();
			arcTypeToColor.put(type, color);
		}
	}

	/**
	 * Returns the paint to use for the arc. Checks if the arc type has a color mapped to it, if so
	 * that color/paint is returned. Otherwise the default arc color is returned.
	 * @param arc the arc whose paint/color will be returned
	 * @return the paint or color for the arc
	 */
	public Paint getPaint(GraphArc arc) {
		return getTypePaint(arc.getType());
	}

	public Stroke getStroke(GraphArc arc) {
		float width = (arc.isSelected() && arc.isHighlighted() ? THICK_STROKE_WIDTH : (arc.isHighlighted() || arc.isSelected() ? MEDIUM_STROKE_WIDTH : THIN_STROKE_WIDTH));
		return createStroke(width, isDashed);
	}

	public Color getTooltipBackgroundColor() {
		return tooltipBackground;
	}

	public Color getTooltipTextColor() {
		return tooltipTextColor;
	}

	public Font getTooltipFont() {
		return tooltipFont;
	}
	
	public void setDashedCapSquare(int capSquare) {
		this.capSquare = capSquare;
	}
	
	public void setDashWidth(float dashWidth) {
		this.dashWidth = dashWidth;
	}

	protected Stroke createStroke(float strokeWidth, boolean dashed) {
		// any point in caching these instead of creating them every time a paint occurs?
		Stroke stroke;
		if (dashed) {
			float spaceWidth = 3.0f + strokeWidth;
			//float absoluteDashWidth = 10f;
			float absoluteSpaceWidth = spaceWidth;
			float[] dash = { dashWidth, absoluteSpaceWidth };
			try {
				stroke = new PFixedWidthStroke(strokeWidth, capSquare, BasicStroke.JOIN_ROUND, 1.0f, dash, 0);
			} catch (RuntimeException e) {
				// to catch any problems with creating a dashed stroke
				stroke = new BasicStroke(strokeWidth);
			}
		} else {
			stroke = new PFixedWidthStroke(strokeWidth);
		}
		return stroke;
	}

	public void setDashed(boolean isDashed) {
		this.isDashed = isDashed;
	}

	public boolean isDashed() {
		return isDashed;
	}
}
