package ca.uvic.cs.chisel.cajun.actions;

import java.awt.event.ActionEvent;

import ca.uvic.cs.chisel.cajun.graph.handlers.PNormalZoomHandler;
import ca.uvic.cs.chisel.cajun.resources.ResourceHandler;
import edu.umd.cs.piccolo.PCamera;

public class ZoomOutAction extends CajunAction {
	private static final long serialVersionUID = -1252312002328728298L;
	private PNormalZoomHandler zoom;

	public ZoomOutAction(PCamera camera) {
		super("Zoom Out", ResourceHandler.getIcon("icon_zoom_out.gif"));
		this.zoom = new PNormalZoomHandler(camera);
		this.zoom.setShrinkScale(0.9);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// zoom one step
		zoom.zoomOutOneStep();
	}
	
}
