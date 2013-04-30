package ca.uvic.cs.chisel.cajun.filter;

import ca.uvic.cs.chisel.cajun.graph.GraphItem;

public interface GraphFilter {

	/**
	 * @return true if this filter acts on nodes
	 */
	public boolean isNodeFilter();
	
	/**
	 * @return true if this filter acts on arcs
	 */
	public boolean isArcFilter();
	
	/**
	 * Determines whether the given GraphItem (node or arc) should be visible.
	 * @param item
	 * @return true if the item should be visible, or false if it should be hidden
	 */
	public boolean isVisible(GraphItem item);
	
}
