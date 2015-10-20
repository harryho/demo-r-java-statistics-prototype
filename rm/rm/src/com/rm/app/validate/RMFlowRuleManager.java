package com.rm.app.validate;

public class RMFlowRuleManager implements IRMFlowValidRuleSet {
    
    public static IRMFlowRuleService createRMFlowValidateService( int serviceId  ){
        	    
	switch (serviceId){
	    case RM_BASIC_RULE_SERVICE:
		return new RMFlowBaseRuleService();
	    case RM_GLS_RULE_SERVICE:
		return new RMFlowGLSRuleService();
	    default :
		return new RMFlowBaseRuleService();
	}
	
    }

}
