package com.rm.app.r.component.explanation;

import com.rm.app.log.RMLogger;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.component.explanation.statement.RExCoefTableStatement;
import com.rm.app.r.component.explanation.statement.RExCoefficientStatement;
import com.rm.app.r.component.explanation.statement.RExFullPlotStatement;
import com.rm.app.r.component.explanation.statement.RExPowerStateStatement;
import com.rm.app.r.component.explanation.statement.RExResVsFitPlotStatement;
import com.rm.app.r.component.explanation.statement.RExSEMDiagramStatement;
import com.rm.app.r.component.explanation.statement.RExSEMFittedEqStatement;
import com.rm.app.r.component.explanation.statement.RExSEMFullSummaryStatement;
import com.rm.app.r.component.explanation.statement.RExSEMNFIStatement;
import com.rm.app.r.component.explanation.statement.RExSEMRmseaStatement;
import com.rm.app.r.component.explanation.statement.RExSigStateStatement;
import com.rm.app.r.component.explanation.statement.RExStatement;
import com.rm.app.r.component.explanation.statement.RExSumSetterStatement;
import com.rm.app.r.flow.RFlowProperties;

public class RDefaultEXComponent implements REXComponent {

	private String key;

	public RDefaultEXComponent(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getRCommand(RFlowProperties properties) {
		return null;
	}

	/**
	 * 
	 * @param cmdKey
	 * @return
	 */
	public RExStatement getRExStatement(int cmdKey) {
		RExStatement statement = null;
		switch (cmdKey) {
		case RExStatement.RCMD_SUM_SETTER:
			statement = new RExSumSetterStatement();
			break;
		case RExStatement.RCMD_SIG_STATE:
			statement = new RExSigStateStatement();
			break;
		case RExStatement.RCMD_POWER_STATE:
			statement = new RExPowerStateStatement();
			break;
		case RExStatement.RCMD_COEF_STATE:
			statement = new RExCoefficientStatement();
			break;
		case RExStatement.RCMD_COEF_TABLE:
			statement = new RExCoefTableStatement();
			break;
		case RExStatement.RCMD_PLOT_RES_VS_FIT:
			statement = new RExResVsFitPlotStatement();
			break;
		case RExStatement.RCMD_FULL_PLOT:
			statement = new RExFullPlotStatement();
			break;
		case RExStatement.RCMD_SEM_FIT_EQN:
			statement = new RExSEMFittedEqStatement();
			break;
		case RExStatement.RCMD_SEM_FIT_NFI:
			statement = new RExSEMNFIStatement();
			break;
		case RExStatement.RCMD_SEM_FIT_RMSEA:
			statement = new RExSEMRmseaStatement();
			break;
		case RExStatement.RCMD_SEM_DIAGRAM:
			statement = new RExSEMDiagramStatement();
			break;
		case RExStatement.RCMD_SEM_FULL_SUM:
			statement = new RExSEMFullSummaryStatement();
			break;
		}
		return statement;
	}

	public long holdedMS() {
		return 1000;
	}

	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		String cmd = getRCommand(properties);
		RMLogger.info(cmd);
		return RComponent.SUCEESS;
	}

	public boolean validation() throws Exception {
		return true;
	}
}
