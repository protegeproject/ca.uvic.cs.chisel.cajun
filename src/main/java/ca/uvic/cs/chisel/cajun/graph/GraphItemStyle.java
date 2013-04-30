package ca.uvic.cs.chisel.cajun.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.util.Collection;

import javax.swing.Icon;

public interface GraphItemStyle {

	/**
	 * Sets the node/arc types - this maps a color/gradient to each type.
	 * @param types
	 */
	public void setTypes(Collection<? extends Object> types);
	
	/**
	 * Returns the paint/color for the given type, or a default color.
	 * @param type
	 * @return the paint for the type, or a default color.
	 */
	public Paint getTypePaint(Object type);
	
	/**
	 * Returns a thumbnail icon of the graph item.
	 * @param type the graph item type
	 * @param width the width of the thumbnail
	 * @param height the height of the thumbnail
	 * @return the thumbnail icon
	 */
	public Icon getThumbnail(Object type, int width, int height);
	
	/**
	 * Returns the background color for the arc's tooltip.
	 * @return {@link Color}
	 */
	public Color getTooltipBackgroundColor();
	
	/**
	 * Returns the text color for the arc's tooltip.
	 * @return {@link Color}
	 */
	public Color getTooltipTextColor();
	
	/**
	 * Returns the font for the arc's tooltip.
	 * @return {@link Font}
	 */
	public Font getTooltipFont();
	
}
