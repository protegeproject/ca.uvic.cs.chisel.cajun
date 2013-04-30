package ca.uvic.cs.chisel.cajun.graph;

public interface GraphItem {

	public static final String UNKNOWN_TYPE = "Unknown";

	public Object getUserObject();

	public Object getType();

	public boolean isSelected();
	public void setSelected(boolean selected);

	public boolean isHighlighted();
	public void setHighlighted(boolean highlighted);

	public String getTooltip();
	public void setTooltip(String tooltip);

	public void addAttribute(Object key, Object value);
	public Object getAttribute(Object key);

	public void moveToFront();
	public void moveToBack();
}
