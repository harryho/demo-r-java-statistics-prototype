package com.rm.app.r.component.explanation.statement;

import com.rm.app.r.flow.RFlowProperties;

public class RExFullPlotStatement implements RExStatement {

    public String getRCommand(RFlowProperties properties) {

	String cmd = "png(filename = '" + properties.getPngFile()
		+ "',width = 480, height = 480, pointsize = 12, bg = 'white') \n " +
				"par(mfrow=c(2,2)) \n";
	 cmd =cmd + "plot(" + properties.getSummaryParam() + ") \n";
	 
	 return cmd;
    }

    public String formatDisplay(String callBack, RFlowProperties properties) {
    	return callBack;
    }
}
