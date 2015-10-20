package com.rm.app.r.component.explanation.statement;

import java.util.List;

import com.rm.app.r.flow.RFlowProperties;

public class RExSEMFittedEqStatement implements RExStatement {

    public String formatDisplay(String callBack, RFlowProperties properties) {
	StringBuffer sb = new StringBuffer();
	if (callBack.contains("FITEQ")) {
	    callBack = callBack.substring(callBack.indexOf("FITEQ##") + 7, callBack.lastIndexOf("FITEQ")).trim();
	    String[] paths = callBack.split("##");

	    List<String> eqList = properties.getSemEquations();
	    List<String> endoList = properties.getEndoVars();
	    List<String> exoList = properties.getExoVars();

	    for (String eq : eqList) {
		String left = eq.substring(0, eq.indexOf("="));

		sb.append(left).append("=");
		String right = eq.substring(eq.indexOf("=") + 1, eq.length());

		if (right.contains("+")) {
		    String[] X = right.split("\\+");
		    int i = 1;
		    for (String x : X) {
			String coef = getCoeff(left, x, paths, endoList.contains(right));
			if (i++ == X.length)
			    sb.append(coef).append(x);
			else
			    sb.append(coef).append(x).append("+");
		    }

		} else {
		    String coef = getCoeff(left, right.trim(), paths, endoList.contains(right));
		    sb.append(coef).append(right);
		}
		sb.append("\n");
	    }

	}

	return sb.toString();

    }

    private String getCoeff(String left, String right, String[] paths, boolean rightIsEndo) {
	String coef = "";
	for (String path : paths) {

	    if (!rightIsEndo) {
		if (path.contains(left) && path.contains(right) && path.contains("gamma"))
		    coef = path.substring(path.lastIndexOf("#") + 1);
	    } else {
		if (path.contains(left) && path.contains(right) && path.contains("beta"))
		    if (path.indexOf(right) > path.indexOf(left))
			coef = path.substring(path.lastIndexOf("#") + 1);
	    }
	}
	return coef;
    }

    public String getRCommand(RFlowProperties properties) {
	StringBuffer cmd = new StringBuffer("fitEq<-function(){ vc<-round(as.numeric(")
		.append(properties.getSummaryResultVar())
		.append("$coeff[,\"Estimate\"] ),digits=2); md<-")
		.append(properties.getSemModelVar())
		.append("; vp<-c(md[,1]);vv<-c(md[,2]); mod<-cbind(vp[1:8],vv[1:8],vc[1:8]); rtn<-\"FITEQ\";")
		.append(
			" for ( k in 1: length(mod[,1] )){ rtn<-paste(rtn, mod[k,1], sep=\"##\"); rtn<-paste(rtn, mod[k,2], sep=\"#\"); ")
		.append(
			" rtn<-paste(rtn, as.character(mod[k,3]), sep=\"#\");if( k ==length(mod[,1])){ rtn<-paste(rtn,\"FITEQ\" , sep=\"\"); print(rtn); }}  }")
		.append(" \n  fitEq()  \n");

	return cmd.toString();
    }

}
