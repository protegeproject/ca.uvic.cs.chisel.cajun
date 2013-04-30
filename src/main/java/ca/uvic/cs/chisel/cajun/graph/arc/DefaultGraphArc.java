package ca.uvic.cs.chisel.cajun.graph.arc;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.eclipse.zest.layouts.LayoutBendPoint;
import org.eclipse.zest.layouts.LayoutEntity;
import org.eclipse.zest.layouts.constraints.LayoutConstraint;

import ca.uvic.cs.chisel.cajun.graph.node.DefaultGraphNode;
import ca.uvic.cs.chisel.cajun.graph.node.GraphNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

public class DefaultGraphArc extends PPath implements GraphArc {
	private static final long serialVersionUID = 1530720146193007435L;
	
	private static final double CURVE_FACTOR_BASE_OFFSET = 8.0;
	private static final double SELF_ARC_DIAMETER = 20.0;

	private final Object userObject;
	private Object type;

	private Object graphData;
	
	private GraphNode src;
	private GraphNode dest;

	private Object layoutObject;

	private boolean selected;
	private boolean highlighted;

	private GraphArcStyle style;

	//private GeneralPath path;
	private int curveFactor;

	private boolean showArrowHead;
	private ArrowHead arrowHead;
	private String tooltip;

	private boolean inverted;
	
	private Icon icon;
	private PImage image;

	public DefaultGraphArc(Object userObject, GraphNode src, GraphNode dest) {
		this(userObject, src, dest, UNKNOWN_TYPE);
	}
	
	public DefaultGraphArc(Object userObject, GraphNode src, GraphNode dest, Object type) {
		this(userObject, src, dest, null, type);
	}
	
	public DefaultGraphArc(Object userObject, GraphNode src, GraphNode dest, Icon icon, Object type) {
		super();

		this.userObject = userObject;
		this.src = src;
		this.dest = dest;
		setType(type);

		//this.path = new GeneralPath();
		this.curveFactor = 0;

		this.selected = false;
		this.highlighted = false;

		this.showArrowHead = true;
		this.arrowHead = new ArrowHead();

		this.inverted = false;

		if(icon != null) {
			this.icon = icon;
			image = new PImage(((ImageIcon) icon).getImage());
			addChild(image);
		}
		
		this.style = new DefaultGraphArcStyle();
	}
	
	public void setShowArrowHead(boolean showArrowHead) {
		this.showArrowHead = showArrowHead;
	}
	
	public Icon getIcon() {
		return icon;
	}

	public Object getUserObject() {
		return userObject;
	}

	public Object getType() {
		return type;
	}

	public void setType(Object type) {
		this.type = (type != null ? type : UNKNOWN_TYPE);
	}

	public GraphNode getSource() {
		return src;
	}

	public GraphNode getDestination() {
		return dest;
	}

	public void setCurveFactor(int curveFactor) {
		this.curveFactor = curveFactor;
	}

	public int getCurveFactor() {
		return curveFactor;
	}

	public boolean isInverted() {
		return inverted;
	}

	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	public GraphArcStyle getArcStyle() {
		return style;
	}

	public void setArcStyle(GraphArcStyle style) {
		if ((style != null) && (this.style != style)) {
			this.style = style;
			invalidateFullBounds();
			invalidatePaint();
		}
	}

	public boolean isVisible() {
		return getVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		if (!visible) {
			// hide the arc
			super.setVisible(false);
		} else if (visible && getSource().isVisible() && getDestination().isVisible()) {
			// only show the arc if both the source and destination nodes are visible
			super.setVisible(true);
		}
	}

	@Override
	public String toString() {
		GraphNode src = (isInverted() ? getDestination() : getSource());
		GraphNode dest = (isInverted() ? getSource() : getDestination());
		return src + " -- " + getType() + " --> " + dest;
	}

	public String getTooltip() {
		if (tooltip == null) {
			return toString();
		}
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			arrowHead.setSelected(selected);
			invalidatePaint();
		}
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		if (this.highlighted != highlighted) {
			this.highlighted = highlighted;
			arrowHead.setHighlighted(highlighted);
			invalidatePaint();
		}
	}

	public void updateArcPath() {
		reset();

		DefaultGraphNode srcNode = (DefaultGraphNode) src;
		DefaultGraphNode destNode = (DefaultGraphNode) dest;

		// invert the arc path which will invert the arrowhead
		Rectangle2D srcBounds = (isInverted() ? destNode.getBounds() : srcNode.getBounds());
		Rectangle2D destBounds = (isInverted() ? srcNode.getBounds() : destNode.getBounds());
		double startX = srcBounds.getCenterX();
		double startY = srcBounds.getCenterY();
		double endX = destBounds.getCenterX();
		double endY = destBounds.getCenterY();

		if (src == dest) {
			final double diam = SELF_ARC_DIAMETER * (curveFactor + 1);
			startX = (float) (srcBounds.getX() + srcBounds.getWidth() - 1);
			endX = startX;
			moveTo((float) startX, (float) startY);
			append(new Ellipse2D.Double(startX, startY - diam / 2.0, diam, diam), false);

			arrowHead.setPoint(new Point2D.Double(startX + diam, startY));
			arrowHead.setSlope(-Double.MAX_VALUE);
		} else {
			Segment segment = new Segment(startX, startY, endX, endY);
			arrowHead.setSlope(segment.getSlope());

			moveTo((float) startX, (float) startY);
			if (curveFactor == 0) {
				lineTo((float) endX, (float) endY);
				arrowHead.setPoint(segment.getMidPoint());
			} else {
				// the distance that the ctrl point should be offset in the y direction
				double lineLength = segment.getLineLength();
				double yOffset = (curveFactor + 1) * lineLength / CURVE_FACTOR_BASE_OFFSET;
				Point2D.Double arrowHeadPoint = new Point2D.Double(lineLength / 2.0, yOffset / 2.0);
				Point2D arrowHeadPointT = segment.getLineTransform().transform(arrowHeadPoint, new Point2D.Double());
				arrowHead.setPoint(arrowHeadPointT);

				Point2D ctrlPoint = new Point2D.Double(lineLength / 2.0, yOffset);
				Point2D ctrlPointT = segment.getLineTransform().transform(ctrlPoint, new Point2D.Double());
				quadTo((float) ctrlPointT.getX(), (float) ctrlPointT.getY(), (float) endX, (float) endY);
			}
		}
		arrowHead.setPointRight(endX >= startX);
		
		invalidatePaint();
		invalidateFullBounds();
	}
	
	private Point2D getIconLocation(double slope, boolean pointRight, Point2D point) {
		double theta1 = Math.atan(slope);
		double iconHeight = 4;
		double iconWidth = 8;
		if (selected) {
		    iconHeight *= 1.5;
		}
		if (highlighted) {
			iconHeight *= 1.5;
		}
		iconHeight += 5;
		double dx = iconHeight * Math.cos(theta1) * 1.25;
		double dy = iconHeight * Math.sin(theta1) * 1.25;

		double iconX;
		double iconY;
		if (pointRight) {
			iconX = point.getX() + dx - iconWidth;
			iconY = point.getY() + dy - iconWidth;
		} else {
			iconX = point.getX() - dx - iconWidth;
			iconY = point.getY() - dy - iconWidth;
		}

		return new Point2D.Double(iconX, iconY);
	}
	
	public boolean equals(Object o) {
		GraphArc arc = (GraphArc)o;
		return this.getUserObject().equals(arc.getUserObject());
	}
	
	public int hashCode() {
		return this.getUserObject().hashCode();
	}

	public LayoutEntity getSourceInLayout() {
		return src;
	}

	public LayoutEntity getDestinationInLayout() {
		return dest;
	}

	public void setLayoutInformation(Object layoutInformation) {
		this.layoutObject = layoutInformation;
	}

	public Object getLayoutInformation() {
		return layoutObject;
	}

	public Object getGraphData() {
		return graphData;
	}
	
	public void setGraphData(Object data) {
		this.graphData = data;
	}
	
	public void clearBendPoints() {}

	public void setBendPoints(LayoutBendPoint[] bendPoints) {}

	public void populateLayoutConstraint(LayoutConstraint constraint) {}

	@Override
	public Stroke getStroke() {
		return style.getStroke(this);
	}

	@Override
	public Paint getStrokePaint() {
		return style.getPaint(this);
	}

	@Override
	protected void paint(PPaintContext paintContext) {
		Graphics2D g2 = paintContext.getGraphics();

		Stroke stroke = getStroke();
		Paint paint = getStrokePaint();
		if ((stroke != null) && (paint != null)) {
			g2.setPaint(paint);
			g2.setStroke(stroke);
			g2.draw(getPathReference());
			
			if (showArrowHead) {
				Shape shape = arrowHead.getShape();
				// first fill the arrow head in white
				g2.setPaint(ArrowHead.FILL);
				g2.fill(shape);
				// now draw the outline
				g2.setPaint(paint);
				g2.setStroke(ArrowHead.STROKE);
				g2.draw(shape);
			}
			
			if(image != null) {
				Point2D p = getIconLocation(arrowHead.getSlope(), arrowHead.isPointRight(), arrowHead.getPoint());
				image.setX(p.getX());
				image.setY(p.getY());
			}
		}
	}

	private class Segment {

		private double lineSlope;
		private double lineLength;
		private Point2D srcPtT;
		private Point2D midPtT;
		private Point2D destPtT;
		private AffineTransform lineT;

		/**
		 * Build a segment from the specified source to the specified destination
		 * 
		 * @param srcX
		 * @param srcY
		 * @param destX
		 * @param destY
		 */
		public Segment(double srcX, double srcY, double destX, double destY) {

			double lineDx = destX - srcX;
			double lineDy = destY - srcY;
			this.lineSlope = lineDy / lineDx;

			double lineTheta = Math.atan(-(1.0 / lineSlope));
			double lineAngle = (lineDy < 0) ? lineTheta + (3.0 / 2.0) * Math.PI : lineTheta + (1.0 / 2.0) * Math.PI;
			this.lineLength = Point2D.distance(srcX, srcY, destX, destY);

			lineT = new AffineTransform();
			lineT.concatenate(AffineTransform.getRotateInstance(lineAngle, srcX, srcY));
			lineT.concatenate(AffineTransform.getTranslateInstance(srcX, srcY));

			Point2D.Double srcPt = new Point2D.Double(0, 0);
			Point2D.Double destPt = new Point2D.Double(lineLength, 0);
			Point2D.Double midPt = new Point2D.Double(lineLength / 2.0, 0);
			srcPtT = lineT.transform(srcPt, new Point2D.Double());
			midPtT = lineT.transform(midPt, new Point2D.Double());
			destPtT = lineT.transform(destPt, new Point2D.Double());
		}

		public double getSlope() {
			return lineSlope;
		}

		public double getLineLength() {
			return lineLength;
		}

		public Point2D getStartPoint() {
			return srcPtT;
		}

		public Point2D getMidPoint() {
			return midPtT;
		}

		public Point2D getEndPoint() {
			return destPtT;
		}

		public AffineTransform getLineTransform() {
			return lineT;
		}

	}
}
