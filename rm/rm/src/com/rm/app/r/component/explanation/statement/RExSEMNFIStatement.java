package com.rm.app.r.component.explanation.statement;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.rm.app.r.flow.RFlowProperties;

public class RExSEMNFIStatement implements RExStatement{

	public String formatDisplay(String callBack, RFlowProperties properties) {
		// input: [1] 0.9994473
		// output: Fits the data well with a 0.999 NFI
		callBack = callBack.replaceAll("\\[1\\]", "").trim();
		String ret = "";
		try {
			BigDecimal dec = new BigDecimal(callBack);
			String parten = "#.###";
			DecimalFormat format = new DecimalFormat(parten);
			String value = format.format(dec);
			ret = "Fits the data well with a " + value + " NFI.";
		} catch (Exception e) {

		}
		return ret;
	}

	public String getRCommand(RFlowProperties properties) {
		return properties.getSummaryResultVar() + "$NFI";
	}

}
