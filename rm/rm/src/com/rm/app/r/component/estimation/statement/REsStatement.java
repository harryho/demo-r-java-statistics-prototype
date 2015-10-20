package com.rm.app.r.component.estimation.statement;

import com.rm.app.r.flow.RFlowProperties;

public interface REsStatement {

	public static final int RCMD_SEM_MLE = 0;
	public static final int RCMD_GLM_MLE = 1;

	public String getRCommand(RFlowProperties properties);

	public String callBackHandler(String callBack,
			RFlowProperties properties);

}
