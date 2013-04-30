package ca.uvic.cs.chisel.cajun.graph.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ca.uvic.cs.chisel.cajun.actions.CajunAction;
import ca.uvic.cs.chisel.cajun.filter.FilterChangedEvent;
import ca.uvic.cs.chisel.cajun.filter.FilterChangedListener;
import ca.uvic.cs.chisel.cajun.graph.GraphItemStyle;
import ca.uvic.cs.chisel.cajun.resources.ResourceHandler;
import ca.uvic.cs.chisel.cajun.util.GradientPainter;
import ca.uvic.cs.chisel.cajun.util.ToStringComparator;

/**
 * Base class for display the Node and Arc types and allowing the user to show/hide node and arc
 * types in the graph.
 * 
 * @author Chris
 * @since  21-Dec-07
 */
public abstract class FilterPanel extends JPanel implements FilterChangedListener {
	private static final long serialVersionUID = -8139293494944952671L;
	
	private static final String TYPE_FILTER_TEXT = "type filter text";
	private static final int ICON_HEIGHT = 15;
	private static final int ICON_WIDTH = 24;
	private static final Color BORDER_COLOR = new Color(0, 45, 150);
	private static final Color NO_FILTER_TEXT_COLOR = Color.gray;
	private static final Color FILTER_TEXT_COLOR = new Color(20, 70, 160);

	protected boolean ignoreFilterChange;

	private JPanel typesPanel;
	private JPanel headerPanel;
	private JPanel searchPanel;
	private JLabel headerLabel;
	private JButton closeButton;

	private JTextField searchTextField;

	private String title;
	private Icon icon;
	private GraphItemStyle style;

	public FilterPanel(String title, Icon icon, GraphItemStyle style) {
		super(new BorderLayout());
		this.title = title;
		this.icon = icon;
		this.style = style;
		this.ignoreFilterChange = false;

		initialize();

		reload();
	}
	
	public GraphItemStyle getStyle() {
		return style;
	}
	
	public void setStyle(GraphItemStyle style) {
		this.style = style;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		getHeaderLabel().setText(title.trim() + "  ");
	}

	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
		getHeaderLabel().setIcon(icon);
	}

	private void initialize() {
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		JPanel holder = new JPanel(new BorderLayout());
		holder.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		holder.add(getTypesPanel(), BorderLayout.NORTH);
		holder.setBackground(Color.white);
		add(getHeaderPanel(), BorderLayout.NORTH);

		JPanel centrePanel = new JPanel(new BorderLayout());
		centrePanel.add(getSearchPanel(), BorderLayout.NORTH);

		JScrollPane scroll = new JScrollPane(holder, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		centrePanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		centrePanel.add(scroll, BorderLayout.CENTER);

		add(centrePanel, BorderLayout.CENTER);
	}

	/**
	 * Filters the types to only display items matching the searchText.
	 * 
	 * @param searchText
	 */
	private void filterTypes(String searchText) {
		Map<Object, Boolean> types = getTypes();
		if (searchText.length() == 0 || (searchText.length() == 1 && searchText.equals("*"))) {
			loadTypes(types);
		} else {
			Map<Object, Boolean> validTypes = new HashMap<Object, Boolean>();
			boolean beginsWith = true;
			searchText = searchText.toLowerCase();
			if (searchText.charAt(searchText.length() - 1) == '*') {
				searchText = searchText.substring(0, searchText.length() - 1);
			} else if (searchText.charAt(0) == '*') {
				searchText = searchText.substring(1, searchText.length());
				beginsWith = false;
			}

			for (Object object : types.keySet()) {
				if (beginsWith && object.toString().toLowerCase().startsWith(searchText)) {
					validTypes.put(object, types.get(object));
				} else if (!beginsWith && object.toString().toLowerCase().contains(searchText)) {
					validTypes.put(object, types.get(object));
				}
			}
			loadTypes(validTypes);
			highlightTypes(searchText);
		}
	}

	private JPanel getSearchPanel() {
		if (searchPanel == null) {
			searchPanel = new JPanel();
			searchTextField = new JTextField(TYPE_FILTER_TEXT);
			searchTextField.setForeground(NO_FILTER_TEXT_COLOR);
			searchTextField.setPreferredSize(new Dimension(150, 20));
			searchTextField.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					if (searchTextField.getText().equals(TYPE_FILTER_TEXT)) {
						searchTextField.setText("");
						searchTextField.setForeground(FILTER_TEXT_COLOR);
					}
				}

				public void focusLost(FocusEvent e) {
					if (searchTextField.getText().length() == 0) {
						searchTextField.setText(TYPE_FILTER_TEXT);
						searchTextField.setForeground(NO_FILTER_TEXT_COLOR);
					}
				}
			});

			searchTextField.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					filterTypes(searchTextField.getText());
				}
			});

			searchPanel.add(searchTextField);
		}

		return searchPanel;
	}

	private JPanel getHeaderPanel() {
		if (headerPanel == null) {
			final GradientPainter painter = new GradientPainter(new Color(89, 135, 214), new Color(14, 66, 156));
			painter.setBorder(BORDER_COLOR, new BasicStroke(1));
			headerPanel = new JPanel(new BorderLayout()) {
				private static final long serialVersionUID = 329558692812327877L;

				protected void paintComponent(Graphics g) {
					Shape shape = GradientPainter.createRoundedTopRectangle(0, 0, this.getWidth() - 1, this.getHeight() - 1, 8);
					painter.paint(g, shape);
				}
			};
			headerPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
			headerPanel.add(getHeaderLabel(), BorderLayout.CENTER);
			headerPanel.add(getCloseButton(), BorderLayout.EAST);
		}
		return headerPanel;
	}

	public JButton getCloseButton() {
		if (closeButton == null) {
			closeButton = new JButton(new CajunAction() {
				private static final long serialVersionUID = -9047289454457835078L;

				public void doAction() {
					Container parent = FilterPanel.this.getParent();
					if (parent != null) {
						parent.remove(FilterPanel.this);
					}
				}
			});
			closeButton.setText(null);
			closeButton.setToolTipText("Close this panel");
			closeButton.setPreferredSize(new Dimension(14, 14));
			closeButton.setOpaque(false);
			closeButton.setBorder(null);
			closeButton.setFocusPainted(false);
			closeButton.setContentAreaFilled(false);
			closeButton.setIcon(ResourceHandler.getIcon("icon_close.gif"));
			closeButton.setRolloverIcon(ResourceHandler.getIcon("icon_close_over.gif"));
			closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		return closeButton;
	}

	private JLabel getHeaderLabel() {
		if (headerLabel == null) {
			headerLabel = new JLabel(title.trim() + "  ", icon, JLabel.LEADING);
			headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 14f));
			headerLabel.setForeground(Color.white);
		}
		return headerLabel;
	}

	private JPanel getTypesPanel() {
		if (typesPanel == null) {
			typesPanel = new JPanel(new GridLayout(0, 1, 0, 1));
			typesPanel.setOpaque(false);
		}
		return typesPanel;
	}

	/**
	 * Reloads the node/arc types.
	 */
	public void reload() {
		searchTextField.setText(TYPE_FILTER_TEXT);
		loadTypes(getTypes());
	}

	public void loadTypes(Map<Object, Boolean> items) {
		getTypesPanel().removeAll();
		if (items.size() > 0) {
			// sort the types alphabetically
			TreeSet<Object> sortedTypes = new TreeSet<Object>(new ToStringComparator());
			sortedTypes.addAll(items.keySet());
			for (Object type : sortedTypes) {
				boolean selected = items.get(type);
				FilterCheckBox checkbox = new FilterCheckBox(type, null, selected) {
					private static final long serialVersionUID = -861175558062891232L;

					public void typeVisibilityChanged(Object type, boolean visible) {
						FilterPanel.this.typeVisibilityChanged(type, visible);
					}
				};

				// create an icon for this type - it will be a rounded rectangle
				// filled with the same color/paint as the node or arc type
				Icon icon = style.getThumbnail(type, ICON_WIDTH, ICON_HEIGHT);
				checkbox.setIcon(icon);

				getTypesPanel().add(checkbox);
			}
		}
		this.invalidate();
		this.validate();
		this.repaint();
	}
	
	public void highlightTypes(String searchText) {
		for (Component comp : getTypesPanel().getComponents()) {
			FilterCheckBox checkbox = (FilterCheckBox) comp;
			checkbox.highlightLabel(searchText);
		}
		this.invalidate();
		this.validate();
		this.repaint();
	}

	/**
	 * Notifies the FilterManager that the visibility has changed on a node or arc type
	 * 
	 * @param type the node or arc type
	 * @param visible if it is visible
	 */
	protected void typeVisibilityChanged(Object type, boolean visible) {
		ignoreFilterChange = true;
		// this will fire a filter change event - we don't want to update our list
		setTypeVisibility(type, visible);
		ignoreFilterChange = false;
	}

	public abstract void setTypeVisibility(Object type, boolean visible);

	/**
	 * @return the node or arc types.
	 */
	public abstract Map<Object, Boolean> getTypes();

	/**
	 * This gets called when the filters changed (e.g. the node or arc types have changed).
	 */
	public void filtersChanged(FilterChangedEvent fce) {
		if (!ignoreFilterChange) {
			// do we need to reload here?
			// I think it causes a weird problem where the node or arc panel checkboxes don't respond...
			//reload();
		}
	}

	

}
