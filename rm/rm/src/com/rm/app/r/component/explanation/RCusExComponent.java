package com.rm.app.r.component.explanation;

import com.rm.app.r.RMEngine;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.component.explanation.statement.RExStatement;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.r.flow.output.TextWindow;
import com.rm.app.ui.RMFlowRunningDialog;

public class RCusExComponent extends RDefaultEXComponent {

    private String key;

    public int[] getCmds() {
	return cmds;
    }

    public void setCmds(int[] cmds) {
	this.cmds = cmds;
    }

    private int[] cmds = null;

    public RCusExComponent(String key) {
	super(key);
	this.key = key;
    }

    public String getKey() {
	return key;
    }

    public String getRCommand(RFlowProperties properties) {
	return null;
    }

    public long holdedMS() {
	return 1500;
    }

    public int handleCallBack(StringBuffer sb, RFlowProperties properties) {

	try {
	     new RMFlowRunningDialog(this,properties);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	if (null != cmds) {
	    StringBuffer output = new StringBuffer();
	    for (int rcmd : cmds) {
		RExStatement statement = getRExStatement(rcmd);
		String strCmd = statement.getRCommand(properties);
		String str = "";
		if (strCmd != null) {
		    str = RMEngine.executeAndWaitReturn(strCmd, 200, false);
		}
		System.out.println(str);
		str = statement.formatDisplay(str, properties);
		System.out.println( " str ==== "+str);
		output.append(str);
		output.append("\n");
		output.append("\n");
	    }
	    TextWindow.info.add(output.toString());
	}

	return RComponent.SUCEESS;
    }

    public boolean validation() throws Exception {
	return true;
    }

}
