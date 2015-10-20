package com.rm.app.r.component.estimation;

import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.RMEngine;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.component.equation.REQComponent;
import com.rm.app.r.component.estimation.statement.REsGlmMleStatement;
import com.rm.app.r.component.estimation.statement.REsSemMleStatement;
import com.rm.app.r.component.estimation.statement.REsStatement;
import com.rm.app.r.flow.RFlowProperties;

public class RMLEComponent implements RESComponent {

    private String key;
    private RFlowProperties properties;
    
    static int glmMleCmd = REsStatement.RCMD_GLM_MLE;
    static int semMleCmd = REsStatement.RCMD_SEM_MLE;

    public RMLEComponent(String key, RFlowProperties properties) {
	this.key = key;
	this.properties = properties;
    }

    public String getRCommand(RFlowProperties properties) {
	// StringBuffer tmp = new StringBuffer(properties.getSummaryParam()
	// + " <- sem").append("(").append(properties.getSemModelVar())
	// .append(",").append(properties.getDataParaName()).append(",")
	// .append(properties.getNumOfObs()).append(",").append(
	// "fixed.x=c(");
	// List<String> list = properties.getExoVars();
	// int i = 1;
	// for (String exo : list) {
	// if (i++ == list.size())
	// tmp.append("'").append(exo).append("'");
	// else
	// tmp.append("'").append(exo).append("'").append(",");
	//			
	// }
	// tmp.append(")) \n");
	// return tmp.toString();
	return null;
    }
    
    
    
    public static int getCmds(String eqType) {
	int rcmds = 0;
	RMLogger.debug("Eq Type:" + eqType);
	if (REQComponent.TYPE_EQUATION_GLM.equals(eqType)) {
		rcmds = glmMleCmd;
	} else if (REQComponent.TYPE_EQUATION_SEM.equals(eqType)) {
		rcmds = semMleCmd;
	}
	return rcmds;
}
    
    public REsStatement getREsStatement(int cmdKey) {
	REsStatement statement = null;
	switch (cmdKey) {
	    case REsStatement.RCMD_GLM_MLE:
		statement = new REsGlmMleStatement();
		break;
	    case REsStatement.RCMD_SEM_MLE:
		statement = new REsSemMleStatement();
		break;

	}
	return statement;
    }

    public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
	String str = null;
	int cmd = getCmds(properties.getEqType()); 
	REsStatement esStatement = getREsStatement(cmd);
	String rcmd = esStatement.getRCommand(properties);
	if (rcmd != null) {
		str = RMEngine.executeAndWaitReturn(rcmd, 200, false);
	}
	
	if (str.indexOf("Error") > -1) {
		RMLogger
				.error("Error found. Please check your flow and data.");
		RMLogger.info(str);
		return RComponent.ERROR;
	} 
	
	System.out.println(rcmd);
	RMLogger.debug(str);

	return RComponent.SUCEESS;
    }

    public String getKey() {
	return key;
    }

    public long holdedMS() {
	return 500;
    }

    public boolean validation() throws RMException {
//	if (properties.getEndoVars() == null || properties.getEndoVars().size() < 1) {
//	    throw new RMException("Endogeneous variable is not available !");
//	}
//	if (properties.getExoVars() == null || properties.getExoVars().size() < 1) {
//	    throw new RMException("Exogenous variable is not available !");
//	}
//	if (properties.getSemEquations() == null || properties.getSemEquations().size() < 1) {
//	    throw new RMException("Equation is not available !");
//	}
	return true;
    }

}
