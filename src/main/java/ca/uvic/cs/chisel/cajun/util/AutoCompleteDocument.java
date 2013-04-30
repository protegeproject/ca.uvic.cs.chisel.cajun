/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

/**
 * Used for an autocompleting {@link JComboBox}.
 *
 * @author Chris Callendar
 * @since  12-Dec-06
 */
public class AutoCompleteDocument extends PlainDocument {

    private JComboBox combobox;
    private FilterComboBoxModel model;
    private JTextComponent editor;
    private boolean restrictToModelElements;
	private boolean showPopup;
    private boolean autocomplete;

    public AutoCompleteDocument(final JComboBox combobox) {
        this.combobox = combobox;
        this.editor = (JTextComponent) combobox.getEditor().getEditorComponent();
        this.restrictToModelElements = false;
        this.showPopup = true;
        this.autocomplete = true;
        setFilteredModel();

        this.combobox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!model.isSelecting()) {
                	highlightCompletedText(0);
                }
            }
        });
        this.combobox.addPropertyChangeListener("model", new PropertyChangeListener() {
        	public void propertyChange(PropertyChangeEvent evt) {
        		if (evt.getNewValue() != null) {
        			setFilteredModel();
        		}
        	}
        });
        editor.addKeyListener(new KeyAdapter() {
        	public void keyPressed(KeyEvent e) {
        		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        			if (combobox.isPopupVisible()) {
        				Object obj = combobox.getSelectedItem();
        				if (obj != null) {
        					editor.setText(obj.toString());
        				}
        			}
        		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
        			hidePopup();
        		}
        	}
        });
    }

    private void setFilteredModel() {
        if (combobox.getModel() instanceof FilterComboBoxModel) {
			this.model = (FilterComboBoxModel) combobox.getModel();
		} else {
			this.model = new FilterComboBoxModel(combobox.getModel());
			combobox.setModel(model);
		}
    }

    /**
     * If true then the user will only be allowed to type in text that matches
     * a value in the list.  Otherwise the previously selected value will be used.
     * @param restrictToModelElements
     */
    public void setRestrictToModelElements(boolean restrictToModelElements) {
    	this.restrictToModelElements = restrictToModelElements;
    }

	/**
	 * @return true if we are restricting what the user types to only allow elements in the list
	 */
	public boolean isRestrictToModelElements() {
		return restrictToModelElements;
	}

	/**
	 * Set this to false to repress showing the combobox dropdown list.
	 * @param showPopup
	 */
	public void setShowPopup(boolean showPopup) {
		this.showPopup = showPopup;
	}

	public boolean isShowPopup() {
		return showPopup;
	}

	/**
	 * Sets whether autocompletion is on.
	 */
	public void setAutoComplete(boolean autocomplete) {
		this.autocomplete = autocomplete;
	}

	public boolean isAutoComplete() {
		return autocomplete;
	}

    public void remove(int offs, int len) throws BadLocationException {
    	hidePopup();

    	// return immediately when selecting an item
        if (model.isSelecting()) {
        	return;
        }
        super.remove(offs, len);

        if (isAutoComplete()) {
	        int length = getLength();
			String fullText = getText(0, length);
			model.setFilterPattern(fullText);

			// select nothing
	    	editor.select(length, length);
			showPopup();
        }
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        hidePopup();
        // return immediately when selecting an item
        if (model.isSelecting()) {
        	return;
        }
        // insert the string into the document
        super.insertString(offs, str, a);

        if (isAutoComplete()) {
	        String fullText = getText(0, getLength());
			model.setFilterPattern(fullText);

			selectMatchingItem(offs + str.length(), fullText);
        }
    }

	private void selectMatchingItem(int start, String fullText) throws BadLocationException {
		// lookup and select a matching item
        Object item = model.find(fullText, true);
		if (item != null) {
            setSelectedItem(item);
            setText(item.toString());
            // highlight the remaining part of the matching item
            highlightCompletedText(start);
            if (fullText.length() > 0) {
            	showPopup();
            }
        } else if (restrictToModelElements) {
        	// keep old item selected if there is no match
            item = combobox.getSelectedItem();
            String text = item.toString();
            setSelectedItem(item);
            setText(text);
            // select the whole string
            highlightCompletedText(0);
            model.setFilterPattern(text);
        } else {
        	// select nothing
        	editor.select(start, start);
        }
	}

    private void showPopup() {
    	if (isShowPopup() && combobox.isShowing() && combobox.isEnabled() && !combobox.isPopupVisible()) {
    		combobox.setPopupVisible(true);
    	}
    }

    private void hidePopup() {
    	if (combobox.isShowing() && combobox.isPopupVisible()) {
    		combobox.setPopupVisible(false);
    	}
    }

    private void setText(String text) throws BadLocationException {
        // remove all text and insert the completed string
        super.remove(0, getLength());
        super.insertString(0, text, null);
    }

    private void highlightCompletedText(int start) {
        editor.setSelectionStart(start);
        editor.setSelectionEnd(getLength());
    }

    private void setSelectedItem(Object item) {
        model.setSelectedItem(item);
    }

    /**
     * Wraps a {@link DefaultComboBoxModel} and supports filtering.
     *
     * @author Chris Callendar
     * @since  13-Dec-06
     */
    private class FilterComboBoxModel extends DefaultComboBoxModel {

    	private String pattern = "";
    	private int cachedSize = -1;
    	private Vector<Object> cachedItems = null;
    	private boolean selecting = false;
    	private boolean caseInsensitive = true;

    	private ComboBoxModel originalModel;

       	public FilterComboBoxModel() {
    		super();
    		originalModel = new DefaultComboBoxModel();
    	}


    	public FilterComboBoxModel(Object[] items) {
    		super();
    		originalModel = new DefaultComboBoxModel(items);
    	}


    	public FilterComboBoxModel(Vector<?> v) {
    		super();
    		originalModel = new DefaultComboBoxModel(v);
    	}

    	public FilterComboBoxModel(ComboBoxModel originalModel) {
    		super();
    		setModel(originalModel);
    	}

    	public void setModel(ComboBoxModel model) {
    		this.originalModel = (model == null ? new DefaultComboBoxModel() : model);
    	}

    	public ComboBoxModel getModel() {
    		return originalModel;
    	}

    	public void setCaseSensitive(boolean caseSensitive) {
    		this.caseInsensitive = !caseSensitive;
    	}

    	public boolean isSelecting() {
    		return selecting;
    	}

    	protected void fireContentsChanged(Object source, int index0, int index1) {
    		selecting = true;
    		super.fireContentsChanged(source, index0, index1);
    		selecting = false;
    	}

    	public void clear() {
    		this.pattern = "";
    		cachedSize = -1;
    		cachedItems = null;
    		fireContentsChanged(this, -1, -1);
    	}

    	public void setFilterPattern(String newPattern) {
    		if (newPattern != null) {
    			String trimmed = newPattern.trim();
    			if (!getFilterPattern().equals(trimmed)) {
    				this.pattern = trimmed;
    				cachedSize = -1;
    				cachedItems = null;
    				fireContentsChanged(this, -1, -1);
    			}
    		} else {
    			clear();
    		}
    	}

    	public String getFilterPattern() {
    		if (pattern == null) {
    			pattern = "";
    		}
    		return pattern;
    	}

    	public boolean isFiltered() {
    		return (getFilterPattern().length() > 0);
    	}

    	public void addElement(Object anObject) {
    		if (originalModel instanceof DefaultComboBoxModel) {
    			((DefaultComboBoxModel)originalModel).addElement(anObject);
    		}
    	}

    	public void insertElementAt(Object anObject, int index) {
    		if (originalModel instanceof DefaultComboBoxModel) {
    			((DefaultComboBoxModel)originalModel).insertElementAt(anObject, index);
    		}
    	}

    	public void removeElement(Object anObject) {
    		if (originalModel instanceof DefaultComboBoxModel) {
    			((DefaultComboBoxModel)originalModel).removeElement(anObject);
    		}
    	}

    	public void removeElementAt(int index) {
    		if (originalModel instanceof DefaultComboBoxModel) {
    			((DefaultComboBoxModel)originalModel).removeElementAt(index);
    		}
    	}

    	public void removeAllElements() {
    		if (originalModel instanceof DefaultComboBoxModel) {
    			((DefaultComboBoxModel)originalModel).removeAllElements();
    		}
    	}

    	public int getIndexOf(Object anObject) {
    		if (originalModel instanceof DefaultComboBoxModel) {
    			return ((DefaultComboBoxModel)originalModel).getIndexOf(anObject);
    		}
    		return -1;
    	}

    	public void setSelectedItem(Object anObject) {
    		originalModel.setSelectedItem(anObject);
    	}

    	public Object getSelectedItem() {
    		return originalModel.getSelectedItem();
    	}

    	public Object getElementAt(int index) {
    		if (isFiltered()) {
    			performFilter();
    		    if ((index >= 0) && (index < cachedItems.size())) {
    	            return cachedItems.elementAt(index);
    		    }
    		    return null;
    		}
    		return originalModel.getElementAt(index);
    	}

    	public int getSize() {
    		int size = 0;
    		if (isFiltered()) {
    			performFilter();
    			size = cachedSize;
    		} else {
    			size = originalModel.getSize();
    		}
    		return size;
    	}

    	private void performFilter() {
    		if (cachedItems == null) {
    			cachedItems = new Vector<Object>();
    			// iterate through ALL elements, doing string comparisons with the pattern
    			for (int i = 0; i < originalModel.getSize(); i++) {
    				Object obj = originalModel.getElementAt(i);
    				if (matches(pattern, obj, caseInsensitive)) {
    					cachedItems.add(obj);
    				}
    			}
    			cachedSize = cachedItems.size();
    		}
    	}

    	private boolean matches(String pattern, Object obj, boolean caseInsensitive) {
    		boolean match = false;
    		if (obj != null) {
    			String str = obj.toString();
    			if (caseInsensitive) {
    				match = str.toUpperCase().startsWith(pattern.toUpperCase());
    			} else {
    				match = str.startsWith(pattern);
    			}
    		}
    		return match;
    	}

    	public Object find(String pattern, boolean checkSelectedFirst) {
    		Object found = null;

    		// check the selected item first
    		if (checkSelectedFirst) {
    			Object selected = getSelectedItem();
    			if (matches(pattern, selected, caseInsensitive)) {
    				found = selected;
    			}
    		}

    		if (found == null) {
        		for (int i = 0; i < originalModel.getSize(); i++) {
    				Object obj = originalModel.getElementAt(i);
    				if (matches(pattern, obj, caseInsensitive)) {
    					found = obj;
    					break;
    				}
    			}
    		}
    		return found;
    	}

    }

}


