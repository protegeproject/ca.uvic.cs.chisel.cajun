/**
 * Copyright 1998-2007, CHISEL Group, University of Victoria, Victoria, BC, Canada.
 * All rights reserved.
 */
package ca.uvic.cs.chisel.cajun.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * 
 * @author Rob Lintern
 */
public class GeometryUtils {
	
	/**
	 * @param theta 	Angle of line from (0,0), in radians
	 * @param r 		Radius of the circle.
	 * @param h 		X coordinate of centre of circle
	 * @param k 		Y coordinate of centre of circle
	 * @param result 	An array of 2 Point2D objects. Returns the 0, 1, or 2 points of intersection.
	 * 					result [0] is the first point of intersection, result[1] is the sectod point of intersection
	 * 					Either may be null.
	 */
	public static void circleLineIntersection(double theta, double r, double h, double k, Point2D[] result) {
		if (result.length != 2) {
			(new Exception ("result must have length 2!")).printStackTrace();
			return;
		}
		
		/* equation of a line, y=m*x+b, since one point at 0,0 and m=dy/dx=tan(theta) this line becomes y=tan(theta)*x
		   equation of a circle, (x-h)^2 + (y-k)^2 = r^2 where (h,k) is the center of the circle and r is its radius
		
		   after a bit of algebra and the quadratic equation, solving for x gives ...
		  		x = (-b +/- sqrt(b^2 - 4*a*c))/2*a
		    	where 	a = 1 + tan(theta)^2,
		    			b = -2*h - 2*k*tan(theta), and
		    			c = h^2 + k^2 - r^2
		   
		   *** Note: There is absolutely no consideration of extreme values or other checks
		   * for computational errors in the following code (ex. when theta approaches PI/2, tan(theta) approaches infinity)
		   * As of Aug 2003, this method is only used to find the intersection of arcs with the corners of rounded rectangles
		   * so these extremes do not come into play.
		*/
		double tanTheta = Math.tan(theta);
		double a = 1.0 + Math.pow(tanTheta,2);
		double b = -2.0*h - 2.0*k*tanTheta;
		double c = Math.pow(h, 2) + Math.pow(k, 2) - Math.pow(r, 2);
		
		try {
			double x1 = (-b + Math.sqrt(Math.pow(b,2) - 4.0*a*c))/(2.0*a);
			double y1 = x1 * tanTheta;
			result[0] = new Point2D.Double (x1, y1);
		} catch (RuntimeException e) {
			e.printStackTrace();
			result[0] = null;
		}
		try {
			double x2 = (-b - Math.sqrt(Math.pow(b,2) - 4.0*a*c))/(2.0*a);
			double y2 = x2 * tanTheta;
			result[1] = new Point2D.Double (x2, y2);
		} catch (RuntimeException e1) {
			e1.printStackTrace();
			result[1] = null;
		}
	}

	/**
	 * Rotate <code>angle</code> by PI radians.
	 * @param angle a double value between PI and -PI
	 * @return a double value between PI and -PI
	 */
	public static double rotateByPI(double angle) {
		if (angle > 0.0d) {
			return (angle - Math.PI);
		}
		return (angle + Math.PI);
	}

	/**
	  * Modify the object by applying the given transform.
	  * @param tf the AffineTransform to apply.
	  */
	 public static Rectangle2D.Double transform(Rectangle2D.Double rect, AffineTransform tf) {
		 double x = rect.x;
		 double y = rect.y;
		 double width = rect.width;
		 double height = rect.height;
		 
		 // First, transform all 4 corners of the rectangle
		 double[] pts = new double[8];
		 pts[0] = x;          // top left corner
		 pts[1] = y;
		 pts[2] = x + width;  // top right corner
		 pts[3] = y;
		 pts[4] = x + width;  // bottom right corner
		 pts[5] = y + height;
		 pts[6] = x;          // bottom left corner
		 pts[7] = y + height;
		 tf.transform(pts, 0, pts, 0, 4);
	
		// Then, find the bounds of those 4 transformed points.
		 double minX = pts[0];
		 double minY = pts[1];
		 double maxX = pts[0];
		 double maxY = pts[1];
		 int i;
		 for (i=1; i<4; i++) {
			 if (pts[2*i] < minX) {
				 minX = pts[2*i];
			 }
			 if (pts[2*i+1] < minY) {
				 minY = pts[2*i+1];
			 }
			 if (pts[2*i] > maxX) {
				 maxX = pts[2*i];
			 }
			 if (pts[2*i+1] > maxY) {
				 maxY = pts[2*i+1];
			 }
		 }
		 
		 Rectangle2D.Double transformedRect = new Rectangle2D.Double (minX, minY, maxX - minX, maxY - minY);
		 return transformedRect;
	 }
	 
	/**
	 * Compares the x, y, width, and height values of each Rectangle2D.
	 * If the difference between each of them is within the given delta then true is returned.
	 * @param bounds1	The first rectangle
	 * @param bounds2	The second rectangle
	 * @param delta		The upper limit on the difference between 2 values that are considered the same
	 * @return boolean	if the two bounds are the same with the given delta
	 */
	public static boolean compareBounds(Rectangle2D bounds1, Rectangle2D bounds2, double delta) {
		if ((bounds1 == null) || (bounds2 == null)) {
			return false;
		}
		
		double dx = Math.abs(bounds1.getX() - bounds2.getX());
		double dy = Math.abs(bounds1.getY() - bounds2.getY());
		double dw = Math.abs(bounds1.getWidth() - bounds2.getWidth());
		double dh = Math.abs(bounds1.getHeight() - bounds2.getHeight());
		boolean same = ((dx <= delta) && (dy <= delta) && (dw <= delta) && (dh <= delta));
		return same;
	}
	 
}
