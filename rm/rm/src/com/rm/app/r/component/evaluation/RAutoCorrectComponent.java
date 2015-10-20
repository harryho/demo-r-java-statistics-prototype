package com.rm.app.r.component.evaluation;

import javax.swing.JOptionPane;

import org.wltea.expression.ExpressionEvaluator;

import com.rm.app.RMAppContext;
import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.util.LogicUtil;

public class RAutoCorrectComponent implements REVComponent {

	private String key;

	private String logic;

	public RAutoCorrectComponent(String key, String logic) {
		this.key = key;
		this.logic = logic;
	}

	public boolean validation() throws RMException {
		if (logic == null || "".equals(logic)
				|| !LogicUtil.validExpression(logic)) {
			throw new RMException(
					"Autocorrelation's P-value setting is incorrect!");
		}
		return true;
	}

	public String getKey() {
		return key;
	}

	public String getRCommand(RFlowProperties properties) {
		String cmd = "load('car')\n";
		cmd += "durbin.watson(" + properties.getSummaryParam() + ")$p";
		RMLogger.info(cmd);
		RMLogger.debug(cmd);
		return cmd;
	}

	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		String infor = sb.toString();
		return checkReturn(infor);
	}

	private int checkReturn(String infor) {
		boolean ret = false;
		// [1] 0.5933501
		int index = infor.indexOf("]");
		String expression = "";
		String value = "";
		if (index != -1) {
			value = infor.substring(index + 1).trim();
			expression = value + logic;
			System.out.println(expression);
			try {
				ret = (Boolean) ExpressionEvaluator.evaluate(expression);
			} catch (Exception e) {
			}
		}
		if (ret) {
			RMLogger.warn("Autocollectation P-value " + value);
			Object[] options = { "Continue", "Cancel" };
			int selectedValue = JOptionPane.showOptionDialog(RMAppContext
					.getRMApp(), "Your setting: " + logic
					+ ", Autocollectation is " + value + ". Continue?\n",
					"Autocollectation Suggetion", JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			if (selectedValue == 0) {
				return RComponent.SUCEESS;
			} else {
				return RComponent.ERROR;
			}
		} else {
			RMLogger.info("Autocollectation P-value:"+value);
		}
		return RComponent.SUCEESS;
	}

	public long holdedMS() {
		return 200;
	}

}
