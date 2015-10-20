package com.rm.app.r.component.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.util.LogicUtil;

public class RPValueComponent implements REVComponent {

	// record failed var when linking to R
	List<String> failed_var = new ArrayList<String>();

	private String key;

	// the expression of PValue component
	private String rsExpress;

	public RPValueComponent(String key, String rsExpress) {
		this.key = key;
		this.rsExpress = rsExpress;
	}

	public String getKey() {
		return key;
	}

	//
	public String getRCommand(RFlowProperties properties) {
		return "summary(" + properties.getSummaryParam() + ")$coefficients";
	}

	public long holdedMS() {
		return 1000;
	}

	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		String str = sb.toString();
		Map<String, String> info = getIndependenInfo(str);
		RMLogger.debug(info);
		boolean ret = true;
		if (properties.getIndepend_var() != null
				&& properties.getIndepend_var().size() > 0) {
			for (int i = 0, k = properties.getIndepend_var().size(); i < k; i++) {
				String para = properties.getIndepend_var().get(i);
				String value = info.get(para);
				boolean logicOK = LogicUtil.checkLogicByExpression(value,
						this.rsExpress);
				if (logicOK) {
					RMLogger.info("P-value Of " + para + ": " + value
							+ rsExpress + " is " + logicOK + "!");
				} else {
					RMLogger.info("P-value Of " + para + ": " + value
							+ rsExpress + " is " + logicOK + "!");
					failed_var.add(para);
				}
				ret &= logicOK;
			}
		}
		if (ret) {
			return RComponent.SUCEESS;
		} else {
			return RComponent.REPROCESS;
		}
	}

	public List<String> getFailedVar() {
		return failed_var;
	}

	private static Map<String, String> getIndependenInfo(String str) {
		Map<String, String> map = new HashMap<String, String>();

		String[] tmp = str.split("\n");
		if (tmp == null || tmp.length < 2) {
			return map;
		}
		for (int i = 1, k = tmp.length; i < k; i++) {
			try {
				String line = tmp[i];
				StringTokenizer st = new StringTokenizer(line);
				int index = 0;
				String para = null;
				String pvalue = null;
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if (index == 0) {
						para = token;
					} else if (index == 4) {
						pvalue = token;
					}
					index++;
				}
				if (para != null)
					map.put(para, pvalue);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return map;
	}

	public boolean validation() throws Exception {
		if (rsExpress == null || "".equals(rsExpress = rsExpress.trim())
				|| !LogicUtil.validExpression(rsExpress)) {
			throw new RMException("Logic Function of " + key
					+ " is incorrect!");
		}
		return true;
	}

}
