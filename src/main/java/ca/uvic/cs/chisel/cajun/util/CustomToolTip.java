/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.util;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JToolTip;


/**
 * Inspired by "Multi-line ToolTip" artical by Zafir Anjum
 * at http://www.codeguru.com/java/articles/122.shtml
 *
 * @author Chris Callendar
 */
public class CustomToolTip extends JToolTip {

	public CustomToolTip() {
		this(Color.black, Color.white, null);
	}

	public CustomToolTip(Color fgColor, Color bgColor, Font font) {
		updateUI();
		setForeground(fgColor);
		setBackground(bgColor);
		if (font != null) {
			setFont(font);
		}
		
		int sum = bgColor.getRed() + bgColor.getGreen() + bgColor.getBlue();
		Color borderColor = (sum > 400 ? bgColor.darker() : bgColor.brighter());
		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(borderColor, 1),
						BorderFactory.createEmptyBorder(5, 2, 5, 2)));
	}

	public void updateUI() {
		setUI(SwingMultiLineToolTipUI.createUI(this));
	}

}

