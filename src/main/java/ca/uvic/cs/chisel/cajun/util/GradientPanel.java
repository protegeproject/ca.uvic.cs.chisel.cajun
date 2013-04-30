/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JPanel;


/**
 * Extension of {@link JPanel} to paint a gradient background.
 * For customize the gradient call {@link GradientPanel#getPainter()} and configure the painter.
 *
 * @see    GradientPainter
 * @author Chris Callendar
 * @since  1-Nov-06
 */
public class GradientPanel extends JPanel {

	/** Gradient direction from left to right. */
	public static final int LEFT_TO_RIGHT = GradientPainter.LEFT_TO_RIGHT;
	/** Gradient direction from top to bottom [default]. */
	public static final int TOP_TO_BOTTOM = GradientPainter.TOP_TO_BOTTOM;
	/** Gradient direction from top left to bottom right. */
	public static final int TOP_LEFT_TO_BOTTOM_RIGHT = GradientPainter.TOP_LEFT_TO_BOTTOM_RIGHT;
	/** Gradient direction from bottom left to top right. */
	public static final int BOTTOM_LEFT_TO_TOP_RIGHT = GradientPainter.BOTTOM_LEFT_TO_TOP_RIGHT;

	public static final Color BG_START = new Color(123, 162, 231);
	public static final Color BG_END = new Color(99, 117, 214);

	private final GradientPainter painter = new GradientPainter();

	/**
	 * Initializes the panel with the default gradient colors and direction (top to bottom).
	 */
	public GradientPanel() {
		this(TOP_TO_BOTTOM);
	}

	/**
	 * Initializes the panel with the default gradient colors.
	 * @param direction the gradient direction
	 * @see GradientPanel#TOP_TO_BOTTOM
	 * @see GradientPanel#LEFT_TO_RIGHT
	 * @see GradientPanel#TOP_LEFT_TO_BOTTOM_RIGHT
	 * @see GradientPanel#BOTTOM_LEFT_TO_TOP_RIGHT
	 */
	public GradientPanel(int direction) {
		this(BG_START, BG_END, direction);
	}

	/**
	 * Initializes the panel with the given gradient colors and the default
	 * direction - up/down.
	 * @param start the start color (at the top)
	 * @param end the end color (at the bottom)
	 */
	public GradientPanel(Color start, Color end) {
		this(start, end, TOP_TO_BOTTOM);
	}

	/**
	 * Initializes the panel with the default gradient colors.
	 * @param start the start color (at the top or left)
	 * @param end the end color (at the bottom or right)
	 * @param direction the gradient direction
	 * @see GradientPanel#TOP_TO_BOTTOM
	 * @see GradientPanel#LEFT_TO_RIGHT
	 * @see GradientPanel#TOP_LEFT_TO_BOTTOM_RIGHT
	 * @see GradientPanel#BOTTOM_LEFT_TO_TOP_RIGHT
	 */
	public GradientPanel(Color start, Color end, int direction) {
		super();
		initialize(start, end, direction);
	}

	public GradientPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		initialize(BG_START, BG_END, TOP_TO_BOTTOM);
	}

	public GradientPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		initialize(BG_START, BG_END, TOP_TO_BOTTOM);
	}

	public GradientPanel(LayoutManager layout) {
		super(layout);
		initialize(BG_START, BG_END, TOP_TO_BOTTOM);
	}

	private void initialize(Color start, Color end, int direction) {
		painter.setBorderPainted(false);
		painter.setGradientColors(start, end);
		painter.setGradientDirection(direction);
	}

	/**
	 * Gets the {@link GradientPainter}.  This object can be used
	 * to change more properties about the gradient.
	 */
	public GradientPainter getPainter() {
		return painter;
	}

	public Color getStartColor() {
		return painter.getStartColor();
	}

	public Color getEndColor() {
		return painter.getEndColor();
	}

	public void setGradientColors(Color start, Color end) {
		painter.setGradientColors(start, end);
		setBackground(start);	// repaints
	}

	public void setEmptyBorder(int padding) {
		setEmptyBorder(padding, padding, padding, padding);
	}

	public void setEmptyBorder(int top, int left, int bottom, int right) {
		setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		painter.paintRect(g, 0, 0, getWidth(), getHeight());
	}

}
