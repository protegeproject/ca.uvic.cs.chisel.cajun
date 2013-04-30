package ca.uvic.cs.chisel.cajun.graph.util;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

import ca.uvic.cs.chisel.cajun.graph.AbstractGraph;
import ca.uvic.cs.chisel.cajun.graph.handlers.PNormalZoomHandler;
import ca.uvic.cs.chisel.cajun.util.GeometryUtils;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivityScheduler;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PUtil;

/**
 * Utility class for working with a {@link PCanvas} and performing animations.
 *
 * @author Chris
 * @since  9-Nov-07
 */
public class AnimationHandler {

	private PCanvas canvas;

	public AnimationHandler(PCanvas canvas) {
		this.canvas = canvas;
	}

	protected PLayer getNodeLayer() {
		return canvas.getCamera().getLayer(AbstractGraph.NODE_LAYER_INDEX);
	}
	
	protected PLayer getArcLayer() {
		return canvas.getCamera().getLayer(AbstractGraph.ARC_LAYER_INDEX);
	}
	
    public Rectangle2D.Double getDisplayBounds() {
		Rectangle bounds = canvas.getBounds();
		if ((bounds == null) || (bounds.width < 0) || (bounds.height < 0)) {
			bounds.setBounds(0, 0, 1000, 1000);
			canvas.setBounds(bounds);
		}
		return new Rectangle2D.Double(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public Rectangle2D.Double getGlobalOuterBounds(PNode node) {
		Rectangle2D.Double bounds = node.getBounds();
		bounds = GeometryUtils.transform(bounds, node.getLocalToGlobalTransform(new PAffineTransform()));
		return bounds;
	}
	
	protected int getNodeCount() {
		return getNodeLayer().getChildrenCount();
	}
	
	protected Collection<PNode> getNodes() {
		ArrayList<PNode> nodes = new ArrayList<PNode>();
		for (Object obj : getNodeLayer().getAllNodes()) {
			if (obj instanceof PNode) {
				PNode displayNode = (PNode) obj;
				nodes.add(displayNode);
			}
		}
		return nodes;
	}
	
	protected int getArcCount() {
		return getArcLayer().getChildrenCount();
	}

	protected Collection<PNode> getArcs() {
		ArrayList<PNode> allArcs = new ArrayList<PNode>();
		Collection<?> arcs = getArcLayer().getAllNodes();
		for (Object obj : arcs) {
			if (obj instanceof PNode) {
				allArcs.add((PNode) obj);
			}
		}
		return allArcs;
	}
	
	public void moveViewToCenterBounds(Rectangle2D globalBounds, boolean shouldScale, long duration, boolean animate) {
        // turn off the animation if too many objects on screen
        int animationThreshold = 500;
        animate = animate && (getNodeCount() < animationThreshold);
        if (!animate) {
            duration = 0;
        }

		PActivity activity = getAnimateViewToCenterBoundsActivity(globalBounds, shouldScale, duration);
        if (activity != null) {
        	PActivityScheduler scheduler = canvas.getCamera().getRoot().getActivityScheduler();
    		ActivityManager manager = new ActivityManager(canvas, scheduler, activity);
    		manager.waitForActivitiesToFinish();
        }
	}
	
	public void focusOnCoordinates(Rectangle2D coords, boolean animate) {
		boolean shouldScale = true;
		moveViewToCenterBounds(coords, shouldScale, 500, animate);
	}
	
	public void focusOnExtents(boolean animate) {
        if (getNodeCount() > 0) {
	        try {
				if (canvas.getCamera().getViewScale() != 1) {
					// need to set back to no zoom state
					new PNormalZoomHandler(canvas.getCamera()).noZoom();
				}
				
				double[] extents = getExtents();
				Rectangle2D extentsRect = new Rectangle2D.Double(extents[0], extents[1], extents[2], extents[3]);
				focusOnCoordinates(extentsRect, animate);
			} finally {
				// default cursor
			}
        }
	}

	/**
	 * @return a PActivity that will animiate the view to the center
	 */
	private PActivity getAnimateViewToCenterBoundsActivity(Rectangle2D globalBounds, boolean shouldScale, long duration) {
		Rectangle2D viewBounds = canvas.getCamera().getBounds();
		if (viewBounds.isEmpty()) {
			// @tag Shrimp.Piccolo.Determinant0 : if the view bounds are null then the transform will be zero
			System.err.println("Warning - viewing bounds is empty!");
		}
		double s = 1;

		if (shouldScale) {
			s = Math.min(viewBounds.getWidth() / globalBounds.getWidth(), viewBounds.getHeight() / globalBounds.getHeight());
		}

		AffineTransform destination = new AffineTransform();
		double transX = viewBounds.getCenterX() + (-globalBounds.getCenterX() * s);
		double transY = viewBounds.getCenterY() + (-globalBounds.getCenterY() * s);
		destination.translate(transX, transY);
		destination.scale(s, s);

		if (duration == 0) {
			canvas.getCamera().setViewTransform(destination);
            return null;
		}

		PTransformActivity.Target t = new PTransformActivity.Target() {
			public void setTransform(AffineTransform aTransform) {
				canvas.getCamera().setViewTransform(aTransform);
			}
			public void getSourceMatrix(double[] aSource) {
				canvas.getCamera().getViewTransformReference().getMatrix(aSource);
			}
		};

		PTransformActivity ta = new PTransformActivity(duration, PUtil.DEFAULT_ACTIVITY_STEP_RATE, t, destination);
		return ta;
	}
	
    private double[] getExtents() {
        double x = 0;
        double y = 0;
        double w = 0;
        double h = 0;

		if (getNodeCount() > 0) {
			double minX = Double.MAX_VALUE;
			double minY = Double.MAX_VALUE;
			double maxX = Double.MIN_VALUE;
			double maxY = Double.MIN_VALUE;

			Collection<PNode> visibleNodes = getNodes();
			for (PNode node : visibleNodes) {
				Rectangle2D.Double bounds = getGlobalOuterBounds(node);
				if (bounds.getX() < minX) {
					minX = bounds.getX();
				}
				if (bounds.getY() < minY) {
					minY = bounds.getY();
				}
				if (bounds.getX() + bounds.getWidth() > maxX) {
					maxX = bounds.getX() + bounds.getWidth();
				}
				if (bounds.getY() + bounds.getHeight() > maxY) {
					maxY = bounds.getY() + bounds.getHeight();
				}
			}
			x = minX;
			y = minY;
			w = maxX - minX; //Math.max(getDisplayBounds().width, maxX - minX);
			h = maxY - minY; //Math.max(getDisplayBounds().height, maxY - minY);
		} else {
		    x = getDisplayBounds().x;
		    y = getDisplayBounds().y;
		    w = getDisplayBounds().width;
		    h = getDisplayBounds().height;
		}

		// make extents 10% bigger
		double borderWidth = w * 0.1;
		double borderHeight = h * 0.1;
		x -= 0.5 * borderWidth;
		y -= 0.5 * borderHeight;
		w += borderWidth;
		h += borderHeight;
        return new double[] { x, y, w, h };
    }
    
}
