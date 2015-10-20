package com.rm.app.ui.action;

import java.awt.event.ActionEvent;

import com.rm.app.RMAppContext;
import com.rm.app.log.RMLogger;
import com.rm.app.r.RHelper;

public class RMAppToolbarAction extends RMAppAction {

	public RMAppToolbarAction(String name) {
		super(name);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		if ("openfile".equals(this.getActionObjectName())) {
			doOpenFileAction(e);
		} else if ("search".equals(this.getActionObjectName())) {
			doSearchAction(e);
		} else if ("collaboration".equals(this.getActionObjectName())) {
			doCollabAction(e);
		} else if ("rmaps".equals(this.getActionObjectName())) {
			doOpenRmapsAction(e);
		} else if ("help".equals(this.getActionObjectName())) {
			ActionHelper.doHelpAction(e);
		} else if ("display".equals(this.getActionObjectName())) {
			doDisplayAction(e);
		} else if ("run".equals(getActionObjectName())) {
			doRunAction(e);
		} else if ("terminal".equals(getActionObjectName())) {
			doStopAction(e);
		}
	}

	private void doOpenFileAction(ActionEvent event) {
		ActionHelper.doFileOpenActionWithChooser(RMAppContext.rmCompanyFlowPath);
	}

	private void doSearchAction(ActionEvent event) {

	}

	private void doCollabAction(ActionEvent event) {

	}

	private void doOpenRmapsAction(ActionEvent event) {

	}

	private void doDisplayAction(ActionEvent event) {

		if (!RHelper.displayCurrentOutputWindow()) {
			RMLogger.info("Not any output window.");
		}
	}

	private void doRunAction(ActionEvent e) {
		RMAppRMAction.doRunAction(e);
	}

	private void doStopAction(ActionEvent e) {
		RMAppRMAction.doStopAction(e);
	}
}
