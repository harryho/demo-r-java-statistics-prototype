package com.rm.app.r.component.explanation.statement;

import com.rm.app.r.flow.RFlowProperties;

public interface RExStatement {

	public static final int RCMD_SUM_SETTER = 0;
	public static final int RCMD_SIG_STATE = 1;
	public static final int RCMD_POWER_STATE = 2;
	public static final int RCMD_COEF_TABLE = 3;
	public static final int RCMD_COEF_STATE = 4;
	public static final int RCMD_PLOT_RES_VS_FIT = 5;
	public static final int RCMD_FULL_PLOT = 6;
	
	public static final int RCMD_SEM_FIT_EQN=7;
	public static final int RCMD_SEM_FIT_NFI=8;
	public static final int RCMD_SEM_FIT_RMSEA=9;
	public static final int RCMD_SEM_DIAGRAM=10;
	public static final int RCMD_SEM_FULL_SUM=11;
	
	

	public String getRCommand(RFlowProperties properties);

	public String formatDisplay(String callBack,
			RFlowProperties properties);

}
