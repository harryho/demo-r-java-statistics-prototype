package com.rm.app.r.component.explanation.statement;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.rm.app.r.flow.RFlowProperties;

public class RExSEMRmseaStatement implements RExStatement {

	public String formatDisplay(String callBack, RFlowProperties properties) {
		// input:[1] 0.01973168 0.01207378 0.02850818 0.90000000
		// output: Fits the data well with a 0.02 RMSEA
		callBack = callBack.replaceAll("\\[1\\]", "").trim();
		String ret = "";
		try {
			String[] tmp = callBack.split(" ");
			BigDecimal dec = new BigDecimal(callBack);
			String parten = "#.##";
			DecimalFormat format = new DecimalFormat(parten);
			for (int i = 0; i < tmp.length; i++) {
				if ("".equals(tmp[i].trim())) {
					continue;
				}
				String value = format.format(dec);
				ret = "Fits the data well with a " + value + " RMSEA.";
				break;
			}
		} catch (Exception e) {

		}
		return ret;
	}

	public String getRCommand(RFlowProperties properties) {
		return properties.getSummaryResultVar()+ "$RMSEA";
	}

}
