package com.rm.app.ui.action;

import java.awt.event.ActionEvent;

import com.rm.app.RMAppContext;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.log.RMLogger;
import com.rm.app.r.RHelper;

public class RMAppRMAction extends RMAppAction {

	private static final String M_RUN = "mrun";

	private static final String M_STOP = "mstop";

	private static final String M_EDIT = "medit";

	public RMAppRMAction(String name) {
		super(name);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		String name = getActionObjectName();
		RMLogger.debug("==========> i want to stop! "+name);
		if (M_RUN.equals(name)) {
			doRunAction(e);
		} else if (M_STOP.equals(name)) {
			doStopAction(e);
		} else if (M_EDIT.equals(name)) {
			doStopAction(e);
		}
	}

	public static void doRunAction(ActionEvent e) {
		ActionHelper.doRunAction();

	}

	public static void doStopAction(ActionEvent e) {
		RHelper.stopReasearchFlow();
	}

	public static void doEditAction(ActionEvent e) {
		// Object cell =
		// RMAppContext.getGraph().getFirstCellForLocation(e.getX(), e.getY());
		RMGraphCell cell = RMAppContext.cell;
		if (cell != null) {
			ActionHelper.doRMComponentEditAction(cell);
		}

	}

	public static class AllActions implements Bundle {

		RMAppAction action_mrun;

		RMAppAction action_mstop;

		RMAppAction action_medit;

		public AllActions() {
			action_mrun = new RMAppRMAction(M_RUN);
			action_mstop = new RMAppRMAction(M_STOP);
			action_medit = new RMAppRMAction(M_EDIT);
			//			    
		}

		public RMAppAction[] getActions() {
			return new RMAppAction[] { action_mrun, action_mstop, action_medit };
		}

		public void update() {
			action_mrun.setEnabled(true);
			action_mstop.setEnabled(true);
			action_medit.setEnabled(true);

		}

	}
}
