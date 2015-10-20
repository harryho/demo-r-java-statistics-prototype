package com.rm.app.r.flow;

import java.util.List;

public class RFlowProperties {

	private List<String> depend_var;
	private List<String> independ_var;
	private List<String> def_depend_var;
	private List<String> def_independ_var;

	private String eqType;

	// <-----------------------------------------------

	// var name in R context

	private String dataParaName;

	// summary(summaryParam)
	private String summaryParam;

	// summaryResultVar <- summary()
	private String summaryResultVar;

	// ----------------------------------------------->

	private String pngFile;

	// <---------------------for SEM Only --------------
	private List<String> semEquations;
	private List<String> endoVars;
	private List<String> exoVars;
	private String semModelVar;
	private String numOfObs;
	// all var user choosed
	private List<String> semPickVarList;
	// var for data
	private List<String> semInitialVarList;
	
	private String semMatrix;

	// -------------for SEM Only----------------------->
	
        // ------------- for GLM ---------------------
	private String family;
        // ------------- for GLM ---------------------	
	
	public String getFamily() {
	    return family;
	}

	public void setFamily(String family) {
	    this.family = family;
	}
	

	public String getSemMatrix() {
		return semMatrix;
	}

	public void setSemMatrix(String semMatrix) {
		this.semMatrix = semMatrix;
	}

	public String getNumOfObs() {
		return numOfObs;
	}

	public void setNumOfObs(String numOfObs) {
		this.numOfObs = numOfObs;
	}

	public void generateSemModelVar() {
		semModelVar = "model." + this.getDataParaName();
	}

	public List<String> getSemEquations() {
		return semEquations;
	}

	public void setSemEquations(List<String> semEquations) {
		this.semEquations = semEquations;
	}

	public List<String> getEndoVars() {
		return endoVars;
	}

	public void setEndoVars(List<String> endoVars) {
		this.endoVars = endoVars;
	}

	public List<String> getExoVars() {
		return exoVars;
	}

	public void setExoVars(List<String> exoVars) {
		this.exoVars = exoVars;
	}

	public List<String> getSemPickVarList() {
		return semPickVarList;
	}

	public void setSemPickVarList(List<String> semPickVarList) {
		this.semPickVarList = semPickVarList;
	}

	public List<String> getSemInitialVarList() {
		return semInitialVarList;
	}

	public void setSemInitialVarList(List<String> semInitialVarList) {
		this.semInitialVarList = semInitialVarList;
	}

	public String getSemModelVar() {
		return semModelVar;
	}

	public void setSemModelVar(String semModelVar) {
		this.semModelVar = semModelVar;
	}

	public String getPngFile() {
		return pngFile;
	}

	public String getEqType() {
		return eqType;
	}

	public void setEqType(String eqType) {
		this.eqType = eqType;
	}

	public void setPngFile(String pngFile) {
		this.pngFile = pngFile;
	}

	public String getSummaryResultVar() {
		return summaryResultVar;
	}

	public void setSummaryResultVar(String summaryResultVar) {
		this.summaryResultVar = summaryResultVar;
	}

	public RFlowProperties() {
		generateNewSummaryResultVar();
		generateNewSummaryParam();
	}

	public void generateNewSummaryResultVar() {
		summaryResultVar = "sm_" + System.currentTimeMillis() % 100000;
	}

	public void generateNewSummaryParam() {
		summaryParam = "r_" + System.currentTimeMillis() % 100000;
	}

	public String getDataParaName() {
		return dataParaName;
	}

	public void setDataParaName(String dataParaName) {
		this.dataParaName = dataParaName;
	}

	public List<String> getDepend_var() {
		return depend_var;
	}

	public void setDepend_var(List<String> depend_var) {
		this.depend_var = depend_var;
	}

	public List<String> getIndepend_var() {
		return independ_var;
	}

	public void setIndepend_var(List<String> independ_var) {
		this.independ_var = independ_var;
	}

	public List<String> getDef_depend_var() {
		return def_depend_var;
	}

	public void setDef_depend_var(List<String> def_depend_var) {
		this.def_depend_var = def_depend_var;
	}

	public List<String> getDef_independ_var() {
		return def_independ_var;
	}

	public void setDef_independ_var(List<String> def_independ_var) {
		this.def_independ_var = def_independ_var;
	}

	public String getSummaryParam() {
		return summaryParam;
	}

	public void setSummaryParam(String summaryParam) {
		this.summaryParam = summaryParam;
	}

}
