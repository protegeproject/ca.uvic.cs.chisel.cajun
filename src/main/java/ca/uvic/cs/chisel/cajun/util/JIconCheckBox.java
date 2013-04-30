package ca.uvic.cs.chisel.cajun.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 * Renders an Icon as well as the checkbox.
 * Only works when the checkbox is on the left.
 *
 * @author Chris
 * @since  20-Dec-07
 */
public class JIconCheckBox extends JCheckBox {

	private static final int CHECKBOX_SIZE = 18;
	private JLabel label;
	
	public JIconCheckBox() {
		super();
		initialize();
	}

	public JIconCheckBox(Icon icon) {
		super(icon);
		initialize();
	}

	public JIconCheckBox(String text) {
		super(text);
		initialize();
	}

	public JIconCheckBox(Action a) {
		super(a);
		initialize();
	}

	public JIconCheckBox(Icon icon, boolean selected) {
		super(icon, selected);
		initialize();
	}

	public JIconCheckBox(String text, boolean selected) {
		super(text, selected);
		initialize();
	}

	public JIconCheckBox(String text, Icon icon) {
		super(text, icon);
		initialize();
	}

	public JIconCheckBox(String text, Icon icon, boolean selected) {
		super(text, icon, selected);
		initialize();
	}

	private void initialize() {
		setOpaque(false);
		setLayout(new BorderLayout());
		add(getIconLabel(), BorderLayout.CENTER);
	}
	
	public JLabel getIconLabel() {
		if (label == null) {
			label = new JLabel();
			label.setBorder(BorderFactory.createEmptyBorder(0, CHECKBOX_SIZE, 0, 0));
		}
		return label;
	}

	@Override
	public void setIcon(Icon defaultIcon) {
		getIconLabel().setIcon(defaultIcon);
	}

	@Override
	public Icon getIcon() {
		return null;
	}
	
	public Icon getLabelIcon() {
		return getIconLabel().getIcon();
	}
	
	@Override
	public void setText(String text) {
		getIconLabel().setText(text);
	}
	
	@Override
	public String getText() {
		return null;
	}
	
	public String getLabelText() {
		return getIconLabel().getText();
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		getIconLabel().setForeground(fg);
	}
	
	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		getIconLabel().setBackground(bg);
	}
	
	@Override
	public void setFont(Font font) {
		super.setFont(font);
		getIconLabel().setFont(font);
	}
	
}
