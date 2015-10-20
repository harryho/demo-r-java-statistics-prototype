package com.rm.app.r.component.explanation.statement;

import com.rm.app.r.flow.RFlowProperties;

public class RExSumSetterStatement implements RExStatement {

	public String getRCommand(RFlowProperties properties) {

		return properties.getSummaryResultVar() + "<-summary("
				+ properties.getSummaryParam() + ") \n  ";
	}

	public String formatDisplay(String callBack, RFlowProperties properties) {
		return "";
	}

}
