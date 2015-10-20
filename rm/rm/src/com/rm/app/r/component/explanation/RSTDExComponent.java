package com.rm.app.r.component.explanation;

import com.rm.app.log.RMLogger;
import com.rm.app.r.RMEngine;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.component.equation.REQComponent;
import com.rm.app.r.component.explanation.statement.RExStatement;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.r.flow.output.TextWindow;

public class RSTDExComponent extends RDefaultEXComponent {

	private String key;

	protected static int[] cmds4mr = { RExStatement.RCMD_SUM_SETTER,
			RExStatement.RCMD_SIG_STATE, RExStatement.RCMD_POWER_STATE,
			RExStatement.RCMD_COEF_TABLE, RExStatement.RCMD_COEF_STATE,
			RExStatement.RCMD_PLOT_RES_VS_FIT };

	protected static int[] cmds4sem = { RExStatement.RCMD_SUM_SETTER,
			RExStatement.RCMD_SEM_FIT_EQN, RExStatement.RCMD_SEM_FIT_NFI,
			RExStatement.RCMD_SEM_FIT_RMSEA, RExStatement.RCMD_SEM_DIAGRAM,
			RExStatement.RCMD_SEM_FULL_SUM, };

	public static int[] getCmds(String eqType) {
		int[] rcmds = null;
		RMLogger.debug("Eq Type:" + eqType);
		if (REQComponent.TYPE_EQUATION_MR.equals(eqType)
				|| REQComponent.TYPE_EQUATION_SR.equals(eqType)) {
			rcmds = cmds4mr;
		} else if (REQComponent.TYPE_EQUATION_SEM.equals(eqType)) {
			rcmds = cmds4sem;
		}
		return rcmds;
	}

	public RSTDExComponent(String key) {
		super(key);
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public String getRCommand(RFlowProperties properties) {
		return super.getRCommand(properties);
	}

	public long holdedMS() {
		return 1500;
	}

	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		StringBuffer output = new StringBuffer();
		int[] cmds = getCmds(properties.getEqType());
		RMLogger.debug("CMDS:" + cmds);
		for (int rcmd : cmds) {
			RExStatement statement = getRExStatement(rcmd);
			String strCmd = statement.getRCommand(properties);
			String str = "";
			if (strCmd != null) {
				str = RMEngine.executeAndWaitReturn(strCmd, 200, false);
			}
			System.out.println(str);
			str = statement.formatDisplay(str, properties);
			output.append(str);
			output.append("\n");
			output.append("\n");
		}
		TextWindow.info.add(output.toString());

		return RComponent.SUCEESS;
	}

	public boolean validation() throws Exception {
		return true;
	}

}
