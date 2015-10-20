package com.rm.app.r.component.explanation.statement;

import com.rm.app.r.flow.RFlowProperties;

public class RExCoefTableStatement implements RExStatement {

	public String getRCommand(RFlowProperties properties) {
		return properties.getSummaryResultVar() + "$coef";
	}
	public String formatDisplay(String callBack,
			RFlowProperties properties) {
		return callBack;
	}
}
