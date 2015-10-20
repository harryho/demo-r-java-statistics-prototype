package com.rm.app.r.component.explanation.statement;

import java.util.ArrayList;
import java.util.List;

import com.rm.app.log.RMLogger;
import com.rm.app.r.flow.RFlowProperties;

public class RExCoefficientStatement implements RExStatement {

	public String getRCommand(RFlowProperties properties) {
		//String cmd = "names(" + properties.getSummaryParam() + "$coef) \n";
		String cmd = "";
		cmd = cmd + "vr <- round(as.numeric(" + properties.getSummaryParam()
				+ "$coeff), digits =2) \n";
		cmd = cmd + "vr[2:length(vr)]";
		return cmd;
	}

	public String formatDisplay(String callBack, RFlowProperties properties) {
		/*
		 * input: 
		 * [1] "(Intercept)" "WaterTemp" "AirFlow" "AcidConc" 
		 * [1] -39.92  * 1.30 0.72 -0.15 output: The dependent variable will be increased by
		 * 0.67 as AirFlow increases by 1 unit, be increased by 1.30 as
		 * WaterTemp increases by 1 unit.
		 */
		
		RMLogger.debug(callBack);
		List<String> list = getInforByRows(callBack);
		List<String> ind_list = properties.getIndepend_var();
		if(list.size()<ind_list.size()){
			return "";
		}
		StringBuffer sb = new StringBuffer("The dependent variable will be increased by ");
		for(int i=0,k=ind_list.size();i<k;i++){
			String var = ind_list.get(i);
			String value = list.get(i);
			String inf = value+" as "+var+" increases by 1 unit";
			if(i!=k-1){
				inf += ", ";
			}else{
				inf += ".";
			}
			sb.append(inf);
		}
		return sb.toString();
	}

	private List<String> getInforByRows(String infor) {
		List<String> list = new ArrayList<String>();
		String[] rows = infor.split("\n");
		if (rows != null && rows.length > 0) {
			for (int i = 0; i < rows.length; i++) {
				String row = rows[i];
				list.addAll(getRow(row," "));
			}
		}
		return list;
	}

	private List<String> getRow(String row, String seperation) {
		List<String> list = new ArrayList<String>();
		row = row.replaceAll("\\[1\\]", "").trim();
		String[] tmp = row.split(seperation);
		for (int i = 0; i < tmp.length; i++) {
			String a = tmp[i].trim();
			if(!"".equals(a)){
				list.add(tmp[i].trim());
			}
		}
		return list;
	}
	
}
