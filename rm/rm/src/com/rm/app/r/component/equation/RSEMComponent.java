package com.rm.app.r.component.equation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rm.app.log.RMLogger;
import com.rm.app.r.RMEngine;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;

public class RSEMComponent implements REQComponent {

    private String key;

    public RSEMComponent(String key) {
	this.key = key;
    }

    private static String PRE_MODEL_SPEC = "varInd<-function(){ vc<-colnames($SEM); rtn<-\"\"; for(k in 1 : length(vc)) {  "
	    + "rtn<-paste(rtn, vc[k], sep=\"\");  rtn<-paste(rtn, as.character(k), sep=\"=\"); rtn<-paste(rtn,\"\");  if( k==length(vc)){"
	    + " print (rtn); } }  } \n  varInd()  \n ";

    // public String getRCommand(RFlowProperties properties) {
    // properties.generateSemModelVar();
    // String tmp = PRE_MODEL_SPEC.replace("$SEM", properties
    // .getDataParaName());
    // return tmp;
    // }

    public String getRCommand(RFlowProperties properties) {

	return null;
    }

    public String getModelSpecRCommand(RFlowProperties properties, Map varMap) {
	properties.generateSemModelVar();
	StringBuffer sb = new StringBuffer();
	sb.append("load('sem') \n");
	sb.append(properties.getSemModelVar()).append("<-").append("specify.model()  \n");
	List<String> endoVars = properties.getEndoVars();
	List<String> exoVars = properties.getExoVars();
	List<String> semEqs = properties.getSemEquations();
	Map<String, String> corrMap = new HashMap<String, String>();

	for (String eq : semEqs) {
	    String y = eq.substring(0, eq.indexOf("="));
	    String X = eq.substring(eq.indexOf("=") + 1);
	    System.out.println("y  " + y + " X " + X);

	    if (X.contains("+")) {
		String[] xs = X.split("\\+");
		for (String x : xs) {
		    if (endoVars.contains(x)) {
			sb.append(x).append("->").append(y).append(", beta").append(varMap.get(x))
				.append(varMap.get(y)).append(", NA \n ");
		    } else if (exoVars.contains(x)) {
			sb.append(x).append("->").append(y).append(", gamma").append(varMap.get(x)).append(
				varMap.get(y)).append(", NA \n ");
		    }
		}
	    } else {
		if (endoVars.contains(X)) {
		    sb.append(X).append("->").append(y).append(", beta").append(varMap.get(X)).append(varMap.get(y))
			    .append(", NA \n ");
		    corrMap.put(X, y);
		} else if (exoVars.contains(X)) {
		    sb.append(X).append("->").append(y).append(", gamma").append(varMap.get(X)).append(varMap.get(y))
			    .append(", NA \n ");
		}
	    }
	    // sb.append()
	}

	Iterator<String> keyItr = corrMap.keySet().iterator();
	Map tmpMap = new HashMap<String, String>();
	while (keyItr.hasNext()) {
	    String x = keyItr.next();

	    if (corrMap.get(x) != null) {

		if (null != tmpMap.get(corrMap.get(x)) && x.equals(tmpMap.get(corrMap.get(x))))
		    continue;

		String y = corrMap.get(corrMap.get(x));
		if (null != y && y.trim().equals(x.trim())) {
		    sb.append(x).append("<->").append(corrMap.get(x)).append(", sigma").append(varMap.get(x)).append(
			    varMap.get(corrMap.get(x))).append(", NA \n ");
		    tmpMap.put(x, corrMap.get(x));
		}
	    }
	}

	for (String endo : endoVars) {
	    sb.append(endo).append("<->").append(endo).append(", sigma").append(varMap.get(endo)).append(
		    varMap.get(endo)).append(", NA \n ");
	}

	sb.append("\n");

	return sb.toString();
    }

    public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
	String cmd = getRCommand(properties);
	Map<String, String> varMap = new HashMap<String, String>();
	List<String> vars = properties.getSemInitialVarList();
	int i = 1;
	for (String var : vars) {
	    varMap.put(var, Integer.toString(i++));
	}
	if (vars.size() > 0) {
	    cmd = getModelSpecRCommand(properties, varMap);
	    String str = sb.toString();
	    str = RMEngine.executeAndWaitReturn(cmd, 200, false);
	    if (str.indexOf("Error") > -1) {
		RMLogger.error("Error found. Please check your flow and data.");
		RMLogger.info(str);
		return RComponent.ERROR;
	    }
	} else {
	    return ERROR;
	}

	return SUCEESS;
    }

    public String getKey() {
	return key;
    }

    public long holdedMS() {
	return 500;
    }

    public boolean validation() {
	return true;
    }

}
