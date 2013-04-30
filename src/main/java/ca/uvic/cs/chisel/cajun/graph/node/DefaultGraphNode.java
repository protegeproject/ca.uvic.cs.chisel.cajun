package ca.uvic.cs.chisel.cajun.graph.node;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.zest.layouts.constraints.BasicEntityConstraint;
import org.eclipse.zest.layouts.constraints.LabelLayoutConstraint;
import org.eclipse.zest.layouts.constraints.LayoutConstraint;

import ca.uvic.cs.chisel.cajun.graph.arc.GraphArc;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Default graph node implementation. Displays some text and possible an image/icon.
 * 
 * @author Chris Callendar
 * @since  30-Oct-07
 */
public class DefaultGraphNode extends PNode implements GraphNode {
	private static final long serialVersionUID = 3223950711940456476L;
	
	private static final int ICON_GAP = 4;
	private static final int PADDING_X = 12;
	private static final int PADDING_Y = 6;
	protected static final int MAX_TEXT_CHARS = 15;
	protected static final int MAX_LINES = 2;

	private Object userObject;
	private String fullText;
	private Object type;
	private String tooltip;

	private Map<Icon, PImage> overlayIconMap = new HashMap<Icon, PImage>();
	private Collection<Icon> overlayIcons = new ArrayList<Icon>();

	private GraphTextNode textNode;
	private PImage pImage;
	private int iconWidth = 0;
	private int iconHeight = 0;

	private GraphNodeStyle style;

	private boolean selected;
	private boolean highlighted;
	private boolean matching;
	private boolean fixedLocation;

	private double xInLayout = 0;
	private double yInLayout = 0;
	protected double wInLayout = 0;
	protected double hInLayout = 0;
	private Object layoutInformation;
	
	private double xFactor, yFactor;
	private Object graphData;
	
	private List<ChangeListener> changeListeners;
	
	private Collection<GraphArc> arcs;

	public DefaultGraphNode(Object userObject) {
		this(userObject, String.valueOf(userObject));
	}

	public DefaultGraphNode(Object userObject, String text) {
		this(userObject, text, null);
	}

	public DefaultGraphNode(Object userObject, String text, Icon icon) {
		this(userObject, text, icon, null);
	}

	public DefaultGraphNode(Object userObject, String text, Icon icon, Object type) {
		super();
		this.userObject = userObject;
		
		this.changeListeners = new ArrayList<ChangeListener>();

		this.style = new DefaultGraphNodeStyle();
		this.selected = false;
		this.highlighted = false;
		this.matching = false;

		this.arcs = new ArrayList<GraphArc>();

		this.setPickable(true);
		this.setChildrenPickable(false);

		textNode = new GraphTextNode();
		// make this node match the text size
		textNode.setConstrainWidthToTextWidth(true);
		textNode.setConstrainHeightToTextHeight(true);
		textNode.setPickable(false);
		addChild(textNode);
		setText(text);
		setIcon(icon);
		setType(type);
	}
	
	public void removeChangeListener(ChangeListener l) {
		changeListeners.remove(l);
	}
	
	public void addChangeListener(ChangeListener l) {
		changeListeners.add(l);
	}

	public Object getUserObject() {
		return userObject;
	}

	public GraphNodeStyle getNodeStyle() {
		return style;
	}

	public void setNodeStyle(GraphNodeStyle style) {
		if ((style != null) && (this.style != style)) {
			this.style = style;
			invalidateFullBounds();
			invalidatePaint();
		}
	}

	public Object getType() {
		return type;
	}

	public void setType(Object type) {
		this.type = (type == null ? UNKNOWN_TYPE : type);
	}

	public boolean isVisible() {
		return getVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		// hide or show the arcs for this node
		for (GraphArc arc : arcs) {
			// this method handles whether or not to show the arc
			// checks if the src and dest nodes are visible
			arc.setVisible(visible);
		}
	}

	public Collection<GraphArc> getArcs() {
		return arcs;
	}

	public Collection<GraphArc> getArcs(boolean incoming, boolean outgoing) {
		Collection<GraphArc> graphArcs;
		if (incoming && outgoing) {
			graphArcs = getArcs();
		} else if (!incoming && !outgoing) {
			graphArcs = Collections.emptyList();
		} else {
			graphArcs = new ArrayList<GraphArc>();
			for (GraphArc arc : getArcs()) {
				if (incoming && (arc.getDestination() == this)) {
					graphArcs.add(arc);
				} else if (outgoing && (arc.getSource() == this)) {
					graphArcs.add(arc);
				}
			}
		}
		return graphArcs;
	}

	public void addArc(GraphArc arc) {
		if (!this.arcs.contains(arc)) {
			this.arcs.add(arc);
		}
	}

	public void removeArc(GraphArc arc) {
		this.arcs.remove(arc);
	}

	public Collection<GraphNode> getConnectedNodes() {
		ArrayList<GraphNode> connectedNodes = new ArrayList<GraphNode>();
		for (GraphArc arc : getArcs()) {
			GraphNode src = arc.getSource();
			GraphNode dest = arc.getDestination();
			GraphNode nodeToAdd = null;
			if (this == src) {
				nodeToAdd = dest;
			} else if (this == dest) {
				nodeToAdd = src;
			}
			if ((nodeToAdd != null) && !connectedNodes.contains(nodeToAdd)) {
				connectedNodes.add(nodeToAdd);
			}
		}
		return connectedNodes;
	}

	public boolean hasAttribute(Object key) {
		return (getAttribute(key) != null);
	}

	public void removeAttribute(Object key) {
		addAttribute(key, null);
	}

	public String getText() {
		return fullText;
	}

	public void setText(String s) {
		if (s == null) {
			s = "";
		}
		this.fullText = s;
		// TODO let user choose between eliding the label and splitting into lines?
		textNode.setText(splitTextIntoLines(s, MAX_LINES, MAX_TEXT_CHARS));
		updateBounds();
	}

	/**
	 * Restricts the number of characters in the text node. If the string is too long it is chopped
	 * and appended with "...".
	 * 
	 * @param text the string to possibly elide
	 * @return the elided string, or the original if text isn't longer than the max allowed chars
	 */
	protected String elideText(String text, int maxCharsPerLine) {
		if (text.length() > maxCharsPerLine) {
			return new String(text.substring(0, maxCharsPerLine).trim() + "...");
		}
		return text;
	}

	/**
	 * Splits the text into lines. Attempts to split at word breaks if possible. Also puts a cap on
	 * the max number of lines.
	 */
	protected String splitTextIntoLines(String text, int maxLines, int maxCharsPerLine) {
		text = text.trim();
		StringBuffer buffer = new StringBuffer(text.length() + 10);
		if (text.length() > maxCharsPerLine) {
			int lines = 0;
			while ((text.length() > 0) && (lines < maxLines)) {
				// base case #1 - text is short
				if (text.length() < maxCharsPerLine) {
					buffer.append(text);
					break;
				}
				// base case #2 - added max lines
				if ((lines + 1) == maxLines) {
					// elide the remaining text (s) instead of just the current line
					buffer.append(elideText(text, maxCharsPerLine));
					break;
				}

				// find a space and break on it
				int end = findWhiteSpace(text, maxCharsPerLine);
				if (end == -1) {
					end = Math.min(text.length(), maxCharsPerLine);
				}
				String line = text.substring(0, end).trim();
				if (line.length() == 0) {
					break;
				}

				buffer.append(line);
				buffer.append('\n');
				lines++;
				text = text.substring(end).trim();
			}
			return buffer.toString().trim();
		}
		return text;
	}

	private int findWhiteSpace(String s, int end) {
		int ws = -1;
		// look 2 characters past the end for a space character
		// and work backwards
		for (int i = Math.min(s.length() - 1, end + 2); i >= 0; i--) {
			if (Character.isWhitespace(s.charAt(i))) {
				ws = i;
				break;
			}
		}
		return ws;
	}

	@Override
	public String toString() {
		return getText();
	}

	public String getTooltip() {
		if (tooltip == null) {
			// use the full text, not the elided version from getText()
			return fullText;
		}
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public void setIcon(Icon icon) {
		if (pImage != null) {
			pImage.removeAllChildren();
			pImage.removeFromParent();
			pImage.getImage().flush();
		}
		if ((icon != null) && (icon instanceof ImageIcon)) {
			iconWidth = icon.getIconWidth();
			iconHeight = icon.getIconHeight();
			pImage = new PImage(((ImageIcon) icon).getImage());
			pImage.setPickable(false);
			addChild(pImage);
			updateBounds();
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			updateArcs();
			textNode.invalidatePaint();
			invalidatePaint();
		}
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		if (this.highlighted != highlighted) {
			this.highlighted = highlighted;
			bubbleNode();
			textNode.invalidatePaint();
			invalidatePaint();
		}
	}

	public boolean isMatching() {
		return matching;
	}

	public void setMatching(boolean matching) {
		if (this.matching != matching) {
			this.matching = matching;
			invalidatePaint();
		}
	}

	/**
	 * Scales the node back to normal size if the canvas is currently scaled below the regular size.
	 */
	protected void bubbleNode() {
		PCamera camera = ((PLayer) this.getParent()).getCamera(0);
		double viewScale = camera.getViewScale();
		
		if (highlighted) {
			if (viewScale < 1.0) {
				double scaleFactor = 1.0 / viewScale;
				
				double unscaledWidth = this.getGlobalBounds().width;
				double unscaledHeight = this.getGlobalBounds().height;
				double scaledWidth = this.getGlobalBounds().width * viewScale;
				double scaledHeight = this.getGlobalBounds().height * viewScale;
				
				this.scaleAboutPoint(scaleFactor, getX(), getY());
				
				xFactor = (unscaledWidth - scaledWidth) / 2;
				yFactor = (unscaledHeight - scaledHeight) / 2;
				this.translate(-1 * xFactor, -1 * yFactor);
			}
		} else {
			if(xFactor > 0) {
				this.translate(xFactor, yFactor);
				this.scaleAboutPoint(1.0 / getScale(), getX(), getY());
			}

			xFactor = 0;
			yFactor = 0;
		}
	}

	private void updateArcs() {
		for (GraphArc arc : arcs) {
			if (isSelected()) {
				arc.setSelected(true);
			} else {
				GraphNode src = arc.getSource();
				GraphNode dest = arc.getDestination();
				if (src == dest) {
					arc.setSelected(false);
				} else if (this == src) {
					arc.setSelected(dest.isSelected());
				} else if (this == dest) {
					arc.setSelected(src.isSelected());
				}
			}
		}
	}

	private void fireChangeListeners() {
		ChangeEvent event = new ChangeEvent(this);
		for(ChangeListener listener: changeListeners) {
			listener.stateChanged(event);
		}
	}
	
	private void updateArcLocations() {
		for (GraphArc arc : arcs) {
			arc.updateArcPath();
		}
	}

	/**
	 * Sets the bounds of this node based on the icon and text size. Takes into consideration the
	 * maximum node width too.
	 */
	private void updateBounds() {
		PBounds textBounds = textNode.getBounds();
		double w = (3 * PADDING_X) + iconWidth + ICON_GAP + textBounds.getWidth();
		double h = (2 * PADDING_Y) + Math.max(iconHeight, textBounds.getHeight());
		setBounds(getX(), getY(), w, h);
	}

	@Override
	public boolean setBounds(double x, double y, double width, double height) {
		// TODO handle maximum width?
		boolean changed = super.setBounds(x, y, width, height);

		if (changed) {
			if (pImage != null) {
				pImage.setBounds(getX() + PADDING_X, getY() + PADDING_Y, iconWidth, iconHeight);
			}
			textNode.setBounds(getX() + PADDING_X + iconWidth + ICON_GAP, getY() + PADDING_Y, textNode.getWidth(), textNode.getHeight());
			updateArcLocations();
			invalidatePaint();
			
			fireChangeListeners();
		}
		return changed;
	}

	public boolean setLocation(double x, double y) {
		setHighlighted(false);
		
		return setBounds(x, y, getWidth(), getHeight());
	}

	public boolean setSize(double w, double h) {
		return setBounds(getX(), getY(), w, h);
	}

	public double getCenterX() {
		return (getX() + (getWidth() / 2));
	}

	public double getCenterY() {
		return (getY() + (getHeight() / 2));
	}

	public Object getGraphData() {
		return graphData;
	}
	
	public void setGraphData(Object data) {
		this.graphData = data;
	}
	
	public double getXInLayout() {
		return xInLayout;
	}

	public double getYInLayout() {
		return yInLayout;
	}

	public double getWidthInLayout() {
		//return wInLayout;
		return getBounds().width;
	}

	public double getHeightInLayout() {
		//return hInLayout;
		return getBounds().height;
	}

	public void setLocationInLayout(double x, double y) {
		xInLayout = x;
		yInLayout = y;
	}

	public Object getLayoutInformation() {
		return layoutInformation;
	}

	public void setLayoutInformation(Object layoutInformation) {
		this.layoutInformation = layoutInformation;
	}

	public void setSizeInLayout(double width, double height) {
		wInLayout = width;
		hInLayout = height;
	}

	public boolean hasPreferredLocation() {
		return false;
	}

	/**
	 * Populate the specified layout constraint
	 */
	public void populateLayoutConstraint(LayoutConstraint constraint) {
		if (constraint instanceof LabelLayoutConstraint) {
			LabelLayoutConstraint labelConstraint = (LabelLayoutConstraint) constraint;
			labelConstraint.label = fullText;
			labelConstraint.pointSize = 18;
		} else if (constraint instanceof BasicEntityConstraint) {
			BasicEntityConstraint basicEntityConstraint = (BasicEntityConstraint) constraint;
			if (this.hasPreferredLocation()) {
				basicEntityConstraint.hasPreferredLocation = true;
				basicEntityConstraint.preferredX = this.getX();
				basicEntityConstraint.preferredY = this.getY();
			}
		}
	}

	public int compareTo(Object o) {
		if (o instanceof DefaultGraphNode) {
			DefaultGraphNode node = (DefaultGraphNode) o;
			return this.fullText.compareToIgnoreCase(node.fullText);
		}
		return 0;
	}

	@Override
	protected void paint(PPaintContext paintContext) {
		Graphics2D g2 = paintContext.getGraphics();

		PBounds bounds = getBounds();
		// shrink the bounds slightly to avoid painting outside the bounds
		bounds.setFrame(bounds.x + 1, bounds.y + 1, bounds.width - 2, bounds.height - 2);

		// can't be null
		Shape shape = style.getNodeShape(this, bounds);

		// these can be null
		Paint bg = style.getBackgroundPaint(this);
		Paint borderPaint = style.getBorderPaint(this);
		Stroke borderStroke = style.getBorderStroke(this);

		// gradients need to have the correct control points
		if (bg instanceof GradientPaint) {
			bg = updateGradientPaintPoints((GradientPaint) bg);
		}
		if (borderPaint instanceof GradientPaint) {
			borderPaint = updateGradientPaintPoints((GradientPaint) borderPaint);
		}

		// 1. paint the background shape
		if (bg != null) {
			g2.setPaint(bg);
			// Mac bug - doesn't fill the shape!
			//g2.fill(shape);
			Rectangle r = shape.getBounds();
			g2.fillRoundRect(r.x, r.y, r.width, r.height, 5, 5);
		}

		// 2. paint the border
		if ((borderPaint != null) && (borderStroke != null)) {
			g2.setPaint(borderPaint);
			g2.setStroke(borderStroke);
			g2.draw(shape);
		}

		addOverlayIcons(style.getOverlayIcons(this));
	}

	/**
	 * If necessary, creates the overlay icons as PImage's and adds them to this node as a child
	 * object. If it is already created, the overlayIcon is repositioned.
	 * 
	 * @param icon The icon to set as the overlayIcon.
	 */
	private void addOverlayIcons(Collection<Icon> icons) {
		if (icons == null || !icons.equals(overlayIcons)) {
			for (PImage overlayIcon : overlayIconMap.values()) {
				removeChild(overlayIcon);
			}
			overlayIconMap.clear();
		}
		if (icons != null) {
			for (Icon icon : icons) {
				PImage overlayIcon = overlayIconMap.get(icon);
				if (overlayIcon == null && icon != null) {
					overlayIcon = new PImage(((ImageIcon) icon).getImage());
					overlayIcon.setPickable(false);
					addChild(overlayIcon);
					overlayIconMap.put(icon, overlayIcon);
				}

				if(overlayIcon != null) {
					Point2D iconPosition = style.getOverlayIconPosition(this, icon);
					overlayIcon.setX(iconPosition.getX());
					overlayIcon.setY(iconPosition.getY());
				}
			}
		}

		overlayIcons = icons;
	}

	private GradientPaint updateGradientPaintPoints(GradientPaint gp) {
		int x = (int) getX();
		int y = (int) getY();
		int h = (int) getHeight();
		GradientPaint gradient = new GradientPaint(x, y, gp.getColor1(), x, y + h, gp.getColor2(), gp.isCyclic());
		return gradient;
	}

	class GraphTextNode extends PText {
		private static final long serialVersionUID = -871571524212274580L;
		
		private boolean ignoreInvalidatePaint = false;

		@Override
		public Font getFont() {
			Font font = style.getFont(DefaultGraphNode.this);
			if (font == null) {
				font = DEFAULT_FONT;
			}
			return font;
		}

		@Override
		public Paint getTextPaint() {
			Paint paint = style.getTextPaint(DefaultGraphNode.this);
			if (paint == null) {
				paint = Color.black;
			}
			return paint;
		}

		@Override
		protected void paint(PPaintContext paintContext) {
			// update the text paint - the super paint method doesn't call our getTextPaint() method
			Paint p = getTextPaint();
			if (!p.equals(super.getTextPaint())) {
				ignoreInvalidatePaint = true;
				setTextPaint(getTextPaint());
				ignoreInvalidatePaint = false;
			}
			// the font is never set in the super paint class?
			paintContext.getGraphics().setFont(getFont());
			super.paint(paintContext);
		}

		@Override
		public void invalidatePaint() {
			if (!ignoreInvalidatePaint) {
				super.invalidatePaint();
			}
		}

	}

	public boolean isFixedLocation() {
		return fixedLocation;
	}

	public void setFixedLocation(boolean fixedLocation) {
		this.fixedLocation = fixedLocation;
	}
}
