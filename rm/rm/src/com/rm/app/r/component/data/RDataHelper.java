package com.rm.app.r.component.data;

import java.util.ArrayList;
import java.util.List;

import com.rm.app.exception.RMException;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.log.RMLogger;
import com.rm.app.r.RMEngine;
import com.rm.app.r.component.RComponent;

public class RDataHelper {

	/**
	 * 
	 * @param item
	 * @return
	 */
	public static boolean isDataComponent(RComponent item) {
		if (item instanceof RDataImportComponent
				|| item instanceof RDataSelectComponent) {
			return true;
		}
		return false;
	}

	/**
	 * get All numeric variable by R commands
	 * 
	 * @param dataParaName
	 * @param dataImportCommand
	 * @return
	 */
	public static List<String> getAllNumericVarsFromR(String dataParaName,
			String dataImportCommand) {

		List<String> list = new ArrayList<String>();
		long current = System.currentTimeMillis() % 100000;
		StringBuffer sb = new StringBuffer();
		sb.append(dataImportCommand).append("\n");
		sb.append("indices_" + current + "<-1:dim(" + dataParaName + ")[2]")
				.append("\n");
		sb.append(
				"indices_" + current + "<-na.omit(ifelse(indices_" + current
						+ "*sapply(" + dataParaName + ",is.numeric),indices_"
						+ current + ",NA))").append("\n");
		sb.append(
				"mean_" + current + "<-sapply(" + dataParaName + "[,indices_"
						+ current + "],mean)").append("\n");
		sb.append("mean_" + current);

		String info = RMEngine.executeAndWaitReturn(sb.toString(), 200, false);
		RMEngine.MAINRCONSOLE.cleanCallBackMessage();

		RMLogger.debug("getNumericVarsByRCommand :return infor = " + info);
		if (info.indexOf("Error") != -1 || info.indexOf(dataParaName) != -1) {// Error
			return list;
		}
		String[] tmp = info.split("\n");
		if (tmp != null && tmp.length > 1) {
			for (int i = 0; i < tmp.length; i = i + 2) {
				String[] vars = tmp[i].split(" ");
				if (vars != null && vars.length > 1) {
					for (int j = 0, k = vars.length; j < k; j++) {
						String t = vars[j].trim();
						if (!"".equals(t)) {
							list.add(t);
						}
					}
				}
			}

		}
		return list;
	}

	/**
	 * 
	 * @param dataCom
	 * @param def_independ_var
	 * @param def_depend_var
	 * @throws Exception
	 */
	public static RDataComponent getDefaultVariables(RDataComponent dataCom,
			List<String> def_independ_var, List<String> def_depend_var)
			throws Exception {
		List<String> var_list = null;
		RDataCache cache = RDataCache.getIntance();
		if (cache.isDataLoaded(dataCom)) {
			var_list = cache.getAllNumericVariables();
			//cover back the dataParaName
			dataCom = cache.getCurrentDataCom();
			RMLogger.debug("Numeric list getted from datacache.");
		} else {
			var_list = dataCom.getAllNumericVariables();
			cache.setAllNumericVariables(var_list);
		}
		RMLogger.debug("Numeric list ======>" + var_list);
		if (var_list == null || var_list.size() < 2) {
			throw new RMException(
					"Data Component loading failed! Please check the setting.");
		}
		List<String> clone = RDataHelper.clone(var_list);
		def_depend_var.add(clone.remove(0));
		def_independ_var.addAll(clone);
		return dataCom;
	}

	/**
	 * 
	 * @param dataCell
	 * @return
	 */
	public static RDataComponent buildDataComponent(RMGraphCell dataCell) {
		RDataComponent dataCom = null;
		if (RDataComponent.TYPE_DATA_MODEL_SEL.equals(dataCell.getKey())) {
			String location = dataCell
					.getParaStringValue(RDataSelectComponent.ATTR_DATA_MODEL_DATA_LOCATION);

			String dataName = dataCell
					.getParaStringValue(RDataSelectComponent.ATTR_DATA_MODEL_DATA_NAME);

			dataCom = new RDataSelectComponent(dataCell.getKey(), location,
					dataName);
		} else if (RDataComponent.TYPE_DATA_MODEL_IMP.equals(dataCell.getKey())) {
			dataCom = new RDataImportComponent(dataCell.getKey(), dataCell
					.getDataMap());
			
		}

		return dataCom;
	}

	public static boolean dataParaEquals(Object ob1, Object ob2) {
		if (ob1 == null) {
			return (ob1 == ob2);
		} else {
			return ob1.equals(ob2);
		}
	}

	public static List<String> clone(List<String> list) {
		if (list == null) {
			return null;
		}
		List<String> newList = new ArrayList<String>();

		for (int i = 0, k = list.size(); i < k; i++) {
			newList.add(list.get(i));
		}
		return newList;
	}
}
