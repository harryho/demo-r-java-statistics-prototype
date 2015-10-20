package com.rm.app.r.component.evaluation;

import javax.swing.JOptionPane;

import org.wltea.expression.ExpressionEvaluator;

import com.rm.app.RMAppContext;
import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.util.LogicUtil;

public class RNcvComponent implements REVComponent {

	private String key;

	private String logic;

	public RNcvComponent(String key, String logic) {
		this.key = key;
		this.logic = logic;
	}

	private String LOAD_LIB_TEMPL="options(show.error.messages=FALSE);  \n " +
	"load<-function(lib){ vc<-c(.libPaths());  for ( k in 1 : length(vc)) { t<-try(library( lib, pos=2, lib.loc=vc[k], character.only=TRUE)); } } \n " +
	"load('$LIB') \n " +
	" options(show.error.messages=TRUE);  \n ";
	
	public boolean validation() throws RMException {
		if (logic == null || "".equals(logic)
				|| !LogicUtil.validExpression(logic)) {
			throw new RMException(
					" Please check NCV's setting again. e.g. >2*(k+1)");
		}
		return true;
	}

	public String getKey() {
		return key;
	}

	public String getRCommand(RFlowProperties properties) {
//		String cmd = "library(car)\n";
	        String cmd=LOAD_LIB_TEMPL.replace("$LIB", "car");
		cmd += "ncv.test(" + properties.getSummaryParam() + ")$p";
		RMLogger.info(cmd);
		RMLogger.debug(cmd);
		return cmd;
	}

	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		String infor = sb.toString();
		if (infor != null && infor.indexOf("Warning message") >= 0) {
			RMLogger.error("Warning found.");
			RMLogger.info(infor);
			return RComponent.ERROR;
		}
		return checkReturn(infor);
	}

	private int checkReturn(String infor) {
		boolean ret = false;
		// [1] 0.5933501
		int index = infor.indexOf("]");
		String expression = "";
		String value = "";
		if (index != -1) {
			value =  infor.substring(index + 1).trim();
			expression = value + logic;
			System.out.println(expression);
			try {
				ret = (Boolean) ExpressionEvaluator.evaluate(expression);
			} catch (Exception e) {
			}
		}
		if (ret) {
			RMLogger.warn("Ncv P-value " + value);
			Object[] options = { "Continue", "Cancel" };
			int selectedValue = JOptionPane.showOptionDialog(RMAppContext
					.getRMApp(), "Your setting: "+logic+", NCV is "+value+". Continue?" , "Ncv Suggetion",
					JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);

			if (selectedValue == 0) {
				return RComponent.SUCEESS;
			} else {
				return RComponent.ERROR;
			}
		} else {
			RMLogger.info("Ncv P-value:"+value);
		}
		return RComponent.SUCEESS;
	}

	public long holdedMS() {
		return 200;
	}

}
