package com.rm.app.r.component.evaluation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.rm.app.RMAppContext;
import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;

public class RNormalityComponent implements REVComponent {

	private String key;

	private String rsExpress;

	private String pvalue = "";

	private String wvalue = "";

	private static String NORMALITY_TEMPL = "normfunc<-function(lm){ nm<-shapiro.test(residuals(lm)); pv<-nm$p; wv<-nm$stat;  "
			+ "print('NORMALITY_BEGIN'); print(paste('W=', as.numeric(wv), sep=''));         print(paste('P=', as.numeric(pv), sep='')); 		"
			+ "print(pv$RSEXP); print('NORMALITY_END'); }  \n"
			+ " normfunc($ES) \n";

	public RNormalityComponent(String key, String rsExpress) {

		this.key = key;
		this.rsExpress = rsExpress;

	}

	public String getKey() {
		return key;
	}

	public boolean validation() throws Exception {
		Matcher mm = Pattern.compile("\\s+").matcher(rsExpress);

		rsExpress = mm.replaceAll("");
		Matcher m1 = Pattern.compile("^[ [[<][>]][=]?]] [-\\+]?\\d+.*\\d*$")
				.matcher(rsExpress.trim());
		Matcher m2 = Pattern.compile("^[<>=][-\\+]?\\d+.*\\d*$").matcher(
				rsExpress.trim());
		boolean rst = m1.matches() || m2.matches();

		if (!rst)
			throw new RMException(
					" Please check Normality setting again. e.g. <0.01)");

		return rst;
	}

	public String getRCommand(RFlowProperties properties) {
		String rcmd = "";
		rcmd = rcmd
				+ NORMALITY_TEMPL.replace("$ES", properties.getSummaryParam());
		rcmd = rcmd.replace("$RSEXP", rsExpress);
		return rcmd;
	}

	public long holdedMS() {
		return 1500;
	}

	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		String cmd = getRCommand(properties);
		RMLogger.info("Normality ");

		boolean ret = false;
		String str = sb.toString().trim();
		Pattern pp = Pattern.compile("\\[\\d+\\]");
		Matcher mm = pp.matcher(str);
		String cont = mm.replaceAll(" ");
		mm = Pattern.compile("\\\"").matcher(cont);
		cont = mm.replaceAll(" ");
		mm = Pattern.compile("\\s+").matcher(cont);
		cont = mm.replaceAll(" ");
		System.out.println(cont);

		String[] outs = cont.trim().split(" ");

		wvalue = outs[1].split("=")[1];
		pvalue = outs[2].split("=")[1];

		String express = "Your setting : P-value "+ rsExpress +". Now p-value of Normality is"+ pvalue;
                String express4Log=" P-value : "+pvalue;
		if (str.contains("FALSE")) {
			RMLogger.info(express4Log + " ");
			ret = true;
		} else if (str.contains("TRUE")) {
			RMLogger.info(express4Log + " ");
			Object[] options = { "Continue", "Cancel" };
			int selectedValue = JOptionPane.showOptionDialog(RMAppContext
					.getRMApp(), express + "! Continue? \n",
					"Normality Suggetion", JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE, null, options, options[0]);

			if (selectedValue == 0) {
				return RComponent.SUCEESS;
			} else {
				return RComponent.ERROR;
			}
		}

		return RComponent.SUCEESS;
	}

}
