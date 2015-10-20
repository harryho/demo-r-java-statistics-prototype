package com.rm.app.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JTree;

import com.rm.app.RMAppContext;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.r.RHelper;
import com.rm.app.ui.tool.RMFlowTreeNode;

public class RMAppFTAction extends RMAppAction {

	public static final String FT_ADD = "ftadd";

	public static final String FT_NEW = "ftnew";

	public static final String FT_DELETE = "ftdelete";
	
	public static final String FT_OPEN = "ftopen";

	public RMAppFTAction(String name) {
		super(name);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void actionPerformed(ActionEvent e) {
		if (FT_ADD.equals(getActionObjectName())) {
			doFTAddAction(e);
		} else if (FT_NEW.equals(getActionObjectName())) {
		    doFTNewAction(e);
		} else if (FT_DELETE.equals(getActionObjectName())) {
		    System.out.println(" FT_DELETE ");
		    doFTDeleteAction(e);
		}else if (FT_OPEN.equals(getActionObjectName())) {
		    System.out.println(" FT_OPEN ");
		    doFTOpenAction(e);
		}
	}

	private void doFTOpenAction(ActionEvent e) {
	//	 ActionHelper.doFlowOpenAction( e);
		JTree flowTree = RMAppContext.flowTree;
		Object object = flowTree.getLastSelectedPathComponent();
		RMFlowTreeNode flowTreeNode = object instanceof RMFlowTreeNode ? (RMFlowTreeNode) object
				: null;

		if (flowTreeNode != null) {
			String fileName = flowTreeNode.getFile().getPath();
			ActionHelper.openRFlow(fileName);
		}
	   
	}

	private void doFTDeleteAction(ActionEvent e) {
	    
	    ActionHelper.doFTDeleteAction();
	}

	private void doFTAddAction(ActionEvent e) {
	    // TODO Auto-generated method stub
	    
	    ActionHelper.doFTAddAction(e);
	}
	
	private void doFTNewAction(ActionEvent e){
	    ActionHelper.doFTNewAction();
	}

	public static void doRunAction(ActionEvent e) {
		ActionHelper.doRunAction();

	}

	public static void doStopAction(ActionEvent e) {
		RHelper.stopReasearchFlow();
	}

	public static void doEditAction(ActionEvent e) {
		RMGraphCell cell = RMAppContext.cell;
		if (cell != null) {
			ActionHelper.doRMComponentEditAction(cell);
		}

	}

	public static class AllActions implements Bundle {

		RMAppAction action_ftadd;

		RMAppAction action_ftnew;

		RMAppAction action_ftdelete;
		
		RMAppAction action_ftopen;

		public AllActions() {
			action_ftadd = new RMAppFTAction(FT_ADD);
			action_ftnew = new RMAppFTAction(FT_NEW);
			action_ftdelete = new RMAppFTAction(FT_DELETE);			
			action_ftopen =  new RMAppFTAction(FT_OPEN);
			//			    
		}

		public RMAppAction[] getActions() {
			return new RMAppAction[] { action_ftadd, action_ftnew, action_ftdelete, action_ftopen};
		}

		public void update() {
			action_ftadd.setEnabled(true);
			action_ftnew.setEnabled(true);
			action_ftdelete.setEnabled(true);
			action_ftopen.setEnabled(true);
		}

	}
}
