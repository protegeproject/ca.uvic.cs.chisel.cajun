/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;


/**
 * Utility class for painting gradients.
 *
 * @author Chris Callendar
 * @since  2-Nov-06
 */
public class GradientPainter {

	private static final int MIN = 0;
	private static final int MAX = 3;

	/** Gradient direction from left to right. */
	public static final int LEFT_TO_RIGHT = 0;
	/** Gradient direction from top to bottom [default]. */
	public static final int TOP_TO_BOTTOM = 1;
	/** Gradient direction from top left to bottom right. */
	public static final int TOP_LEFT_TO_BOTTOM_RIGHT = 2;
	/** Gradient direction from bottom left to top right. */
	public static final int BOTTOM_LEFT_TO_TOP_RIGHT = 3;

	public static final String PROP_PAINT_CHANGED = "GradientPaintChange";

	private static final Color BG_START = new Color(123, 162, 231);
	private static final Color BG_END = new Color(99, 117, 214);

	private Color startColor = BG_START;
	private Color endColor = BG_END;
	private Color borderStartColor = Color.black;
	private Color borderEndColor = null;
	private int direction = TOP_TO_BOTTOM;
	private int borderDirection = TOP_TO_BOTTOM;
	private boolean paintBorder = true;
	private Stroke borderStroke = new BasicStroke(1);

	private boolean canPaint = false;
	private boolean useAntiAliasing = true;

	/**
	 * Initializes the panel with the default gradient colors and direction (top to bottom).
	 */
	public GradientPainter() {
		this(TOP_TO_BOTTOM);
	}

	/**
	 * Initializes the panel with the default gradient colors.
	 * @param direction the gradient direction
	 * @see GradientPanel#TOP_TO_BOTTOM
	 * @see GradientPanel#LEFT_TO_RIGHT
	 */
	public GradientPainter(int direction) {
		this(BG_START, BG_END, direction);
	}

	/**
	 * Initializes the panel with the given gradient colors and the default
	 * direction - up/down.
	 * @param start the start color (at the top)
	 * @param end the end color (at the bottom)
	 */
	public GradientPainter(Color start, Color end) {
		this(start, end, TOP_TO_BOTTOM);
	}

	/**
	 * Initializes the panel with the default gradient colors.
	 * @param start the start color (at the top or left)
	 * @param end the end color (at the bottom or right)
	 * @param direction the gradient direction
	 * @see GradientPanel#TOP_TO_BOTTOM
	 * @see GradientPanel#LEFT_TO_RIGHT
	 */
	public GradientPainter(Color start, Color end, int direction) {
		setGradientColors(start, end);
		setGradientDirection(direction);
		this.canPaint = true;
	}

	protected void fireChange() {
		if (canPaint) {
			//  notify listeners?
		}
	}

	/**
	 * Sets if antialiasing should be used to perform the painting.
	 */
	public void setAntiAliasing(boolean on) {
		this.useAntiAliasing = on;
	}

	/**
	 * @return true if antialiasing should be used to perform the painting.
	 */
	public boolean isAntiAliasing() {
		return useAntiAliasing;
	}

	/**
	 * Sets the gradient colors.
	 * @param start the start color (top or left) - can't be null.
	 * @param end the end color (right or bottom)
	 */
	public void setGradientColors(Color start, Color end) {
		boolean change = false;
		if ((start != null) && !this.startColor.equals(start)) {
			this.startColor = start;
			change = true;
		}
		if ((end != null) && !end.equals(this.endColor)) {
			this.endColor = end;
			change = true;
		} else if (end == null) {
			this.endColor = this.startColor;
		}
		if (change) {
			fireChange();
		}
	}

	/**
	 * @return the start gradient color
	 */
	public Color getStartColor() {
		return startColor;
	}

	/**
	 * @return the end gradient color
	 */
	public Color getEndColor() {
		return endColor;
	}

	/**
	 * Sets the gradient direction
	 * @param direction the direction
	 * @see GradientPanel#TOP_TO_BOTTOM
	 * @see GradientPanel#LEFT_TO_RIGHT
	 * @see GradientPanel#TOP_LEFT_TO_BOTTOM_RIGHT
	 * @see GradientPanel#BOTTOM_LEFT_TO_TOP_RIGHT
	 */
	public void setGradientDirection(int direction) {
		if (this.direction != direction) {
			this.direction = Math.max(MIN, Math.min(MAX, direction));
			fireChange();
		}
	}

	/**
	 * Gets the gradient direction.
	 * @see GradientPanel#TOP_TO_BOTTOM
	 * @see GradientPanel#LEFT_TO_RIGHT
	 */
	public int getGradientDirection() {
		return direction;
	}

	/**
	 * Sets whether the border should be painted.
	 * @param paintBorder
	 * @see GradientPainter#setBorderStroke(Stroke)
	 */
	public void setBorderPainted(boolean paintBorder) {
		if (this.paintBorder != paintBorder) {
			this.paintBorder = paintBorder;
			fireChange();
		}
	}

	/**
	 * @return true if the border is painted
	 */
	public boolean isBorderPainted() {
		return paintBorder;
	}

	/**
	 * Sets the stroke for the border.  Must set the border to be painted.
	 * @see GradientPainter#setBorderPainted(boolean)
	 * @param stroke the stroke to use for the border (can't be null)
	 */
	public void setBorderStroke(Stroke stroke) {
		if ((stroke != null) && !stroke.equals(this.borderStroke)) {
			this.borderStroke = stroke;
			fireChange();
		}
	}

	/**
	 * @return the border stroke
	 */
	public Stroke getBorderStroke() {
		return borderStroke;
	}

	/**
	 * Convenience method for setting the border to be painted,
	 * and to set the border color and stroke.
	 * @see GradientPainter#setBorderPainted(boolean)
	 * @see GradientPainter#setBorderColor(Color)
	 * @see GradientPainter#setBorderStroke(Stroke)
	 */
	public void setBorder(Color borderColor, Stroke stroke) {
		canPaint = false;
		setBorderPainted(true);
		setBorderColor(borderColor);
		setBorderStroke(stroke);
		canPaint = true;
		fireChange();
	}

	/**
	 * Sets the border color.
	 * @param c the color for the border
	 * @see GradientPainter#setBorderPainted(boolean)
	 * @see GradientPainter#setBorderStroke(Stroke)
	 */
	public void setBorderColor(Color c) {
		if (!this.borderStartColor.equals(c)) {
			this.borderStartColor = c;
			fireChange();
		}
	}

	/**
	 * @return the border color
	 */
	public Color getBorderColor() {
		return borderStartColor;
	}

	/**
	 * Sets the border colors - will be painted as a gradient.
	 * @param start the start color for the border
	 * @param end the end color for the border
	 * @param borderDirection the gradient direction
	 * @param stroke the border {@link Stroke}
	 * @see GradientPainter#setBorderPainted(boolean)
	 * @see GradientPainter#setBorderStroke(Stroke)
	 */
	public void setGradientBorder(Color start, Color end, int borderDirection, Stroke stroke) {
		canPaint = false;
		setBorderGradientColors(start, end);
		setBorderGradientDirection(borderDirection);
		setBorderStroke(stroke);
		canPaint = true;
		fireChange();
	}

	/**
	 * Sets the direction for the border gradient.
	 */
	public void setBorderGradientDirection(int borderDirection) {
		if (this.borderDirection != borderDirection) {
			this.borderDirection = borderDirection;
			fireChange();
		}
	}

	/**
	 * Sets the gradient colors for the border.
	 */
	public void setBorderGradientColors(Color start, Color end) {
		boolean change = false;
		if ((start != null) && !start.equals(borderStartColor)) {
			this.borderStartColor = start;
			change = true;
		}
		if ((end != null) && !end.equals(borderEndColor)) {
			this.borderEndColor = end;
			change = true;
		}
		if (change) {
			fireChange();
		}
	}

	public int getBorderGradientDirection() {
		return borderDirection;
	}

	public Color getBorderStartColor() {
		return borderStartColor;
	}

	public Color getBorderEndColor() {
		return borderEndColor;
	}

	/**
	 * Paints a rectangle.  First the rectangle is filled, then it's border is painted
	 * if {@link GradientPainter#isBorderPainted()} returns true.
	 */
	public void paintRect(Graphics g, int x, int y, int width, int height) {
		fillRect(g, x, y, width, height);
		if (isBorderPainted()) {
			drawRect(g, x, y, width, height);
		}
	}

	/**
	 * Paints a rectangle.  First the rectangle is filled, then it's border is painted
	 * if {@link GradientPainter#isBorderPainted()} returns true.
	 */
	public void paintRoundRect(Graphics g, int x, int y, int width, int height, int arcWidth, int arcHeight) {
		fillRoundRect(g, x, y, width, height, arcWidth, arcHeight);
		if (isBorderPainted()) {
			drawRoundRect(g, x, y, width, height, arcWidth, arcHeight);
		}
	}

	/**
	 * First the shape is filled, then if the border is painted, the shape is drawn as an outline
	 * using the border color(s) and stroke.
	 * @param shape the shape to paint
	 * @see GradientPainter#setBorderPainted(boolean)
	 */
	public void paint(Graphics g, Shape shape) {
		fill(g, shape);
		if (isBorderPainted()) {
			draw(g, shape);
		}
	}

	/**
	 * Draws the outline of a rectangle.  This is the same method that gets called when the border is painted.
	 */
	public void fillRect(Graphics g, int x, int y, int width, int height) {
		fill(g, new Rectangle2D.Float(x, y, width, height));
	}

	/**
	 * Draws the outline of a rectangle.  This is the same method that gets called when the border is painted.
	 */
	public void fillRoundRect(Graphics g, int x, int y, int width, int height, int arcWidth, int arcHeight) {
		fill(g, new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight));
	}

	/**
	 * Draws the the shape.  This is the same method that gets called when the border is painted.
	 */
	public void fill(Graphics g, Shape shape) {
		Graphics2D g2 = (Graphics2D) g;
		if (isAntiAliasing()) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		if (getEndColor() != null) {
			g2.setPaint(createPaint(shape.getBounds(), getStartColor(), getEndColor(), getGradientDirection()));
		} else {
			g2.setColor(getStartColor());
		}
		g2.fill(shape);
	}

	/**
	 * Draws a rectangle outline.
	 */
	public void drawRect(Graphics g, int x, int y, int width, int height) {
		draw(g, new Rectangle2D.Float(x, y, width, height));
	}

	/**
	 * Draws a round rectangle outline.
	 */
	public void drawRoundRect(Graphics g, int x, int y, int width, int height, int arcWidth, int arcHeight) {
		draw(g, new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight));
	}

	/**
	 * Draws the shape.
	 */
	public void draw(Graphics g, Shape shape) {
		Graphics2D g2 = (Graphics2D) g;
		if (isAntiAliasing()) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g2.setStroke(getBorderStroke());
		if (getBorderEndColor() != null) {
			g2.setPaint(createPaint(shape.getBounds(), getBorderStartColor(), getBorderEndColor(), getBorderGradientDirection()));
		} else {
			g2.setColor(getBorderColor());
		}
		g2.draw(shape);
	}

	protected GradientPaint createPaint(Rectangle bounds, Color start, Color end, int direction) {
		return createPaint(bounds.x, bounds.y, bounds.width, bounds.height, start, end, direction);
	}

	protected GradientPaint createPaint(int x, int y, int width, int height, Color start, Color end, int dir) {
		int startX = x;
		int startY = (dir == BOTTOM_LEFT_TO_TOP_RIGHT ? y + height : y);
		int endX = (dir == TOP_TO_BOTTOM ? x : x + width);
		int endY = ((dir == TOP_TO_BOTTOM) || (dir == TOP_LEFT_TO_BOTTOM_RIGHT) ? y + height : y);
		return new GradientPaint(startX, startY, start, endX, endY, end);
	}

	/**
	 * Creates a rounded top rectangle.
	 * @param x the x coordinate of the rectangle
	 * @param y the y coordinate of the rectangle
	 * @param width the width of the rectangle
	 * @param height the height of the rectangle
	 * @param radius the radius of the rounded corners
	 * @return {@link GeneralPath} shape representing a rounded top rectangle
	 */
	public static Shape createRoundedTopRectangle(int x, int y, int width, int height, int radius) {
		// height and width must be non-negative, and radius must be less than or equal to the width or height
		width = Math.max(0, width);
		height = Math.max(0, height);
		radius = Math.min(height, Math.min(width, radius));

		GeneralPath shape = new GeneralPath();
		// start the bottom left corner
		shape.moveTo(x, y + height);
		// draw the left side vertical line
		shape.lineTo(x, y + radius);
		// draw the top left rounded corner
		shape.quadTo(x, y, x + radius, y);
		// draw the top line
		shape.lineTo(x + width - radius, y);
		// draw the top right rounded corner
		shape.quadTo(x + width, y, x + width, y + radius);
		// draw right side vertical line
		shape.lineTo(x + width, y + height);
		// draw the bottom line
		shape.lineTo(x, y + height);
		// connect up
		shape.closePath();
		return shape;
	}

}
