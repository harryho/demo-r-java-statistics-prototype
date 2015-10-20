package com.rm.app.r.component.evaluation;

import javax.swing.JOptionPane;

import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.util.LogicUtil;

public class RRSquareComponent implements REVComponent {

	// private final String ADJ_R_SQUARE = "adj.r.squared";

	private String key;

	// the expression of RSquare component
	private String rsExpress;

	public RRSquareComponent(String key, String rsExpress) {

		this.key = key;
		this.rsExpress = rsExpress;
	}

	public String getKey() {
		return key;
	}

	public boolean validation() throws Exception {
		if (rsExpress == null || "".equals(rsExpress = rsExpress.trim())
				|| !LogicUtil.validExpression(rsExpress)) {
			throw new RMException("Logic Function of " + key + " is incorrect!");
		}
		return true;
	}

	// summary(results2) [["adj.r.squared"]]
	public String getRCommand(RFlowProperties properties) {
		return "summary(" + properties.getSummaryParam()
				+ ")[[\"adj.r.squared\"]]";
	}

	public long holdedMS() {
		return 1500;
	}

	// [1] 0.6944137
	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		String cmd = getRCommand(properties);
		RMLogger.info(cmd);

		boolean ret = false;
		String str = sb.toString().trim();
		int index = str.indexOf("]");
		if (index != -1) {
			String sqstr = str.substring(index + 1).trim();
			ret = LogicUtil.checkLogicByExpression(sqstr, rsExpress);
			if (ret) {
				RMLogger.info("R-square: " + sqstr + rsExpress + "  is " + ret
						+ "!");
			} else {
				RMLogger.warn("R-square: " + sqstr + rsExpress + "  is " + ret
						+ "!");
				JOptionPane
						.showMessageDialog(null,
								"R-square does not pass, please modify your model specification for step Eq.");
			}
		} else {
			RMLogger.info(str);
		}
		return ret ? RComponent.SUCEESS : RComponent.ERROR;
	}

}
