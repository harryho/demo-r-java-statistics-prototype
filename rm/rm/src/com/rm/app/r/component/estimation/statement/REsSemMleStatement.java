package com.rm.app.r.component.estimation.statement;

import java.util.List;

import com.rm.app.r.flow.RFlowProperties;

public class REsSemMleStatement implements REsStatement {

    public String callBackHandler(String callBack, RFlowProperties properties) {
	// TODO Auto-generated method stub
	return null;
    }

    public String getRCommand(RFlowProperties properties) {
	StringBuffer tmp = new StringBuffer(properties.getSummaryParam() + " <- sem").append("(").append(
		properties.getSemModelVar()).append(",").append(properties.getDataParaName()).append(",").append(
		properties.getNumOfObs()).append(",").append("fixed.x=c(");
	List<String> list = properties.getExoVars();
	int i = 1;
	for (String exo : list) {
	    if (i++ == list.size())
		tmp.append("'").append(exo).append("'");
	    else
		tmp.append("'").append(exo).append("'").append(",");

	}
	tmp.append(")) \n");
	return tmp.toString();
    }

}
