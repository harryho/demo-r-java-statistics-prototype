package com.rm.app.r.component.estimation.statement;

import java.util.List;

import com.rm.app.r.flow.RFlowProperties;

public class REsGlmMleStatement implements REsStatement {

    public String callBackHandler(String callBack, RFlowProperties properties) {
	// TODO Auto-generated method stub
	return null;
    }

    public String getRCommand(RFlowProperties properties) {
	StringBuffer tmp = new StringBuffer(properties.getSummaryParam() + " <- glm").append("(");
	String depend = properties.getDepend_var().get(0);
	String independ = "";
	for (int l_i = 0, l_k = properties.getIndepend_var().size(); l_i < l_k; l_i++) {
	    if (l_i == 0) {
		independ = properties.getIndepend_var().get(l_i);
	    } else {
		independ = independ + " + " + properties.getIndepend_var().get(l_i);
	    }

	}
	tmp.append(depend).append(" ~ ").append(independ);
	tmp.append(",").append("family=").append(properties.getFamily()).append(",").append(
		properties.getDataParaName()).append(")");
	return tmp.toString();
    }

}
