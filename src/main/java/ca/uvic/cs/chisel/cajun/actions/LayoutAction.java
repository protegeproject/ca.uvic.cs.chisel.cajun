package ca.uvic.cs.chisel.cajun.actions;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.Icon;

import org.eclipse.zest.layouts.InvalidLayoutConfiguration;
import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.progress.ProgressListener;

import ca.uvic.cs.chisel.cajun.graph.AbstractGraph;
import ca.uvic.cs.chisel.cajun.graph.Graph;
import ca.uvic.cs.chisel.cajun.graph.arc.DefaultGraphArc;
import ca.uvic.cs.chisel.cajun.graph.arc.GraphArc;
import ca.uvic.cs.chisel.cajun.graph.node.DefaultGraphNode;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;
import ca.uvic.cs.chisel.cajun.graph.util.ActivityManager;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PUtil;

public class LayoutAction extends CajunAction {
	private static final long serialVersionUID = -7385859217531335673L;
	
	private static final int MAX_NODES_TO_ANIMATE = 200;
	private static final double DELTA = 0.01;

	private LayoutAlgorithm layout;
	private Graph graph;
	private boolean animate;
	private int maxNodesToAnimate = MAX_NODES_TO_ANIMATE;
	private boolean resizeNodes;
	
	private ActivityManager manager;

	/** list of relationship types that the layout should be applied to */
	private List<Object> layoutRelTypes;
	
	public LayoutAction(String name, Icon icon, LayoutAlgorithm layout, Graph graph) {
		this(name, icon, layout, graph, true);
	}

	public LayoutAction(String name, Icon icon, LayoutAlgorithm layout, Graph graph, boolean animate) {
		super(name, icon);
		this.layout = layout;
		this.graph = graph;
		this.animate = animate;
		this.resizeNodes = false;
		this.layoutRelTypes = new ArrayList<Object>();
		
		this.manager = new ActivityManager(graph.getCanvas(), graph.getCanvas().getRoot().getActivityScheduler());
	}
	
	public LayoutAlgorithm getLayout() {
		return layout;
	}

	public void setLayout(LayoutAlgorithm layout) {
		this.layout = layout;
	}

	public void setLayoutRelTypes(List<Object> layoutRelTypes) {
		this.layoutRelTypes = layoutRelTypes;
	}
	
	public void addProgressListener(ProgressListener listener) {
		manager.addProgressListener(listener);
	}

	public void doAction() {
		// save this action as the last executed action
		((AbstractGraph) graph).setLastLayout(this);
		runLayout();
	}
	
	public void runLayout() {
		// run the layout only on the visible nodes?  Or all nodes?
		Collection<GraphNode> nodes = graph.getModel().getVisibleNodes();
		Collection<GraphArc> arcs = graph.getModel().getVisibleArcs();
		DefaultGraphNode[] entities = nodes.toArray(new DefaultGraphNode[nodes.size()]);
		
		Collection<GraphArc> filteredArcs;
		if (layoutRelTypes.isEmpty()) {
			// no arcs in the list - so assume all arcs should be used in the layout
			filteredArcs = arcs;
		} else {
			// remove arcs that have been filtered
			filteredArcs = new ArrayList<GraphArc>();
			for (GraphArc arc : arcs) {
				if (layoutRelTypes.contains(arc.getType())) {
					filteredArcs.add(arc);
				}
			}
		}
		DefaultGraphArc[] rels = filteredArcs.toArray(new DefaultGraphArc[filteredArcs.size()]);

		PCanvas canvas = graph.getCanvas();

		double x = 0, y = 0;
		double w = Math.max(0, canvas.getWidth() - 10);
		double h = Math.max(0, canvas.getHeight() - 10);

		// to allow extra room for wide nodes
		if (w > 400) {
			w -= 100;
		}
		// extra room for tall nodes (labels wrap)
		if (h > 300) {
			h -= 30;
		}
	
		try {
			// define a local version of the layout in order to avoid threading issues
			LayoutAlgorithm layout = getLayoutAlgorithm();
			layout.applyLayout(entities, rels, x, y, w, h, false, false);

			if (animate && (nodes.size() > maxNodesToAnimate)) {
				animate = false;
			}

			//PActivityScheduler scheduler = canvas.getRoot().getActivityScheduler();
			ArrayList<PActivity> activities = new ArrayList<PActivity>(nodes.size());
			
			for (GraphNode node : nodes) {
				if(!node.isFixedLocation()) {
					if (animate) {
						AffineTransform transform = createTransform(node);
						PActivity activity = createActivity(node, transform);
						if (activity != null) {
							activities.add(activity);
						}
					} else {
						node.setLocation(node.getXInLayout(), node.getYInLayout());
					}
				}
			}
			
			if (animate) {
				//ActivityManager manager = new ActivityManager(canvas, scheduler, activities);
				manager.setActivities(activities);
				// wait until all nodes have finished moving
				// @tag question : why did Chris put this in here?  it blocks the UI thread
				//manager.waitForActivitiesToFinish();
			} else {
				canvas.repaint();
			}
			
			// ensure that the first selected node is visible in the scroll pane
//			Collection<GraphNode> selectedNodes = graph.getSelectedNodes();
//			if (selectedNodes.size() > 0) {
//				GraphNode first = selectedNodes.iterator().next();
//				Rectangle2D bounds = first.getBounds();
//				//graph.getCanvas().scrollRectToVisible(bounds.getBounds());
//			}

		} catch (InvalidLayoutConfiguration e) {
			e.printStackTrace();
		}
	}

	protected AffineTransform createTransform(GraphNode node) {
		Rectangle2D bounds = node.getBounds();
		double oldW = bounds.getWidth();
		double oldH = bounds.getHeight();
		double newW = node.getWidthInLayout();
		double newH = node.getHeightInLayout();
		double dw = newW - oldW;
		double dh = newH - oldH;

		double dx = (node.getXInLayout() - bounds.getX());
		double dy = (node.getYInLayout() - bounds.getY());

		AffineTransform at = new AffineTransform();
		boolean valid = false;
		if ((Math.abs(dx) > DELTA) || (Math.abs(dy) > DELTA)) {
			at.translate(dx, dy);
			valid = true;
		}
		if (resizeNodes && ((oldW != 0) && (oldH != 0)) && ((Math.abs(dw) > DELTA) || (Math.abs(dh) > DELTA))) {
			double sx = (newW / oldW);
			double sy = (newH / oldH);
			// TODO I don't know if this actually works!
			at.scale(sx, sy);
			valid = true;
		}
		if (!valid) {
			at = null;
		}
		return at;
	}

	/**
	 * See ca.uvic.csr.shrimp.DisplayBean.AbstractDisplayBean#
	 * setTransformsOfNodesWithAnimation(java.util.List, java.util.List)
	 */
	protected PActivity createActivity(final GraphNode node, AffineTransform transform) {
		Rectangle2D bounds = node.getBounds();
		final double startX = bounds.getX();
		final double startY = bounds.getY();
		PTransformActivity.Target t = new PTransformActivity.Target() {
			public void setTransform(AffineTransform at) {
				//node.setTransform(at);
				node.setLocation(startX + at.getTranslateX(), startY + at.getTranslateY());
			}

			public void getSourceMatrix(double[] aSource) {
				if (node instanceof PNode) {
					((PNode) node).getTransformReference(true).getMatrix(aSource);
				}
			}
		};
		PActivity activity = new PTransformActivity(1500, PUtil.DEFAULT_ACTIVITY_STEP_RATE, t, transform);
		return activity;
	}

	/**
	 * Creates a new instance of the LayoutAlgorithm using reflection.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private LayoutAlgorithm getLayoutAlgorithm() {
		Class<LayoutAlgorithm> c;
		try {
			c = (Class<LayoutAlgorithm>) Class.forName(layout.getClass().getName());
			return c.newInstance();
		} catch (ClassNotFoundException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
