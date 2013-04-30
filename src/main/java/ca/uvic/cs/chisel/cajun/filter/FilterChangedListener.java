package ca.uvic.cs.chisel.cajun.filter;


/**
 * When something is filtered/unfiltered, this class updates the
 * the display bean.
 */
public interface FilterChangedListener {
	
	public void filtersChanged(FilterChangedEvent fce);
	
}