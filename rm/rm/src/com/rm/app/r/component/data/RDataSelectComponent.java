package com.rm.app.r.component.data;

import java.util.List;

import com.rm.app.exception.RMException;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.flow.RFlowProperties;

public class RDataSelectComponent implements RDataComponent {

	public static final String ATTR_DATA_MODEL_DATA_LOCATION = "ATTR_DATA_MODEL_DATA_LOCATION";
	public static final String ATTR_DATA_MODEL_DATA_NAME = "ATTR_DATA_MODEL_DATA_NAME";

	private String key;

	private String dataParaName;

	private String dataLibrary;

	private String LOAD_LIB_TEMPL="options(show.error.messages=FALSE);  \n " +
			"load<-function(lib){ vc<-c(.libPaths());  for ( k in 1 : length(vc)) { t<-try(library( lib, pos=2, lib.loc=vc[k], character.only=TRUE)); } } \n " +
			"load('$LIB') \n " +
			" options(show.error.messages=TRUE);  \n ";
	
	public RDataSelectComponent(String key, String dataLibrary, String dataParaName) {
		this.key = key;
		this.dataLibrary = dataLibrary;
		this.dataParaName = dataParaName;
	}

	public String getDataLibrary() {
		return dataLibrary;
	}

	public String getKey() {
		return key;
	}

	public String getRCommand(RFlowProperties properties) {
		StringBuffer selectCmd = new StringBuffer();
		System.out.println("   dataLibrary " + dataLibrary);
		String cmd="";
		if (dataLibrary != null && !"".equals(dataLibrary = dataLibrary.trim())) {
//			selectCmd.append("library(" + dataLibrary + ")").append("\n");
		    cmd=LOAD_LIB_TEMPL.replace("$LIB", dataLibrary);
		}
		
		selectCmd.append(cmd);
		selectCmd.append("data(" + dataParaName + ")");
		
		
		return selectCmd.toString();
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

	public long holdedMS() {
		return 500;
	}

	public boolean validation() throws Exception {
		if (dataParaName == null || "".equals(dataParaName = dataParaName.trim())) {
			throw new RMException(key
					+ ":  Please imput the Data Name in Data Selecting Model!");
		}
		return false;
	}

	public String getDataParaName() {
		return dataParaName;
	}

	public List<String> getAllNumericVariables() {
		return RDataHelper.getAllNumericVarsFromR(dataParaName, getRCommand(null));
	}

	public boolean equals(RDataComponent com) {
		if (!(com instanceof RDataSelectComponent)) {
			return false;
		}
		RDataSelectComponent data = (RDataSelectComponent) com;
		if (RDataHelper.dataParaEquals(dataLibrary, data.getDataLibrary())
				&& RDataHelper.dataParaEquals(dataParaName, data.getDataParaName())) {
			return true;
		}
		return false;
	}

}
