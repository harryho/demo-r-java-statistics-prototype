package com.rm.app.r.component.explanation.statement;

import com.rm.app.r.flow.RFlowProperties;

public class RExSigStateStatement implements RExStatement {

	public String getRCommand(RFlowProperties properties) {

		return properties.getSummaryParam() + "$call  \n";
	}

	public String formatDisplay(String callBack, RFlowProperties properties) {
		// "Dependent variable StackLoss is significantly affected by independent variables AirFlow and WaterTemp."
		StringBuffer sb = new StringBuffer();
		sb.append("Dependent variable ").append(
				properties.getDepend_var().get(0));
		sb.append(" is significantly affected by independent variables ");
		for (int i = 0, k = properties.getIndepend_var().size(); i < k; i++) {
			if (i == k - 2) {
				sb.append(properties.getIndepend_var().get(i)).append(" and ");
			} else if (i != k - 1) {
				sb.append(properties.getIndepend_var().get(i)).append(", ");
			} else {
				sb.append(properties.getIndepend_var().get(i)).append(".");
			}
		}
		return sb.toString();
	}
}
