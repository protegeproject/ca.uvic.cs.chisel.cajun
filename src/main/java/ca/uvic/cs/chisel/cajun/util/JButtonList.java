/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.ItemSelectable;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;

/**
 * The {@link JButtonList} class extends {@link JList} to allow {@link AbstractButton}
 * objects instead of the default {@link JLabel} objects.
 * This class was originally designed to only use {@link JCheckBox} objects, but has now
 * been extended to support any kind of {@link AbstractButton} object.
 * <br>
 * If the model contains {@link AbstractButton} objects, then those are rendered.  Otherwise, an AbstractButton will
 * be created for each object.  You can choose which type of button to use: {@link JCheckBox}, {@link JRadioButton},
 * {@link JToggleButton}, {@link JCheckBoxMenuItem}, etc using the {@link JButtonList#setButtonClass(Class)} method.
 * <br>
 * If you want to allow only one button to be selected at a time, call the {@link JButtonList#setRadioButtonMode(boolean)}.
 *
 * @author Chris Callendar
 */
public class JButtonList extends JList implements MouseListener, KeyListener,
	ListDataListener, ListSelectionListener, ItemListener, PopupMenuListener, ItemSelectable {

	protected static final Border BORDER_NONE = new EmptyBorder(1, 1, 1, 1);

	private boolean singleClickSelectionChange;
	private boolean selectionChanging;
	private ArrayList<AbstractButton> buttons = new ArrayList<AbstractButton>(0);
	private ExtendedButtonGroup group;
	private boolean radioButtonMode;
	private Class<? extends AbstractButton> buttonClass;

	private ArrayList<ItemListener> listeners = new ArrayList<ItemListener>();

	private JPopupMenu popupMenu;
	private boolean showContextMenu;
	private ArrayList<Action> contextMenuActions = new ArrayList<Action>(2);
	private Action checkAllAction;
	private Action checkNoneAction;

	/**
	 * Initializes this object.  Adds mouse and key listeners
	 * and sets single selection mode and a CheckBoxCellRenderer.
	 */
	public JButtonList() {
		super();
		init();
	}

	/**
	 * @param dataModel
	 */
	public JButtonList(ListModel dataModel) {
		super(dataModel);
		init();
	}

	/**
	 * @param listData
	 */
	public JButtonList(Object[] listData) {
		super(listData);
		init();
	}

	/**
	 * @param listData
	 */
	public JButtonList(Vector<?> listData) {
		super(listData);
		init();
	}

	private void init() {
		this.buttonClass = JCheckBox.class;
		this.group = new ExtendedButtonGroup();
		this.radioButtonMode = false;
		this.singleClickSelectionChange = true;
		this.selectionChanging = false;
		this.showContextMenu = true;
		this.popupMenu = new JPopupMenu("JButtonList popup menu");
		this.popupMenu.addPopupMenuListener(this);
		reload(false);
		getModel().addListDataListener(this);
		setCellRenderer(new CheckBoxCellRenderer());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		addMouseListener(this);
		addKeyListener(this);
		addListSelectionListener(this);

		createActions();
		addDefaultContextMenuActions();
	}

	private void createActions() {
		checkAllAction = new AbstractAction("Select All") {
			public void actionPerformed(ActionEvent e) {
				checkAll();
			}
		};
		checkNoneAction = new AbstractAction("Select None") {
			public void actionPerformed(ActionEvent e) {
				checkNone();
			}
		};
	}

	private void addDefaultContextMenuActions() {
		addContextMenuAction(checkAllAction);
		addContextMenuAction(checkNoneAction);
	}

	///////////////////////////////////////////////
	// PUBLIC METHODS
	///////////////////////////////////////////////

	public Object[] getSelectedObjects() {
		return getCheckedItems();
	}

	public void addItemListener(ItemListener il) {
		if (!listeners.contains(il)) {
			listeners.add(il);
		}
	}

	public void removeItemListener(ItemListener il) {
		listeners.remove(il);
	}

	public Action createCheckAllAction() {
		Action action = new AbstractAction("Check All") {
			public void actionPerformed(ActionEvent e) {
				checkAll();
			}
		};
		action.putValue(Action.SHORT_DESCRIPTION, "Check all the items");
		return action;
	}

	public Action createCheckNoneAction() {
		Action action = new AbstractAction("Check None") {
			public void actionPerformed(ActionEvent e) {
				checkNone();
			}
		};
		action.putValue(Action.SHORT_DESCRIPTION, "Uncheck all the items");
		return action;
	}

	public void addContextMenuAction(Action action) {
		contextMenuActions.add(action);
	}

	public void addContextMenuSeparator() {
		contextMenuActions.add(null);
	}

	public void removeContextMenuAction(Action action) {
		contextMenuActions.remove(action);
	}

	public void removeAllContextMenuActions() {
		contextMenuActions.clear();
	}

	public void setShowContextMenu(boolean showContextMenu) {
		this.showContextMenu = showContextMenu;
	}

	public boolean isShowContextMenu() {
		return showContextMenu;
	}

	public void addElement(Object element, boolean checked) {
		if (getModel() instanceof DefaultListModel) {
			DefaultListModel model = (DefaultListModel) getModel();
			model.addElement(element);
			AbstractButton button = getButton(model.getSize() - 1);
			button.setSelected(checked);
			repaint();
		}
	}
	
	public void clear() {
		setModel(new DefaultListModel());
	}

	@Override
	public void setModel(ListModel model) {
		if (getModel() != null) {
			getModel().removeListDataListener(this);
		}
		super.setModel(model);
		if (getModel() != null) {
			getModel().addListDataListener(this);
		}
		reload(true);
	}
	
	public void setListData(Map<Object, Boolean> data) {
		super.setListData(new Vector<Object>(data.keySet()));
		ArrayList<Object> checkedItems = new ArrayList<Object>();
		for (Object obj : data.keySet()) {
			Boolean checked = data.get(obj);
			if (checked) {
				checkedItems.add(obj);
			}
		}
		setCheckedItems(checkedItems);
	}

	/**
	 * Sets the button class to use.
	 * @param abstractButton
	 */
	public void setButtonClass(Class<? extends AbstractButton> abstractButton) {
		if (abstractButton == null) {
			abstractButton = JCheckBox.class;
		}
		if (!abstractButton.equals(buttonClass)) {
			this.buttonClass = abstractButton;

			// save the checked indeces
			int[] checked = getCheckedIndeces();
			reload(isVisible());
			// restore the checked indeces
			setCheckedIndices(checked);
		}
	}

	public Class<? extends AbstractButton> getButtonClass() {
		return buttonClass;
	}

	/**
	 * If true is passed in then only one of the checkboxes/buttons can be
	 * checked/selected at one time.
	 * @param onlyOneChecked
	 */
	public void setRadioButtonMode(boolean onlyOneChecked) {
		if (onlyOneChecked != radioButtonMode) {
			this.radioButtonMode = onlyOneChecked;
			// save the checked indeces
			int[] checked = getCheckedIndeces();
			reload(isVisible());
			// restore the checked indeces
			setCheckedIndices(checked);
		}
	}

	public boolean isRadioButtonMode() {
		return radioButtonMode;
	}

	/**
	 * If true is passed in then any time the mouse is clicked on an item
	 * (the checkbox or the text) its checked value will be toggled.
	 * If false is passed in then the item's selected value will only toggle
	 * when either the checkbox is clicked or when the item is click when it is selected in the list.
	 * @param singleClickSelectionChange
	 */
	public void setSingleClickSelectionChange(boolean singleClickSelectionChange) {
		this.singleClickSelectionChange = singleClickSelectionChange;
	}

	public boolean isSingleClickSelectionChange() {
		return singleClickSelectionChange;
	}

	/**
	 * @return an array of the objects in the list that are checked (selected).
	 */
	public Object[] getCheckedItems() {
		final Collection<Object> items = getCheckedItemsCollection();
		return items.toArray(new Object[items.size()]);
	}

	/**
	 * @return a {@link Collection} of the objects in the list that are checked (selected).
	 */
	public Collection<Object> getCheckedItemsCollection() {
		ArrayList<Object> checkedItems = new ArrayList<Object>();
		for (int i = 0; i < getModel().getSize(); i++) {
			Object obj = getModel().getElementAt(i);
			AbstractButton button = buttons.get(i);
			if (button.isSelected()) {
				checkedItems.add(obj);
			}
		}
		return checkedItems;
	}

	protected boolean setSelected(AbstractButton button, boolean selected, int index) {
		boolean changed = false;
		if (button.isSelected() != selected) {
			button.setSelected(selected);
			fireItemSelected(index);
			changed = true;
		}
		return changed;
	}

	/**
	 * Sets the button associated with the item to be checked
	 * and all other buttons will be unchecked.
	 * @param item
	 */
	public void setCheckedItem(Object item) {
		ArrayList<Object> list = new ArrayList<Object>(1);
		list.add(item);
		setCheckedItems(list);
	}

	/**
	 * Sets the button associated with each item to be checked, all remaining buttons will be unchecked.
	 * @param items the items to check
	 */
	public void setCheckedItems(Object[] items) {
		setCheckedItems(Arrays.asList(items));
	}

	/**
	 * Sets the button associated with each item to be checked, all remaining buttons will be unchecked.
	 * @param items the items to check
	 */
	public void setCheckedItems(Collection<Object> items) {
		for (int i = 0; i < getModel().getSize(); i++) {
			Object obj = getModel().getElementAt(i);
			AbstractButton button = buttons.get(i);
			boolean selected = items.contains(obj);
			setSelected(button, selected, i);
		}
	}

	/**
	 * @return the number of checked items in the list
	 */
	public int getCheckedCount() {
		return getCheckedItemsCollection().size();
	}

	/**
	 * @return an array of the indeces in the list that are checked (selected).
	 */
	public int[] getCheckedIndeces() {
		ArrayList<Integer> checkedIndeces = new ArrayList<Integer>();
		for (int i = 0; i < getModel().getSize(); i++) {
			AbstractButton button = buttons.get(i);
			if (button.isSelected()) {
				checkedIndeces.add(i);
			}
		}
		int[] indeces = new int[checkedIndeces.size()];
		for (int i = 0; i < indeces.length; i++) {
			indeces[i] = (checkedIndeces.get(i)).intValue();
		}
		return indeces;
	}

	/**
	 * Checks the buttons at the given indeces.
	 * If null or an empty array is passed in then all the buttons are unchecked.
	 * If an invalid index is passed in nothing happens.
	 * @param checkedIndeces
	 */
	public void setCheckedIndices(int[] checkedIndeces) {
		checkNone();
		if ((checkedIndeces != null) && (checkedIndeces.length > 0)) {
			for (int index : checkedIndeces) {
				if ((index >= 0) && (index < buttons.size())) {
					AbstractButton button = buttons.get(index);
					setSelected(button, true, index);
				}
			}
			repaint();
		}
	}

	/**
	 * Return whether the button at the specified index is selected
	 * @param index
	 * @return true if the button at the index is checked/selected
	 */
	public boolean isCheckedIndex(int index) {
		if ((index >= 0) && (index < buttons.size())) {
			AbstractButton button = buttons.get(index);
			return button.isSelected();
		}
		return false;
	}
	
	/**
	 * Set the button at the specified index to be checked
	 * @param index the index of the button
	 * @param checked if the button should be checked
	 */
	public void setCheckedIndex(int index, boolean checked) {
		if ((index >= 0) && (index < buttons.size())) {
			AbstractButton button = buttons.get(index);
			if (setSelected(button, checked, index)) {
				repaint();
			}
		}
	}

	/**
	 * Selects (checks) all of the buttons.
	 */
	public void checkAll() {
		boolean change = false;
		for (int i = 0; i < buttons.size(); i++) {
			AbstractButton button = buttons.get(i);
			change = setSelected(button, true, i) || change;
		}
		if (change) {
			repaint();
		}
	}

	/**
	 * Selects (checks) none of the buttons.
	 */
	public void checkNone() {
		boolean change = false;
		for (int i = 0; i < buttons.size(); i++) {
			AbstractButton button = buttons.get(i);
			change = setSelected(button, false, i) || change;
		}
		repaint();
	}

	public AbstractButton getButton(Object element) {
		AbstractButton button = null;
		for (int i = 0; i < getModel().getSize(); i++) {
			Object obj = getModel().getElementAt(i);
			if ((obj != null) && obj.equals(element)) {
				if (i < buttons.size()) {
					button = buttons.get(i);
				}
				break;
			}
		}
		return button;
	}

	public AbstractButton getButton(int index) {
		AbstractButton button = null;
		if ((index >= 0) && (index < buttons.size())) {
			button = buttons.get(index);
		}
		return button;
	}

	public int getButtonCount() {
		return buttons.size();
	}

	/**
	 * Gets the tooltip text for the list.
	 * If a tooltip is defined on this list then that is returned.
	 * Otherwise it attempts to get a tooltip based on the mouse location by getting the
	 * object underneath the mouse.  If the object is an {@link Action} or a {@link JComponent}
	 * then the tooltip is returned for those objects.  If it is a {@link JTextComponent} or a
	 * {@link JLabel} then the text is returned for those objects.
	 */
	@Override
	public String getToolTipText(MouseEvent e) {
		String tt = super.getToolTipText(e);
		if ((tt == null) || (tt.length() == 0)) {
			// Convert the mouse coordinates where the left mouse button was pressed
			int index = locationToIndex(e.getPoint());
			ListModel model = getModel();
			if ((index >= 0) && (index < model.getSize())) {
				Object obj = model.getElementAt(index);
				if (obj instanceof String) {
					tt = (String) obj;
				} else if (obj instanceof Action) {
					Action action = (Action) obj;
					tt = (String) action.getValue(Action.SHORT_DESCRIPTION);
					if (tt == null) {
						tt = (String) action.getValue(Action.NAME);
					}
				} else if (obj instanceof JComponent) {
					tt = ((JComponent) obj).getToolTipText();
				}
				if ((tt == null) && (obj instanceof JTextComponent)) {
					tt = ((JTextComponent) obj).getText();
				}
				if ((tt == null) && (obj instanceof JLabel)) {
					tt = ((JLabel) obj).getText();
				}
				if ((tt == null) && (obj != null)) {
					// do we want to use the toString()? It might give the hashcode
					//tt = obj.toString();
				}
			}
		}
		return tt;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (AbstractButton btn : buttons) {
			btn.setEnabled(enabled);
		}
	}

	// //////////////////////////////////////////
	// EVENT HANDLERS
	// //////////////////////////////////////////

	public void itemStateChanged(ItemEvent e) {
		fireItemSelected(e);
	}

	public void valueChanged(ListSelectionEvent e) {
		if (!singleClickSelectionChange) {
			selectionChanging = e.getValueIsAdjusting();
		}
	}

	public void contentsChanged(ListDataEvent e) {
		if (e.getIndex1() >= e.getIndex0()) {
			for (int index = e.getIndex1(); index >= e.getIndex0(); index--) {
				if (index < buttons.size()) {
					Object obj = getModel().getElementAt(index);
					String text = (obj != null ? obj.toString() : "null");
					AbstractButton btn = buttons.get(index);
					btn.setText(text);
					btn.setEnabled(isEnabled());
				}
			}
			repaint(getCellBounds(e.getIndex0(), e.getIndex1()));
		}
	}

	public void intervalAdded(ListDataEvent e) {
		if (e.getIndex1() >= e.getIndex0()) {
			for (int index = e.getIndex1(); index >= e.getIndex0(); index--) {
				AbstractButton button = createButton(getModel().getElementAt(index));
				addButton(index, button);
			}
			repaint();
		}
	}

	public void intervalRemoved(ListDataEvent e) {
		if (e.getIndex1() >= e.getIndex0()) {
			for (int index = e.getIndex1(); index >= e.getIndex0(); index--) {
				removeButton(index);
			}
			repaint();
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			// select the item under the mouse
			final int index = locationToIndex(e.getPoint());
			if ((index != -1) && (index != getSelectedIndex())) {
				setSelectedIndex(index);
			}

			// show the popup menu
			if (isShowContextMenu() && (contextMenuActions.size() > 0)) {
				popupMenu.show(this, e.getX(), e.getY());
			}
		}
	}

	/**
	 * Toggles the checkbox if the clicked checkbox is the selected (highlighted) one
	 * or if the click was right on top of the checkbox.
	 */
	public void mousePressed(MouseEvent e) {
		// checkbox and radiobutton size
		boolean clickedOnCB = (e.getX() < 16);
		if (SwingUtilities.isLeftMouseButton(e) &&
			(singleClickSelectionChange || clickedOnCB || !selectionChanging)) {
			int index = locationToIndex(e.getPoint());
			if (index != -1) {
				Rectangle bounds = getCellBounds(index, index);
				if ((bounds != null) && bounds.contains(e.getPoint())) {
					toggleCheckBox(index);
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Toggles the selected checkbox(es) if the space or enter key is pressed.
	 */
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if ((code == KeyEvent.VK_SPACE) || (code == KeyEvent.VK_ENTER)) {
			for (int index : getSelectedIndices()) {
				toggleCheckBox(index);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void popupMenuCanceled(PopupMenuEvent e) {
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// update enablement for check all/none - not valid in radio button mode
		checkAllAction.setEnabled(!isRadioButtonMode());
		checkNoneAction.setEnabled(!isRadioButtonMode());

		popupMenu.removeAll();
		for (Action action : contextMenuActions) {
			if (action != null) {
				popupMenu.add(action);
			} else {
				popupMenu.addSeparator();
			}
		}
	}

	/////////////////////////////////////////////////////////
	// PRIVATE METHODS
	/////////////////////////////////////////////////////////

	protected void addButton(AbstractButton button) {
		addButton(buttons.size(), button);
	}

	protected void addButton(int index, AbstractButton button) {
		if ((index >= buttons.size())) {
			buttons.add(button);
		} else {
			buttons.add(index, button);
		}
		// have to keep the buttons enabled in sync with this list
		button.setEnabled(isEnabled());

		// This is commented out because if fires an event every time a checkbox/button is toggled
		// regardless of whether it was a user event (mouse click) or done programmatically.
		// Also - the ItemEvent item variable is set to the AbstractButton, not the model element
		//button.addItemListener(this);

		if (radioButtonMode) {
			group.add(button);
		}
	}

	protected void removeButton(int index) {
		if ((index >= 0) && (index < buttons.size())) {
			AbstractButton button = buttons.remove(index);
			button.removeItemListener(this);
			if (group.containsButton(button)) {
				group.remove(button);
			}
		}
	}

	protected void removeButton(AbstractButton button) {
		if (button != null) {
			buttons.remove(button);
			button.removeItemListener(this);
			if (group.containsButton(button)) {
				group.remove(button);
			}
		}
	}

	protected void fireItemSelected(int index) {
		if (listeners.size() > 0) {
			AbstractButton button = buttons.get(index);
			Object obj = getModel().getElementAt(index);
			fireItemSelected(button, obj);
		}
	}

	protected void fireItemSelected(AbstractButton button, Object item) {
		int state = (button.isSelected() ? ItemEvent.SELECTED : ItemEvent.DESELECTED);
		ItemEvent ie = new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, item, state);
		fireItemSelected(ie);
	}

	protected void fireItemSelected(ItemEvent ie) {
		if (listeners.size() > 0) {
			ArrayList<ItemListener> clone = new ArrayList<ItemListener>(listeners);
			for (ItemListener itemListener : clone) {
				itemListener.itemStateChanged(ie);
			}
		}
	}

	/**
	 * Toggles the checkbox with the given index.
	 * @param index
	 */
	protected void toggleCheckBox(int index) {
		if ((index >= 0) && (index < getModel().getSize())) {
			AbstractButton button = buttons.get(index);
			if (button.isEnabled()) {
				if (setSelected(button, !button.isSelected(), index)) {
					repaint();
				}
			}
		}
	}

	protected void reload(boolean repaint) {
		// remove the listeners and from the group
		while (buttons.size() > 0) {
			removeButton(buttons.get(0));
		}

		group.clearButtons();
		int size = getModel().getSize();
		buttons = new ArrayList<AbstractButton>(size);
		for (int i = 0; i < size; i++) {
			Object obj = getModel().getElementAt(i);
			AbstractButton button = (obj instanceof AbstractButton ? (AbstractButton) obj : createButton(obj));
			addButton(i, button);
		}
		if (repaint) {
			repaint();
		}
	}

	private AbstractButton createButton(Object obj) {
		AbstractButton button;
		try {
			// make a new button/checkbox
			button = buttonClass.newInstance();
		} catch (Exception e) {
			button = new JCheckBox();
		}
		String text = (obj != null ? obj.toString() : "null");
		button.setText(text);
		return button;
	}

	/**
	 * Renders an {@link AbstractButton} object.  This will usually be a {@link JCheckBox},
	 * but it could also be a {@link JRadioButton}.
	 */
	protected class CheckBoxCellRenderer implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean hasFocus) {
			AbstractButton button;
			if (value instanceof AbstractButton) {
				button = (AbstractButton) value;
				buttons.set(index, button);
			} else {
				if (index < buttons.size()) {
					button = buttons.get(index);
				} else {
					button = createButton(value);
					addButton(index, button);
				}
			}

			button.setBackground(selected ? getSelectionBackground() : getBackground());
			button.setForeground(selected ? getSelectionForeground() : getForeground());
			button.setFocusPainted(false);
			button.setEnabled(isEnabled() && button.isEnabled());
			button.setFont(getFont());
			button.setBorderPainted(true);
			button.setBorder(selected ? UIManager.getBorder("List.focusCellHighlightBorder") : BORDER_NONE);
			return button;
		}
	}

	/**
	 * Extends {@link ButtonGroup} to not allow duplicate buttons in the group.
	 * @author Chris Callendar
	 * @since  7-Dec-06
	 */
	private class ExtendedButtonGroup extends ButtonGroup {

		private static final long serialVersionUID = -2824602884434990065L;

		@Override
		public void add(AbstractButton b) {
			if (!containsButton(b)) {
				super.add(b);
			}
		}

		public void selectNone() {
			for (int i = 0; i < buttons.size(); i++) {
				AbstractButton button = buttons.get(i);
				JButtonList.this.setSelected(button, false, i);
			}
		}

		public void selectAll() {
			for (int i = 0; i < buttons.size(); i++) {
				AbstractButton button = buttons.get(i);
				JButtonList.this.setSelected(button, true, i);
			}
		}

		public boolean containsButton(AbstractButton button) {
			return buttons.contains(button);
		}

		public void clearButtons() {
			for (AbstractButton btn : new Vector<AbstractButton>(buttons)) {
				remove(btn);
			}
		}
	}

	/** Creates a panel containing a sample JCheckBoxList */
	public static JPanel createDemoPanel() {
		JPanel pnl = new JPanel(new BorderLayout());
		JButtonList list = new JButtonList(createDemoModel());

		list.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				String checked = (e.getStateChange() == ItemEvent.SELECTED ? " CHECKED" : " UNCHECKED");
				AbstractButton btn = (AbstractButton) e.getSource();
				System.out.println("Item checked: " + btn.getText() + checked);
			}
		});

		pnl.add(createOptionsPanel(list), BorderLayout.NORTH);
		pnl.add(new JScrollPane(list), BorderLayout.CENTER);
		pnl.setBorder(BorderFactory.createTitledBorder("JCheckBoxList"));
		return pnl;
	}


	/** Creates a panel containing a sample JCheckBoxList */
	public static JPanel createSimpleDemoPanel() {
		JPanel pnl = new JPanel(new BorderLayout());
		DefaultListModel model = new DefaultListModel();
		for (int i = 0; i < 5; i++) {
			model.addElement("Button #" + i);
		}
		JButtonList list = new JButtonList(model);
		pnl.add(new JScrollPane(list), BorderLayout.CENTER);
		pnl.setBorder(BorderFactory.createTitledBorder("JCheckBoxList"));
		return pnl;
	}

	private static DefaultListModel createDemoModel() {
		DefaultListModel model = new DefaultListModel();
		for (int i = 0; i < 3; i++) {
			model.addElement("Button #" + i);
		}
		model.addElement(new JCheckBox("A JCheckBox  "));
		model.addElement(new JRadioButton("A JRadioButton  "));
		model.addElement(new JToggleButton("A JToggleButton  "));
		model.addElement(new JCheckBoxMenuItem("A JCheckBoxMenuItem"));
		return model;
	}

	private static JPanel createOptionsPanel(final JButtonList list) {
		JPanel pnl = new JPanel(new GridLayout(4, 1, 0, 2));
		JCheckBox chk = new JCheckBox("Single Click Selections");
		chk.setSelected(true);
		chk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				list.setSingleClickSelectionChange((e.getStateChange() == ItemEvent.SELECTED));
			}
		});
		pnl.add(chk);
		chk = new JCheckBox("RadioButton mode (single value selection)");
		chk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				list.setRadioButtonMode((e.getStateChange() == ItemEvent.SELECTED));
			}
		});
		pnl.add(chk);
		final JComboBox combo = new JComboBox(new Class[] { JCheckBox.class, JRadioButton.class, JCheckBoxMenuItem.class, JToggleButton.class });
		combo.addItemListener(new ItemListener() {
			@SuppressWarnings("unchecked")
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					Class<? extends AbstractButton> cls = (Class<AbstractButton>) combo.getSelectedItem();
					list.setButtonClass(cls);
				}
			}
		});
		pnl.add(combo);
		return pnl;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("JCheckBoxList Demo");
		frame.setSize(250, 400);
		frame.setLocation(400, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(createDemoPanel(), BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

}
