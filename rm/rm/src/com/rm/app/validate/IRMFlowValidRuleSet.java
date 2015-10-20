package com.rm.app.validate;

import com.rm.app.r.component.data.RDataComponent;
import com.rm.app.r.component.equation.REQComponent;
import com.rm.app.r.component.estimation.RESComponent;
import com.rm.app.r.component.evaluation.REVComponent;
import com.rm.app.r.component.explanation.REXComponent;

public interface IRMFlowValidRuleSet {

    String[] RMFLOW_STRUCT = { RDataComponent.TYPE_DATA_MODEL, REQComponent.TYPE_EQUATION,
	    RESComponent.TYPE_ESTIMATION, REVComponent.TYPE_EVALUATION, REXComponent.TYPE_EXPLANATION };

    int RM_BASIC_RULE_SERVICE = 1;

    int RM_GLS_RULE_SERVICE = 2;

    String[] RMFLOW_UNIQUE_RCOMPT_TYPE = { RDataComponent.TYPE_DATA_MODEL, REQComponent.TYPE_EQUATION,
	    RESComponent.TYPE_ESTIMATION, REXComponent.TYPE_EXPLANATION };

    Class[] RM_BASIC_RULES = { RMFlowBaseRuleService.class };

    String[] UNIQUE_PORT_RCMPT = { RDataComponent.TYPE_DATA_MODEL, REXComponent.TYPE_EXPLANATION };

    String[] DOUBLE_PORT_RCMPT = { REQComponent.TYPE_EQUATION, RESComponent.TYPE_ESTIMATION,
	    REVComponent.TYPE_EVALUATION };

    String[][] RMFLOW_CORR_RELATIONSHIP = {
	    { RDataComponent.TYPE_DATA_MODEL, REQComponent.TYPE_EQUATION },
	    { REQComponent.TYPE_EQUATION, RESComponent.TYPE_ESTIMATION },
	    { RESComponent.TYPE_ESTIMATION, REVComponent.TYPE_EVALUATION },
	    { REVComponent.TYPE_EVALUATION, REVComponent.TYPE_EVALUATION },
	    { REVComponent.TYPE_EVALUATION, REXComponent.TYPE_EXPLANATION }, };

    String[][] RMFLOW_INCORR_RELATIONSHIP = {
	    { REQComponent.TYPE_EQUATION, REQComponent.TYPE_EQUATION },
	    { RESComponent.TYPE_ESTIMATION, RESComponent.TYPE_ESTIMATION },
	    { RESComponent.TYPE_ESTIMATION, REVComponent.TYPE_EVALUATION },
	    { REVComponent.TYPE_EVALUATION, REQComponent.TYPE_EQUATION },
	    { REXComponent.TYPE_EXPLANATION, REQComponent.TYPE_EQUATION },
	    { REXComponent.TYPE_EXPLANATION, RESComponent.TYPE_ESTIMATION },
	    { REQComponent.TYPE_EQUATION, REQComponent.TYPE_EQUATION },
	    { REQComponent.TYPE_EQUATION, REQComponent.TYPE_EQUATION },
	    { REQComponent.TYPE_EQUATION, REQComponent.TYPE_EQUATION }

    };

    String SPECIAL_CMPT = RESComponent.TYPE_ESTIMATION_GLS;

    String[][] RMFLOW_GLS_CORR_RELATIONSHIP = {
	    { RDataComponent.TYPE_DATA_MODEL, REQComponent.TYPE_EQUATION },
	    { REQComponent.TYPE_EQUATION, RESComponent.TYPE_ESTIMATION },
	    { RESComponent.TYPE_ESTIMATION, REVComponent.TYPE_EVALUATION },
	    { REVComponent.TYPE_EVALUATION, REVComponent.TYPE_EVALUATION },
	    { REVComponent.TYPE_EVALUATION, REXComponent.TYPE_EXPLANATION },
	    { RESComponent.TYPE_ESTIMATION, REXComponent.TYPE_EXPLANATION } };

}
