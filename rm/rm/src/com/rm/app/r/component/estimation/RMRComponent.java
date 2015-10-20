package com.rm.app.r.component.estimation;

import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.log.SystemLogger;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;

public class RMRComponent implements RESComponent {

	private String key;
	private RFlowProperties properties;

	public RMRComponent(String key,RFlowProperties properties) {
		this.key = key;
		this.properties = properties;
	}

	public String getRCommand(RFlowProperties properties) {
		StringBuffer tmp = new StringBuffer(properties.getSummaryParam()
				+ " <- lm").append("(");
		String depend = properties.getDepend_var().get(0);
		String independ = "";
		for (int l_i = 0, l_k = properties.getIndepend_var().size(); l_i < l_k; l_i++) {
			if (l_i == 0) {
				independ = properties.getIndepend_var().get(l_i);
			} else {
				independ = independ + " + "
						+ properties.getIndepend_var().get(l_i);
			}

		}
		tmp.append(depend).append(" ~ ").append(independ);
		tmp.append(",").append(properties.getDataParaName()).append(")");
		return tmp.toString();
	}

	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		String cmd = getRCommand(properties);
		RMLogger.info(cmd);

		RMLogger.debug(sb.toString());
		SystemLogger.fileLogger.error(sb.toString());
		String str = sb.toString();
		if (str != null && str.indexOf("Warning message") >= 0) {
			RMLogger.info(str);
			return RComponent.ERROR;
		}
		return RComponent.SUCEESS;
	}

	public String getKey() {
		return key;
	}

	public long holdedMS() {
		return 500;
	}

	public boolean validation() throws RMException {
		if (properties.getIndepend_var() == null
				|| properties.getIndepend_var().size() < 1) {
			throw new RMException(
					"Independent variable is not correct !");
		}
		if (properties.getDepend_var() == null
				|| properties.getDepend_var().size() < 1) {
			throw new RMException(
					"Dependent variable is not correct !");
		}
		return true;
	}

}
