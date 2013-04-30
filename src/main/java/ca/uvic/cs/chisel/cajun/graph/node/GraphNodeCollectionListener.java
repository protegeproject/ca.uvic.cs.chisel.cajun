package ca.uvic.cs.chisel.cajun.graph.node;

/**
 * Notifies listeners that the {@link NodeCollection} changed.
 *
 * @author Chris
 * @since  20-Nov-07
 */
public interface GraphNodeCollectionListener {

	/**
	 * Notifies listeners that the {@link NodeCollection} changed.
	 * @param evt
	 */
	public void collectionChanged(GraphNodeCollectionEvent evt);
	
}
