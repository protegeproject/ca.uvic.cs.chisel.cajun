package ca.uvic.cs.chisel.cajun.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

public class CajunAction extends AbstractAction {
	private static final long serialVersionUID = 4635666655057284083L;

	public CajunAction() {
		super();
	}
	
	public CajunAction(String name) {
		super(name);
	}

	public CajunAction(String name, Icon icon) {
		super(name, icon);
	}

	public CajunAction(String name, String tooltip) {
		this(name, null, tooltip);
	}

	public CajunAction(String name, Icon icon, String tooltip) {
		super(name, icon);
		setTooltip(tooltip);
	}

	public void setTooltip(String tooltip) {
		putValue(SHORT_DESCRIPTION, tooltip);
	}
	
	public String getTooltip() {
		return (String) getValue(SHORT_DESCRIPTION);
	}

	public String getName() {
		return (String) getValue(NAME);
	}
	
	public void setName(String name) {
		putValue(NAME, name);
	}
	
	public Icon getIcon() {
		return (Icon) getValue(SMALL_ICON);
	}
	
	public void setIcon(Icon icon) {
		putValue(SMALL_ICON, icon);
	}
	
	public void actionPerformed(ActionEvent e) {
		doAction();
	}
	
	public String toString() {
		return getName();
	}
	
	public boolean equals(Object o) {
		return toString().equals(o.toString());
	}
	
	public int hashCode() {
		return toString().hashCode();
	}
	
	public void doAction() {
		
	}

}
