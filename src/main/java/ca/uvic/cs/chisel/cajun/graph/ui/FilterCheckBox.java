package ca.uvic.cs.chisel.cajun.graph.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.plaf.LabelUI;

import ca.uvic.cs.chisel.cajun.util.HighlightingLabelUI;
import ca.uvic.cs.chisel.cajun.util.JIconCheckBox;

public class FilterCheckBox extends JIconCheckBox {
	private static final long serialVersionUID = 2506704022401102159L;
	
	private static final Color PANEL_COLOR = new JPanel(null).getBackground();
	private static final Color HIGHLIGHT_COLOR = Color.blue;
	private static final int ROW_SIZE = 32;

	public FilterCheckBox(final Object data, Icon icon, boolean selected) {
		super(String.valueOf(data), icon, selected);
		this.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				boolean selected = (e.getStateChange() == ItemEvent.SELECTED);
				typeVisibilityChanged(data, selected);
				updateColors();
			}
		});
		this.setBorderPainted(true);
		this.setMaximumSize(new Dimension(1000, ROW_SIZE));
		this.setOpaque(true);
		this.setBackground(PANEL_COLOR);
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
		getIconLabel().setUI(new HighlightingLabelUI(HIGHLIGHT_COLOR, true));
		updateColors();
	}
	
	public void typeVisibilityChanged(Object type, boolean visible) {
		
	}

	public void highlightLabel(String searchText) {
		LabelUI ui = getIconLabel().getUI();
		if (ui instanceof HighlightingLabelUI) {
			((HighlightingLabelUI)ui).setHighlightText(searchText);
			getIconLabel().repaint();
		}
	}

	private void updateColors() {
		setForeground(isSelected() ? Color.black : Color.gray);
	}

}
