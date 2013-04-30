/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.graph.handlers;

import java.awt.Component;

import javax.swing.JPopupMenu;

import ca.uvic.cs.chisel.cajun.graph.arc.GraphArc;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;

/**
 * Displays a popup menu depending on which type of node is picked - arc, node, or camera.
 * 
 * @author Chris Callendar
 */
public class GraphPopupListener extends PBasicInputEventHandler {
	
	protected JPopupMenu nodeMenu;
	protected JPopupMenu arcMenu;
	protected JPopupMenu canvasMenu;
	
	protected JPopupMenu popup;
	
	public GraphPopupListener() {
		this.nodeMenu = new JPopupMenu("Graph Node Context Menu");
		this.arcMenu = new JPopupMenu("Graph Arc Context Menu");
		this.canvasMenu = new JPopupMenu("Graph Canvas Context Menu");
		// this is set in the beforeShowPopup() menu
		this.popup = null;
		
		PInputEventFilter filter = new PInputEventFilter();
		filter.rejectAllEventTypes();
		filter.setAcceptsMousePressed(true);
		filter.setAcceptsMouseReleased(true);
		setEventFilter(filter);
	}
	
	public JPopupMenu getNodeMenu() {
		return nodeMenu;
	}
	
	public JPopupMenu getArcMenu() {
		return arcMenu;
	}
	
	public JPopupMenu getCanvasMenu() {
		return canvasMenu;
	}
	
	@Override
	public void mousePressed(PInputEvent event) {
		maybeShowPopup(event);
	}
	
	@Override
	public void mouseReleased(PInputEvent event) {
		maybeShowPopup(event);
	}

	protected final void maybeShowPopup(PInputEvent e) {
		if (e.isPopupTrigger()) {
			boolean show = beforeShowPopup(e);
			if (show) {
				showPopup(e);
			}
		}
	}
	
	/**
	 * Subclasses can override this method to perform operations before the popup menu is displayed.
	 * This method returns true by default.
	 * @return boolean if true the popup menu will be displayed, if false it will not be displayed
	 */
	protected boolean beforeShowPopup(PInputEvent e) {
		boolean show = true;
		PNode node = e.getPickedNode();
		if (node instanceof GraphNode) {
			popup = nodeMenu;
		} else if (node instanceof GraphArc) {
			popup = arcMenu;
		} else if (node instanceof PCamera) {
			popup = canvasMenu;
		} else {
			show = false;
		}
		return show;
	}
	
	protected void showPopup(PInputEvent e) {
		Component invoker = (Component) e.getComponent();
		if ((invoker != null) && (popup != null) && (popup.getComponentCount() > 0)) {
			int x = (int) e.getCanvasPosition().getX();
			int y = (int) e.getCanvasPosition().getY();
			if (x + popup.getWidth() > invoker.getX() + invoker.getWidth()) {
				x = invoker.getX() + invoker.getWidth() - popup.getWidth();
			}
			if (y + popup.getHeight() > invoker.getY() + invoker.getHeight()) {
				y = invoker.getY() + invoker.getHeight() - popup.getHeight();
			}
			popup.show(invoker, x, y);
		}
	}

}