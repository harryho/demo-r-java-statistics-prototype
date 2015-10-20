package com.rm.app.r.component.evaluation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.wltea.expression.ExpressionEvaluator;

import com.rm.app.RMAppContext;
import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.RMEngine;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.util.LogicUtil;

public class RVIFComponent implements REVComponent {

	private String key;

	private String logic;

	private String vifName = "vif_" + System.currentTimeMillis() % 1000000;

	public RVIFComponent(String key, String logic) {
		this.key = key;
		this.logic = logic;
	}

	public boolean validation() throws RMException {
		if (logic == null || "".equals(logic)|| !LogicUtil.validExpression(logic)) {
			throw new RMException(
					"VIF's factor setting is incorrect!");
		}
		return true;
	}

	public String getKey() {
		return key;
	}

	public String getRCommand(RFlowProperties properties) {
		String cmd = "load('car')\n";
		cmd += vifName + "<- vif(" + properties.getSummaryParam() + ")";
		RMLogger.info(cmd);
		return cmd;
	}

	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		String infor = sb.toString();
		RMLogger.debug(infor);
		if (infor != null && infor.indexOf("Warning message") >= 0) {
			RMLogger.error("Warning found.");
			RMLogger.info(infor);
			return RComponent.ERROR;
		}

		String checkingCmd = "for(k in 1 :length(" + vifName + ") ) {print("
				+ vifName + "[k]);}";
		RMLogger.debug(checkingCmd);
		infor = RMEngine.executeAndWaitReturn(checkingCmd, 200, false);
		RMLogger.debug("info=" + infor);
		return checkReturn(infor, properties);
	}

	private int checkReturn(String infor, RFlowProperties properties) {
		if (infor != null && infor.indexOf("Warning message") >= 0) {
			RMLogger.warn(infor);
			return RComponent.ERROR;
		}
		String[] rows = infor.split("\n");
		if (rows == null || rows.length < 2) {
			return RComponent.ERROR;
		}
		Map<String, String> map = new HashMap<String, String>();
		String var = null;
		String value = null;
		for (int i = 0, k = rows.length; i < k; i++) {
			if (i % 2 == 0) {
				var = rows[i].trim();
				continue;
			} else {
				value = rows[i].trim();
			}
			if (var != null && value != null) {
				map.put(var, value);
				var = value = null;
			}
		}
		RMLogger.debug(map);
		if (map.isEmpty()) {
			return RComponent.ERROR;
		}
		Set<String> varSet = map.keySet();
		Iterator<String> it = varSet.iterator();

		if (logic.toLowerCase().indexOf("k") != -1) {
			int size = properties.getIndepend_var().size();
			logic = logic.replace("k", String.valueOf(size));
		}

		boolean ret = true;
		while (it.hasNext()) {
			String k = it.next();
			String v = map.get(k);
			String expression = v + logic;
			Boolean ok = false;
			try {
				ok = (Boolean) ExpressionEvaluator.evaluate(expression);
				ret &= ok;
			} catch (Exception e) {
				ret &= false;
				JOptionPane
						.showMessageDialog(
								RMAppContext.getRMApp(),
								"VIF: "
										+ logic
										+ " format is not correct!\nPlease input like >2*(K+1)",
								"Warning", JOptionPane.WARNING_MESSAGE);
				return RComponent.ERROR;
			}
			if (ok) {
				RMLogger.warn("VIF " +k + ": " + v );
			} else {
				RMLogger.info("VIF " + k + ": " + v);
			}
		}
		if (ret) {

			Object[] options = { "CONTINUE", "CANCEL" };
			// Z<-c("AirFlow","WaterTemp","AcidConc")
			// cor(data[,Z], use="everything", method = c("pearson", "kendall",
			// "spearman"))
			String allIndvars = "c(";
			List<String> list = properties.getIndepend_var();
			for (int i = 0, k = list.size(); i < k; i++) {
				allIndvars += "'" + list.get(i) + "'";
				if (i != k - 1) {
					allIndvars += ",";
				}
			}
			allIndvars += ")";
			String corcmd = "cor("
					+ properties.getDataParaName()
					+ "[,"
					+ allIndvars
					+ "], use='everything', method = c('pearson', 'kendall', 'spearman'))";
			RMLogger.info(corcmd);
			String corInfor = RMEngine.executeAndWaitReturn(corcmd, 100, false);
			RMLogger.debug(corInfor);

			JLabel jl1 = new JLabel("Your setting: "+logic+", VIF is "+value);
			JLabel jl2 = new JLabel("The Correlation Matrix is:");
			JLabel jl3 = new JLabel(
					"Would you go back to modify EQ or continue?");
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(jl1);
			panel.add(jl2);
			panel.add( new JLabel(" "));
			String[] hangs = corInfor.split("\n");
			if(hangs!=null && hangs.length>0){
				for(int i=0;i<hangs.length;i++){
					JLabel j = new JLabel(hangs[i]);
					panel.add(j);
				}
			}
			panel.add( new JLabel(" "));
			panel.add(jl3);
			panel.add( new JLabel(" "));
			int retCode = JOptionPane.showOptionDialog(RMAppContext.getRMApp(),
					panel, "VIF Suggetion", JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			if (retCode == 0) {
				return RComponent.SUCEESS;
			} else {
				return RComponent.ERROR;
			}

		}
		return RComponent.SUCEESS;
	}

	public long holdedMS() {
		return 800;
	}


}
