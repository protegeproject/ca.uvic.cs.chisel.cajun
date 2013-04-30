package ca.uvic.cs.chisel.cajun.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicLabelUI;

public class HighlightingLabelUI extends BasicLabelUI {

	public static Color DEFAULT_COLOR = Color.blue;
	public static boolean DEFAULT_IGNORE_CASE = true;
	public static boolean DEFAULT_UNDERLINE = true;
	public static boolean DEFAULT_HIGHLIGHT_DISABLED_TEXT = false;
	
	private String highlightText;
	private Color highlightColor;
	//private boolean firstOccuranceOnly;
	private boolean highlightDisabledText;
	private boolean ignoreCase;
	private boolean underline;
	
	public HighlightingLabelUI() {
		this(DEFAULT_COLOR);
	}
	
	public HighlightingLabelUI(Color color) {
		this(color, DEFAULT_IGNORE_CASE);
	}
	
	public HighlightingLabelUI(Color color, boolean ignoreCase) {
		this(color, ignoreCase, DEFAULT_UNDERLINE);
	}
	
	public HighlightingLabelUI(Color color, boolean ignoreCase, boolean underline) {
		this(color, ignoreCase, underline, DEFAULT_HIGHLIGHT_DISABLED_TEXT);
	}
	
	public HighlightingLabelUI(Color color, boolean ignoreCase, boolean underline, boolean highlightDisabledText) {
		this.highlightText = "";
		this.highlightColor = color;
		this.ignoreCase = ignoreCase;
		this.underline = underline;
		this.highlightDisabledText = highlightDisabledText;
		//this.firstOccuranceOnly = true;
	}
	
	@Override
	protected void paintDisabledText(JLabel l, Graphics g, String s, int textX, int textY) {
		if (isHighlightDisabledText()) {
			paintHighlightedText(l, g, s, textX, textY, true);
		} else {
			super.paintDisabledText(l, g, s, textX, textY);
		}
	}
	
	@Override
	protected void paintEnabledText(JLabel l, Graphics g, String s, int textX, int textY) {
		//super.paintEnabledText(l, g, s, textX, textY);
		paintHighlightedText(l, g, s, textX, textY, false);
	}
	
	protected void paintHighlightedText(JLabel l, Graphics g, String s, int textX, int textY, boolean disabled) {
		boolean painted = false;
		Color normalColor = l.getForeground();
		String text = getHighlightText();
		if ((text.length() > 0) && !getHighlightColor().equals(normalColor)) {
			int index = -1;
			if (isIgnoreCase()) {
				index = s.toLowerCase().indexOf(text.toLowerCase());
			} else {
				index = s.indexOf(text);
			}
			if (index != -1) {
				String before = s.substring(0, index);
				String match = s.substring(index, index + text.length());
				String after = s.substring(index + text.length());
				
				// paint the text before the matching text
				if (before.length() > 0) {
					paintText(l, g, before, textX, textY, false, disabled);
					// Remove dependency on SwingUtilities2 (its package varies on different platforms)
					textX += SwingUtilities.computeStringWidth(g.getFontMetrics(), before);
				}

				// paint the matching text
				paintText(l, g, match, textX, textY, true, disabled);
				textX += SwingUtilities.computeStringWidth(g.getFontMetrics(), match);
				
				// paint the text after the matching text
				if (after.length() > 0) {
					paintText(l, g, after, textX, textY, false, disabled);
				}
				
				painted = true;
			}
		}
		if (!painted) {
			if (disabled) {
				super.paintDisabledText(l, g, s, textX, textY);
			} else {
				super.paintEnabledText(l, g, s, textX, textY);
			}
		}
	}
	
	/**
	 * Paints the text at the given location.
	 * The color is determined by if the highlight and disabled parameters.
	 * If the highlight parameter is true, then the highlight color is used.
	 * Otherwise the color will be the same as what the {@link BasicLabelUI} class uses.
	 * @param highlight if true the text will be painted using the highlight color
	 * @param disabled if true (and highlight is false) then the text will be painted in the disabled colors
	 */
    protected void paintText(JLabel l, Graphics g, String s, int textX, int textY, boolean highlight, boolean disabled) {
    	if (disabled) {
            Color color = (highlight ? getHighlightColor() : l.getBackground());
            JLabel temp = new JLabel();
            temp.setBackground(color);
            super.paintDisabledText(temp, g, s, textX, textY);
            
            /* Remove dependency on SwingUtilities2
            g.setColor(color.brighter());
            SwingUtilities2.drawString(l, g, s, textX + 1, textY + 1);
            g.setColor(color.darker());
            SwingUtilities2.drawString(l, g, s, textX, textY);
            */
    	} else {
            Color color = (highlight ? getHighlightColor() : l.getForeground());
            JLabel temp = new JLabel();
            temp.setForeground(color);
            super.paintEnabledText(temp, g, s, textX, textY);
            
            /* Remove dependency on SwingUtilities2
    		g.setColor(color);
    		SwingUtilities2.drawString(l, g, s, textX, textY);
    		*/
            
    		// underline the matching text?
    		if (highlight && isUnderline()) {
        		int width = SwingUtilities.computeStringWidth(g.getFontMetrics(), s);
        		((Graphics2D)g).setStroke(new BasicStroke(1f));
				g.drawLine(textX, textY + 2, textX + width - 1, textY + 2);
    		}
    	}
    }
	
    /**
     * Sets the highlight color.
     * @param highlightColor
     */
	public void setHighlightColor(Color highlightColor) {
		if (highlightColor != null) {
			this.highlightColor = highlightColor;
		}
	}
	
	public Color getHighlightColor() {
		return highlightColor;
	}
	
	/**
	 * Sets whether to highlight disabled text.
	 * @param highlightDisabledText
	 */
	public void setHighlightDisabledText(boolean highlightDisabledText) {
		this.highlightDisabledText = highlightDisabledText;
	}
	
	public boolean isHighlightDisabledText() {
		return highlightDisabledText;
	}
	
	/**
	 * Sets the string to highlight
	 * @param highlightText
	 */
	public void setHighlightText(String highlightText) {
		this.highlightText = (highlightText != null ? highlightText : "");
	}
	
	/**
	 * @return the string to highlight
	 */
	public String getHighlightText() {
		return highlightText;
	}
	
	/**
	 * Sets whether to ignore case when trying to match the string.
	 * @param ignoreCase
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	
	public boolean isIgnoreCase() {
		return ignoreCase;
	}
	
	/**
	 * Sets whether the highlighted text should be underlined as well as highlighted.
	 * @param underline
	 */
	public void setUnderline(boolean underline) {
		this.underline = underline;
	}
	
	/**
	 * @return true if the highlighted text will be underlined
	 */
	public boolean isUnderline() {
		return underline;
	}

//	public void setFirstOccuranceOnly(boolean firstOccuranceOnly) {
//		this.firstOccuranceOnly = firstOccuranceOnly;
//	}
//
//	public boolean isFirstOccuranceOnly() {
//		return firstOccuranceOnly;
//	}
	
}
