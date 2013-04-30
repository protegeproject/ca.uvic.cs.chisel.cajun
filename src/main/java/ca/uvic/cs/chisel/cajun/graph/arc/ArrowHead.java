/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.graph.arc;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolox.util.PFixedWidthStroke;

/**
 * Code for visualizing arrow heads. Note that this could have been written using
 * multiple subclasses, but doing it in a single class should make maintenance and
 * understanding easier.
 * @author Chris Bennett
 */
public class ArrowHead {

	public static final PFixedWidthStroke STROKE = new PFixedWidthStroke(1f);
	public static final Paint FILL = Color.white;

	private static final int ARROW_SIZE_CONSTANT = 4;

	private double slope;
	private Point2D point;
	private double magnification;
	private GeneralPath path;
	private boolean selected;
	private boolean highlighted;
	private boolean pointRight;
	private boolean closePath;
	
	private double headX;
	private double headY;
	
	public ArrowHead() {
		this.slope = 1;
		this.point = new Point2D.Double();
		this.magnification = 1;
		this.selected = false;
		this.highlighted = false;
		this.pointRight = false;
		this.closePath = true;
		this.path = null;
	}
	
	public void setSlope(double slope) {
		this.slope = slope;
		reset();
	}
	
	public void setPoint(Point2D point) {
		this.point = point;
		reset();
	}
	
	public void setMagnification(double magnification) {
		this.magnification = magnification;
		reset();
	}
	
	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			reset();
		}
	}
	
	public void setHighlighted(boolean highlighted) {
		if (this.highlighted != highlighted) {
			this.highlighted = highlighted;
			reset();
		}
	}
	
	public void setPointRight(boolean pointRight) {
		if (this.pointRight != pointRight) {
			this.pointRight = pointRight;
			reset();
		}
	}
	
	public void setClosePath(boolean closePath) {
		if (this.closePath != closePath) {
			this.closePath = closePath;
			reset();
		}
	}
	
	public boolean isPointRight() {
		return pointRight;
	}
	
	public Point2D getPoint() {
		return point;
	}

	public double getSlope() {
		return slope;
	}
	
	public Stroke getStroke() {
		return STROKE;
	}
		
	public void reset() {
		this.path = null;
	}
	
	public Shape getShape() {
		if (path == null) {
			path = createArrowHead();
		}
		return path;
	}
	
	public Point2D getHeadLocation() {
		return new Point2D.Double(headX, headY);
	}

	private GeneralPath createArrowHead() {
		GeneralPath arrowHeadPath = new GeneralPath();
		double theta1 = Math.atan(slope);
		double arrowHeight = ARROW_SIZE_CONSTANT / magnification;
		if (selected) {
		    arrowHeight *= 1.5;
		}
		if (highlighted) {
			arrowHeight *= 1.5;
		}
		double dx = arrowHeight * Math.cos(theta1) * 1.25;
		double dy = arrowHeight * Math.sin(theta1) * 1.25;

		//double headX;
		//double headY;
		double tailX;
		double tailY;
		if (pointRight) {
			headX = point.getX() + dx;
			headY = point.getY() + dy;
			tailX = point.getX() - dx;
			tailY = point.getY() - dy;
		} else {
			headX = point.getX() - dx;
			headY = point.getY() - dy;
			tailX = point.getX() + dx;
			tailY = point.getY() + dy;
		}

		double theta2 = Math.atan(- (1 / slope));
		dx = arrowHeight * Math.cos(theta2);
		dy = arrowHeight * Math.sin(theta2);

		double tailX1 = tailX + dx;
		double tailY1 = tailY + dy;
		double tailX2 = tailX - dx;
		double tailY2 = tailY - dy;
		arrowHeadPath.reset();
		arrowHeadPath.moveTo((float) tailX1, (float) tailY1);
		arrowHeadPath.lineTo((float) headX, (float) headY);
		arrowHeadPath.lineTo((float) tailX2, (float) tailY2);
		if (closePath) {
			arrowHeadPath.closePath();
		}
		return arrowHeadPath;
	}

}
