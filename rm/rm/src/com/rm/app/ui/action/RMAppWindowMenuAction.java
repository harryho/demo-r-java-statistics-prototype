package com.rm.app.ui.action;

import java.awt.event.ActionEvent;

import com.rm.app.log.RMLogger;
import com.rm.app.r.RHelper;

public class RMAppWindowMenuAction extends RMAppAction {

	private static final String MDISPLAY = "mdisplay";

	public RMAppWindowMenuAction(String name) {
		super(name);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		if (MDISPLAY.equals(this.getActionObjectName())) {
			if (!RHelper.displayCurrentOutputWindow()) {
				RMLogger.info("Not any output window.");
			}
		}
	}

	public static class AllActions implements Bundle {

		public RMAppAction actionDisplay;

		public AllActions() {
			actionDisplay = new RMAppWindowMenuAction(MDISPLAY);
			//	    
		}

		public RMAppAction[] getActions() {
			return new RMAppAction[] { actionDisplay };
		}

		public void update() {
			actionDisplay.setEnabled(true);
		}

	}
};
