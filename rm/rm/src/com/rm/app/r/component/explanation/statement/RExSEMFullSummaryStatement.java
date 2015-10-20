package com.rm.app.r.component.explanation.statement;

import com.rm.app.r.flow.RFlowProperties;

public class RExSEMFullSummaryStatement implements RExStatement{

	public String formatDisplay(String callBack, RFlowProperties properties) {
		return callBack;
	}

	public String getRCommand(RFlowProperties properties) {
		return properties.getSummaryResultVar();
	}

}
