package com.rm.app.r.component.explanation.statement;

import com.rm.app.r.flow.RFlowProperties;

public class RExPowerStateStatement implements RExStatement {

	public String getRCommand(RFlowProperties properties) {

		return "round(" + properties.getSummaryResultVar()
				+ "[['adj.r.squared']], digits=2)*100  \n";
	}

	public String formatDisplay(String callBack, RFlowProperties properties) {
		// "The fitted model has an explanatory power of  90%."
		// callback is like: [1] 94
		StringBuffer sb = new StringBuffer();
		int index = callBack.indexOf("]");
		if (index != -1) {
			callBack = callBack.replaceAll("\n", "");
			sb.append("The fitted model has an explanatory power of ").append(
					callBack.substring(index + 1).trim());
			sb.append("%.");
		}

		return sb.toString();
	}
}
