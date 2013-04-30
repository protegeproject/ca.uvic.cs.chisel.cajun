package ca.uvic.cs.chisel.cajun.actions;

import ca.uvic.cs.chisel.cajun.graph.util.AnimationHandler;
import ca.uvic.cs.chisel.cajun.resources.ResourceHandler;

public class FocusOnHomeAction extends CajunAction {
	private static final long serialVersionUID = 2406231898001180745L;

	private static final String ACTION_NAME = "Focus On Home";
	
	private AnimationHandler handler;
	
	public FocusOnHomeAction(AnimationHandler handler) {
		super(ACTION_NAME, ResourceHandler.getIcon("icon_home.gif"));
		this.handler = handler;
	}
	
	@Override
	public void doAction() {
		handler.focusOnExtents(true);
	}
	
}
