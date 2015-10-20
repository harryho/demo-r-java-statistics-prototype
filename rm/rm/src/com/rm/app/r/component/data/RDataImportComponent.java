package com.rm.app.r.component.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rm.app.exception.RMException;
import com.rm.app.graph.Attribute;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;

/**
 * 
 * Data Component
 * 
 */
public class RDataImportComponent implements RDataComponent {

	// component's attribute, refer to rm.xml
	public static final String ATTR_DATA_MODEL_LOCATION = "ATTR_DATA_MODEL_LOCATION";
	public static final String ATTR_DATA_MODEL_DATA_NAME = "ATTR_DATA_MODEL_DATA_NAME";
	public static final String ATTR_DATA_MODEL_SEPERATION = "ATTR_DATA_MODEL_SEPERATION";

	public static final String ATTR_DATA_MODEL_TYPE = "ATTR_DATA_MODEL_TYPE";
	public static final String ATTR_DATA_MODEL_TYPE_RAWDATA = "ATTR_DATA_MODEL_TYPE_RAWDATA";
	public static final String VALUEOF_DATA_MODEL_TYPE_RAWDATA = "Raw data";
	public static final String ATTR_DATA_MODEL_TYPE_COVARIANCE = "ATTR_DATA_MODEL_TYPE_COVARIANCE";
	public static final String VALUEOF_DATA_MODEL_TYPE_COVARIANCE = "Covariance matrix";
	public static final String ATTR_DATA_MODEL_TYPE_CORRELATION = "ATTR_DATA_MODEL_TYPE_CORRELATION";
	public static final String VALUEOF_DATA_MODEL_TYPE_CORRELATION = "Correlation matrix";

	public static final String ATTR_DATA_MODEL_PARA_NOOBSERVATIONS = "Num of Obs";
	public static final String ATTR_DATA_MODEL_PARA_VAR = "variable"; // variable1..10
	public static final String ATTR_DATA_MODEL_PARA_VAR_DISPLAY_VALUE = "variable"; // for
	// display
	// only
	public static final String ATTR_DATA_MODEL_PARA_DATA_INITVAR = "init_var";
	public static final String ATTR_DATA_MODEL_PARA_MATRIX_DATA = "Matrix Data";

	private String key;

	private Map dataMap;

	private String dataFile;

	private String dataParaName;

	private String dataSeparation;

	private String dataType;

	private String numOfObs;

	private String matrix;

	// all var user choosed
	private List<String> pickVarList;

	// var for data
	private List<String> initialVarList;

	private int columns;

	public RDataImportComponent(String key, Map dataMap) {
		this.key = key;
		this.dataMap = dataMap;

		dataType = getParaValue(ATTR_DATA_MODEL_TYPE);

		if (VALUEOF_DATA_MODEL_TYPE_RAWDATA.equals(dataType)) {
			dataFile = getParaValue(ATTR_DATA_MODEL_LOCATION);

			dataParaName = getParaValue(ATTR_DATA_MODEL_DATA_NAME);

			dataSeparation = getParaValue(ATTR_DATA_MODEL_SEPERATION);
		} else {
			dataParaName = "impdata_" + (System.currentTimeMillis() % 100000);
			numOfObs = getParaValue(ATTR_DATA_MODEL_PARA_NOOBSERVATIONS);
			matrix = getParaValue(ATTR_DATA_MODEL_PARA_MATRIX_DATA);
			pickVarList = getPickedVars();
			initialVarList = getInitialVars();
			columns = getColumnNumFromInputMatrix(matrix);
			
		}

	}

	public List<String> getPickVarList() {
		return pickVarList;
	}

	public void setPickVarList(List<String> pickVarList) {
		this.pickVarList = pickVarList;
	}

	public void setInitialVarList(List<String> initialVarList) {
		this.initialVarList = initialVarList;
	}

	private String getParaValue(String key) {
		Attribute att = (Attribute) dataMap.get(key);
		if (att != null) {
			return (String) att.getValue();
		}
		return null;
	}

	public String getRCommand(RFlowProperties properties) {
		if (VALUEOF_DATA_MODEL_TYPE_RAWDATA.equals(dataType)) {
			return getReadCSVCommand(properties);
		} else {
			return getRefernCollectionRCommands(properties);
		}
	}

	public int handleCallBack(StringBuffer sb, RFlowProperties properties) {
		String str = sb.toString();
		RMLogger.info(getRCommand(properties));
		if (str != null && str.indexOf("Warning message") >= 0) {
			RMLogger.warn(str);
			return RComponent.ERROR;
		}
		return RComponent.SUCEESS;
	}

	public String getKey() {
		return key;
	}

	private String getReadCSVCommand(RFlowProperties properties) {
		return getDataParaName() + " <- read.csv('" + getDataFile()
				+ "', header = TRUE, row.names = NULL, sep = '"
				+ getDataSeparation() + "')";
	}

	private String getRefernCollectionRCommands(RFlowProperties properties) {
		StringBuffer sb = new StringBuffer();

		String initDataParaName = "init_"
				+ (System.currentTimeMillis() % 100000);
		sb.append(initDataParaName).append(" <- matrix(c(").append(matrix);
		sb.append("), ncol=" + columns + ", byrow=TRUE)\n");

		String varStr = "";
		for (int i = 0, k = initialVarList.size(); i < k; i++) {
			if ((i != k - 1)) {
				varStr = varStr + "'" + initialVarList.get(i) + "'" + ",";
			} else {
				varStr = varStr + "'" + initialVarList.get(i) + "'";
			}

		}
		sb.append(
				"rownames(" + initDataParaName + ") <- colnames("
						+ initDataParaName + ") <- c(" + varStr + ")").append(
				"\n");
		String seq = getSequenceNums();
		sb.append(dataParaName + " <- " + initDataParaName + "[c(" + seq
				+ "), c(" + seq + ")]");
		
		return sb.toString();
	}

	/**
	 * like: R.DHP[c(1, 3, 6,7,8,9), c(1, 3, 6,7,8,9)]
	 * 
	 * @return 1, 3, 6,7,8,9
	 */
	private String getSequenceNums() {
		String ret = "";
		for (int i = 0; i < pickVarList.size(); i++) {
			Object var = pickVarList.get(i);
			for (int j = 0; j < initialVarList.size(); j++) {
				RMLogger.debug("pickVarList.get(i): " + var);
				RMLogger.debug("initialVarList.get(j): "
						+ initialVarList.get(j));
				if (var.equals(initialVarList.get(j))) {
					ret = ret + (j + 1);
					if (i != pickVarList.size() - 1) {
						ret += ",";
					}
				}
			}
		}
		return ret;
	}

	public List<String> getAllNumericVariables() {
		List<String> list = new ArrayList<String>();
		if (VALUEOF_DATA_MODEL_TYPE_RAWDATA.equals(dataType)) {
			list = RDataHelper.getAllNumericVarsFromR(dataParaName,
					getReadCSVCommand(null));
		} else {
			// try to load data into data cache.
			List<String> dataList = RDataHelper.getAllNumericVarsFromR(
					dataParaName, getRefernCollectionRCommands(null));
			RMLogger.debug("dataList=" + dataList);
			if (dataList != null && dataList.size() > 0) {
				list = this.pickVarList;
			}
		}
		return list;
	}

	private int getColumnNumFromInputMatrix(String matrix) {
		int rowNum = 0;
		if (matrix == null) {
			return rowNum;
		}
		String[] rows = matrix.split("\n");
		if (rows == null || rows.length == 0) {
			return rowNum;
		}
		String firstRow = rows[0];
		RMLogger.debug("first low begin :");
		RMLogger.debug(firstRow);
		RMLogger.debug("first low end :\n");
		if (firstRow.indexOf(",") != -1) {
			String[] tmp = firstRow.split(",");
			rowNum = tmp.length;
		} else {// separate by space or tab
			String[] tmp = firstRow.split("\t");
			rowNum = tmp.length;
		}
		return rowNum - 1;
	}

	private List<String> getPickedVars() {
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= 10; i++) {
			String var = getParaValue(ATTR_DATA_MODEL_PARA_VAR + i);
			if (var != null && !"".equals(var = var.trim())) {
				list.add(var);
			}
		}
		return list;
	}

	private List<String> getInitialVars() {
		List<String> list = new ArrayList<String>();
		for (int i = 1; i <= 10; i++) {
			String var = getParaValue(ATTR_DATA_MODEL_PARA_DATA_INITVAR + i);
			if (var != null && !"".equals(var = var.trim())) {
				list.add(var);
			}
		}
		return list;
	}

	public boolean validation() throws Exception {
		if (VALUEOF_DATA_MODEL_TYPE_RAWDATA.equals(dataType)) {
			if (dataFile == null || "".equals(dataFile = dataFile.trim())) {
				throw new RMException(key
						+ ":  Please imput the Data location in DataModel!");
			}
			File data = new File(dataFile);
			if (!data.exists()) {
				throw new RMException("Data file " + dataFile
						+ " is not existed!");
			}
			if (dataParaName == null
					|| "".equals(dataParaName = dataParaName.trim())) {
				throw new RMException(key
						+ ":  Please imput the Data name in DataModel!");
			}
			if (dataSeparation == null
					|| "".equals(dataSeparation = dataSeparation.trim())) {
				throw new RMException(key
						+ ":  Please imput the Separation in DataModel!");
			}

		} else {

			int num = 0;
			try {
				num = Integer.valueOf(numOfObs);
			} catch (Exception e) {
				RMLogger
						.warn("The numOfObs of Importing data component is incorrect!");
				throw new RMException(
						key
								+ ": The numOfObs of Importing data component is incorrect!");
			}
			if (pickVarList.size() == 0 || initialVarList.size() == 0) {
				RMLogger
						.warn("Please input variable name into importing data component!");
				throw new RMException(
						key
								+ ":  Please input variable name into importing data component!");
			}
			for (int i = 0, k = pickVarList.size(); i < k; i++) {
				if (!initialVarList.contains(pickVarList.get(i))) {
					RMLogger
							.warn("Please input correct variable name for importing data component!");
					throw new RMException(
							key
									+ ":  Please input correct variable name for importing data component!");
				}
			}
			if (matrix == null || columns == 0) {
				RMLogger
						.warn("The Matrix of Importing data component is incorrect!");
				throw new RMException(
						key
								+ ":  The Matrix of Importing data component is incorrect!");
			}

		}
		return false;
	}

	public long holdedMS() {
		return 800;
	}

	public String getDataParaName() {
		return dataParaName;
	}

	public void setDataParaName(String dataParaName) {
		this.dataParaName = dataParaName;
	}

	public String getDataSeparation() {
		return dataSeparation;
	}

	public void setDataSeparation(String dataSeparation) {
		this.dataSeparation = dataSeparation;
	}

	public String getDataFile() {
		return dataFile;
	}

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}

	public boolean equals(RDataComponent com) {
		if (!(com instanceof RDataImportComponent)) {
			return false;
		}
		RDataImportComponent data = (RDataImportComponent) com;
		if (!dataType.equals(data.getDataType())) {
			return false;
		}
		if (VALUEOF_DATA_MODEL_TYPE_RAWDATA.equals(dataType)) {

			if (RDataHelper.dataParaEquals(dataFile, data.getDataFile())
					&& RDataHelper.dataParaEquals(dataParaName, data
							.getDataParaName())
					&& RDataHelper.dataParaEquals(dataSeparation, data
							.getDataSeparation())) {
				return true;
			}

		} else {
			if (RDataHelper.dataParaEquals(numOfObs, data.getNumOfObs())
					&& RDataHelper.dataParaEquals(matrix, data.getMatrix())
					&& RDataHelper.dataParaEquals(initialVarList, data
							.getInitialVarList())) {
				return true;
			}

		}
		return false;
	}

	public String getDataType() {
		return dataType;
	}

	public String getNumOfObs() {
		return numOfObs;
	}

	public String getMatrix() {
		return matrix;
	}

	public List<String> getInitialVarList() {
		return initialVarList;
	}

}
