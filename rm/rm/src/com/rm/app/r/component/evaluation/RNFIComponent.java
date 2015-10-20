package com.rm.app.r.component.evaluation;

import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.util.LogicUtil;

public class RNFIComponent implements REVComponent {
	private String key;

	private String logic;

	public RNFIComponent(String key, String logic) {
		this.key = key;
		this.logic = logic;
	}

	public String getKey() {
		return key;
	}

	public String getRCommand(RFlowProperties properties) {
		return "summary(" + properties.getSummaryParam() + ")$NFI";
	}

	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		// [1] 0.9994473
		String info = sb.toString().trim();
		info = info.replaceAll("\\[1\\]", "").trim();
		if (LogicUtil.checkLogicByExpression(info, logic)) {
			RMLogger.info("NFI: " + info + " " + logic + " is true!");
			return RComponent.SUCEESS;
		} else {
			RMLogger.warn("NFI: " + info + " " + logic + " is false!");
			return RComponent.ERROR;
		}
	}

	public long holdedMS() {
		return 200;
	}

	public boolean validation() throws Exception {
		if (logic == null || "".equals(logic = logic.trim())
				|| !LogicUtil.validExpression(logic)) {
			throw new RMException("Logic Function of " + key + " is incorrect!");
		}
		return true;
	}
}
