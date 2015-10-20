package com.rm.app.r.component.data;

import java.util.List;

import com.rm.app.r.component.RComponent;

public interface RDataComponent extends RComponent{

	public static final String TYPE_DATA_MODEL = "dataModel";
	public static final String TYPE_DATA_MODEL_IMP = "dataModel.importData";
	public static final String TYPE_DATA_MODEL_SEL = "dataModel.selectData";

	public String getDataParaName();

	public List<String> getAllNumericVariables();
	
	public boolean equals(RDataComponent com);
}
