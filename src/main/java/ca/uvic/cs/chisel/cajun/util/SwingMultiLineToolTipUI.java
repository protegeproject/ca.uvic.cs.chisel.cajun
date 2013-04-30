/*
 * Modified from "JFC Unleashed", by Mike Foley and Mark McCulley
 * http://mail.phys-iasi.ro/Library/Computing/jfc_unleashed/
 */
package ca.uvic.cs.chisel.cajun.util;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

/**
 * The SwingMultiLineToolTipUI class may be registered with the UIManager
 * to replace the ToolTipUI for JToolTip instances. When used, it
 * divides the ToolTip into multiple lines. Each line is divided by
 * the '\ n' character.
 * <p>
 * @author Mike Foley
 **/
public class SwingMultiLineToolTipUI extends BasicToolTipUI {

	public static Rectangle rect;

	public static boolean processTabs =false;

	/**
	 * The single shared UI instance.
	 **/
	static SwingMultiLineToolTipUI sharedInstance = new SwingMultiLineToolTipUI();

	/**
	 * The margin around the text.
	 **/
	static int margin = 2;

	/**
	 * The character to use in the StringTokenizer to
	 * separate lines in the ToolTip. This could be the
	 * system property end of line character.
	 **/
	static final String lineSeparator = "\n";

	/**
	 * The character translated into tabs
	 **/
	static final String tabCharacter = "\t";

	/**
	 * The size of tab
	 **/
	static public int tab = 75;

	/**
	 * Color of box surrounding the tooltip
	 */
	//public static Color backgroundColor = Color.WHITE;

	/**
	 * Color of the tooltip text
	 */
	//public static Color foregroundColor = Color.BLACK;

	/**
	 * SwingMultiLineToolTipUI, constructor.
	 * <p>
	 * Have the constructor be protected so we can be subclassed,
	 * but not created by client classes.
	 **/
	protected SwingMultiLineToolTipUI() {
		super();

	}

	/**
	 * Create the UI component for the given component.
	 * The same UI can be shared for all components, so
	 * return our shared instance.
	 * <p>
	 * @param c The component to create the UI for.
	 * @return Our shared UI component instance.
	 **/
	public static ComponentUI createUI(JComponent c) {
		return sharedInstance;
	}

	/**
	 * Paint the ToolTip. Use the current font and colors
	 * set for the given component.
	 * <p>
	 * @param g The graphics to paint with.
	 * @param c The component to paint.
	 **/
	public void paint(Graphics g, JComponent c) {

		//
		// Determine the size for each row.
		//

		Font font = c.getFont();
		FontMetrics fontMetrics = c.getFontMetrics(font);
		int fontHeight = fontMetrics.getHeight();
		int fontAscent = fontMetrics.getAscent();

		//
		// Paint the background in the tip color.
		//
		//g.setColor(c.getBackground());
		//Dimension size = c.getSize();
		//g.fillRect(0, 0, size.width, size.height);

		//
		// Paint each line in the tip using the
		// foreground color. Use a StringTokenizer
		// to parse the ToolTip. Each line is then printed
		// out character by character with tabs being 
		// properly interpretted.
		//

		g.setColor(c.getForeground());
		int y = margin + fontAscent;
		String tipText = ((JToolTip) c).getTipText();
		StringTokenizer tokenizer = new StringTokenizer(tipText, lineSeparator);
		int numberOfLines = tokenizer.countTokens();

		for (int i = 0; i < numberOfLines; i++) {

			String line = tokenizer.nextToken();

			int position = 0;
			
			//print out each character
			for (int j = 0; j < line.length(); j++) {
				String s = line.substring(j, j + 1);
				if (s.equalsIgnoreCase(tabCharacter)) {
					int addition = tab - (position % tab);
					if (addition == 0)
						addition = tab;
					position += addition;

				} else {
					g.drawString(s, margin + position, y);
					position += fontMetrics.stringWidth(s);
				}
			}
			
			y += fontHeight;
		}

	} // paint

	/**
	 * The preferred size for the ToolTip is the width of
	 * the longest row in the tip, and the height of a
	 * single row times the number of rows in the tip.
	 * 
	 * @param c The component whose size is needed.
	 * @return The preferred size for the component.
	 **/

	public Dimension getPreferredSize(JComponent c) {

		//
		// Determine the size for each row.
		//        

		Font font = c.getFont();
		FontMetrics fontMetrics = c.getFontMetrics(font);
		int fontHeight = fontMetrics.getHeight();

		//
		// Get the tip text string.
		//

		String tipText = ((JToolTip) c).getTipText();

		//
		// Empty tip, use a default size.
		//

		if (tipText == null)
			return new Dimension(2 * margin, 2 * margin);

		//
		// Create a StringTokenizer to parse the ToolTip.
		//

		StringTokenizer tokenizer = new StringTokenizer(tipText, lineSeparator);

		int numberOfLines = tokenizer.countTokens();

		//
		// Height is number of lines times height of a single line.
		//

		int height = numberOfLines * fontHeight;

		//
		// Width is width of longest single line.
		//

		int width = 0;

		for (int i = 0; i < numberOfLines; i++) {
			String line = tokenizer.nextToken();			
			int position=0;
			
			//print out each character
			for (int j = 0; j < line.length(); j++) {
				String s = line.substring(j, j + 1);
				if (s.equalsIgnoreCase(tabCharacter)) {
					int addition = tab - (position % tab);
					if (addition == 0)
						addition = tab;
					position += addition;

				} else {
					position += fontMetrics.stringWidth(s);
				}
			}
			
			width = Math.max(width, position);
		}

		//
		// Add the margin to the size, and return.
		//

		return (new Dimension(width + 2 * margin, height + 2 * margin));
	} // getPreferredSize

} // SwingMultiLineToolTipUI
