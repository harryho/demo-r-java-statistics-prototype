package com.rm.app.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.rm.app.RMAppContext;
import com.rm.app.log.RMLogger;


public class RMAppHelpMenuAction extends RMAppAction {

    private static final String ABOUT = "about";

    private static final String DOCUMENT = "document";

    private final String aboutus_info = "RM4Es Demo System V1.0";

    public RMAppHelpMenuAction(String name) {
	super(name);
    }

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
	  RMLogger.debug("  --------------------> "+getActionObjectName() );
	if (ABOUT.equals(getActionObjectName())) {
	    this.doAboutUsAction(e);
	} else if (DOCUMENT.equals(getActionObjectName())) {
	  
	    ActionHelper.doHelpAction(e);
	}

    }

    private void doAboutUsAction(ActionEvent e) {
	JOptionPane.showMessageDialog(RMAppContext.getRMApp(), aboutus_info);
    }

    public static class AllActions implements Bundle {

	private RMAppAction actionAboout, actionDocument;

	public AllActions() {
	    actionAboout = new RMAppHelpMenuAction(ABOUT);
	    actionDocument = new RMAppHelpMenuAction(DOCUMENT);
	}

	public RMAppAction[] getActions() {
	    return new RMAppAction[] { actionAboout, actionDocument };
	}

	public void update() {
	    actionAboout.setEnabled(true);
	    actionDocument.setEnabled(true);
	}

    }

}
