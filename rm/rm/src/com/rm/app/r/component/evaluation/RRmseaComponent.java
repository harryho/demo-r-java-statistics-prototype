package com.rm.app.r.component.evaluation;

import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.util.LogicUtil;

public class RRmseaComponent implements REVComponent {
	private String key;

	private String logic;

	public RRmseaComponent(String key, String logic) {
		this.key = key;
		this.logic = logic;
	}

	public String getKey() {
		return key;
	}

	public String getRCommand(RFlowProperties properties) {
		return "summary(" + properties.getSummaryParam() + ")$RMSEA";
	}

	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		// [1] 0.01973168 0.01207378 0.02850818 0.90000000
		String info = sb.toString().trim();
		RMLogger.debug("-----------> " + info);
		info = info.replaceAll("\\[1\\]", "");
		String[] tmp = info.split(" ");
		if (tmp != null && tmp.length > 0) {
			for (int i = 0; i < tmp.length; i++) {
				String value = tmp[i];
				if ("".equals(value.trim())) {
					continue;
				}
				if (!LogicUtil.checkLogicByExpression(value, logic)) {
					RMLogger.warn("RMSEA: " + value + " " + logic
							+ " is false!");
					return RComponent.ERROR;
				}
				RMLogger.info("RMSEA: " + value + " " + logic + " is true!");
				break;//only check the first one.
			}

		}
		return RComponent.SUCEESS;
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
