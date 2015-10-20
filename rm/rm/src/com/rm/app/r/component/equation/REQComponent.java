package com.rm.app.r.component.equation;

import com.rm.app.r.component.RComponent;

public interface REQComponent extends RComponent{
	
	public static final String TYPE_EQUATION = "equation";
	public static final String TYPE_EQUATION_SR = "equation.sr";
	public static final String TYPE_EQUATION_MR= "equation.mr";
	public static final String TYPE_EQUATION_SEM = "equation.sem";
	public static final String TYPE_EQUATION_GLM = "equation.glm";
	

	public static final String ATTR_EQUATION_DEP_VAR = "ATTR_EQUATION_DEP_VAR";

	public static final String ATTR_EQUATION_INDEP_VAR = "ATTR_EQUATION_INDEP_VAR";
	
	public static final String ATTR_EQUATION_EQUATION = "ATTR_EQUATION_EQUATION";
	
	public static final String ATTR_EQUATION_FUNC_FAMILY = "ATTR_EQUATION_FUNC_FAMILY";
}

