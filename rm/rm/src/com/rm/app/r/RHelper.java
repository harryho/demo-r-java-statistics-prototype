package com.rm.app.r;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rm.app.RMAppContext;
import com.rm.app.exception.RMException;
import com.rm.app.graph.Attribute;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.data.RDataComponent;
import com.rm.app.r.component.data.RDataHelper;
import com.rm.app.r.component.data.RDataImportComponent;
import com.rm.app.r.component.equation.RDefaultEQComponent;
import com.rm.app.r.component.equation.REQComponent;
import com.rm.app.r.component.equation.RSEMComponent;
import com.rm.app.r.component.estimation.RESComponent;
import com.rm.app.r.component.estimation.RGLSComponent;
import com.rm.app.r.component.estimation.RMLEComponent;
import com.rm.app.r.component.estimation.RMRComponent;
import com.rm.app.r.component.evaluation.RAutoCorrectComponent;
import com.rm.app.r.component.evaluation.RCookComponent;
import com.rm.app.r.component.evaluation.RDffitsComponent;
import com.rm.app.r.component.evaluation.REVComponent;
import com.rm.app.r.component.evaluation.RHATComponent;
import com.rm.app.r.component.evaluation.RNFIComponent;
import com.rm.app.r.component.evaluation.RNcvComponent;
import com.rm.app.r.component.evaluation.RNormalityComponent;
import com.rm.app.r.component.evaluation.RPValueComponent;
import com.rm.app.r.component.evaluation.RRSquareComponent;
import com.rm.app.r.component.evaluation.RRmseaComponent;
import com.rm.app.r.component.evaluation.RRstudentComponent;
import com.rm.app.r.component.evaluation.RVIFComponent;
import com.rm.app.r.component.explanation.RComExComponent;
import com.rm.app.r.component.explanation.RCusExComponent;
import com.rm.app.r.component.explanation.REXComponent;
import com.rm.app.r.component.explanation.RSTDExComponent;
import com.rm.app.r.flow.RFlow;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.r.flow.output.GraphWindow;
import com.rm.app.r.flow.output.TextWindow;

public class RHelper {

    public static GraphWindow CURRENT_OUTPUT_GRAGH_WINDOW = null;
    public static TextWindow CURRENT_OUTPUT_TEXT_WINDOW = null;

    private static RMWorker CURRENT_WORKER = null;

    public static RFlow CURRENT_FLOW = null;

    public static boolean isRunning = false;

    public static boolean stopReasearchFlow() {
	RMLogger.debug("==========> i want to stop! " + CURRENT_FLOW.hasStopCommand);
	RMLogger.debug("==========> i want to stop CURRENT_WORKER =  " + CURRENT_WORKER);
	RMLogger.debug("==========> i want to stop CURRENT_FLOW =  " + CURRENT_FLOW);
	if (CURRENT_WORKER != null && CURRENT_FLOW != null) {
	    if (isRunning) {
		CURRENT_FLOW.hasStopCommand = true;
		CURRENT_WORKER.interrupt();
		cleanRunningStatus();
	    }
	}
	return true;
    }

    public static boolean runResearchFlow(List flows) throws Exception {

	setRunningStatus();
	cleanCurrentOutputWindow();

	boolean result = false;
	if (flows == null || flows.size() == 0) {
	    return result;
	}
	RMLogger.debug("flows:" + flows);
	List<RFlow> rfolws = getRResearchFlow(flows);
	RMLogger.info("Validation is ok.");
	CURRENT_WORKER = new RMWorker(rfolws) {

	    boolean result = false;

	    public Object construct() {
		TextWindow tw = new TextWindow();
		RMLogger.warn("Research flow is running ... ");
		for (int i = 0, k = rfolws.size(); i < k; i++) {
		    CURRENT_FLOW = rfolws.get(i);
		    try {
			result = CURRENT_FLOW.run();
			if (result) {
			    break;
			} else {
			    if (i != k - 1) {// not the last branch
			    }
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}
		if (result) {
		    RMLogger.warn("Research flow launch successfully!");
		    tw.display();
		    CURRENT_OUTPUT_TEXT_WINDOW = tw;
		} else {
		    RMLogger.warn("Research flow launch unsuccessfully!");
		}

		RMLogger.warn("END");
		cleanRunningStatus();
		return result;
	    }

	};

	CURRENT_WORKER.start();
	return result;
    }

    public static void cleanRunningStatus() {
	isRunning = false;
	RMAppContext.stopButton.setBackground(new Color(204, 100, 100));
	RMAppContext.runButton.setBackground(new Color(255, 255, 255));
    }

    public static void setRunningStatus() {
	isRunning = true;
	RMAppContext.runButton.setBackground(new Color(204, 100, 100));
	RMAppContext.stopButton.setBackground(new Color(255, 255, 255));
    }

    private static List<RFlow> getRResearchFlow(List flows) throws Exception {

	List<RFlow> runnableFlows = new ArrayList<RFlow>(flows.size());

	for (int i = 0; i < 1; i++) {
	    RFlow flow = new RFlow(i);
	    List subList = (List) flows.get(i);

	    RFlowProperties properties = new RFlowProperties();

	    for (int k = 0; k < subList.size(); k++) {
		RMGraphCell cell = (RMGraphCell) subList.get(k);
		String cellType = cell.getClassifierKey();
//		 System.out.println( cellType + "    "+ cell.getKey());
		if (RDataComponent.TYPE_DATA_MODEL.equals(cellType)) {

		    List<String> def_depend_var = new ArrayList<String>();
		    List<String> def_independ_var = new ArrayList<String>();

		    RDataComponent dataCom = RDataHelper.buildDataComponent(cell);
		    dataCom.validation();

		    dataCom = RDataHelper.getDefaultVariables(dataCom, def_independ_var, def_depend_var);

		    RMLogger.debug("getDataParaName=============> " + dataCom.getDataParaName());

		    properties.setDataParaName(dataCom.getDataParaName());
		    properties.setDef_independ_var(def_independ_var);
		    properties.setDef_depend_var(def_depend_var);

		    if (dataCom instanceof RDataImportComponent) {
			RDataImportComponent a = (RDataImportComponent) dataCom;
			properties.setNumOfObs(a.getNumOfObs());
			properties.setSemPickVarList(a.getPickVarList());
			properties.setSemInitialVarList(a.getInitialVarList());
			properties.setSemMatrix(a.getMatrix());
		    }

		    flow.addItem(dataCom);

		} else if (REQComponent.TYPE_EQUATION.equals(cellType)) {

		    // eq not need to add to the executed flow
		    RDefaultEQComponent eqitem = new RDefaultEQComponent(cell.getKey());

		    properties.setEqType(eqitem.getKey());

		    if (REQComponent.TYPE_EQUATION_MR.equals(eqitem.getKey())) {

			// check whether the data file just include 1
			// independent variables
			if (properties.getDef_independ_var() != null && properties.getDef_independ_var().size() == 1) {
			    throw new RMException(cell.getUserObject()
				    + ":  Only Simple Regression can be selected for this data file!");
			}

			// get independent variable & dependent variable
			Attribute dependentVar = (Attribute) cell.getDataMap().get("DEPENDENT");

			String dependentVarValue = null;
			if (dependentVar != null) {
			    List<String> dependentList = (List<String>) dependentVar.getValue();
			    if (dependentList.size() > 0) {
				dependentVarValue = dependentList.get(0);
			    }
			}

			Attribute independentVar = (Attribute) cell.getDataMap().get("INDEPENDENT");

			List independentVarList = null;

			if (independentVar != null) {
			    independentVarList = (List) independentVar.getValue();
			}
			RMLogger.debug("independentVarList---->  " + independentVarList);

			if (dependentVarValue == null || "".equals(dependentVarValue = dependentVarValue.trim())) {
			    // get the first var from data file by default
			    if (properties.getDef_depend_var() == null || properties.getDef_depend_var().size() == 0) {
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Dependent Variable in MR Model!");
			    }

			    properties.setDepend_var(properties.getDef_depend_var());
			    RMLogger.warn("Use default dependent variable: " + properties.getDepend_var());

			} else {
			    List<String> depend_var = new ArrayList<String>();
			    depend_var.add(dependentVarValue);
			    checkUserImputedVars(depend_var, properties.getDef_depend_var(), properties
				    .getDef_independ_var());
			    properties.setDepend_var(depend_var);

			}

			if (independentVarList == null || independentVarList.size() == 0) {
			    // if user not input, use default vars
			    if (properties.getDef_independ_var() == null
				    || properties.getDef_independ_var().size() == 0) {
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Independent Variable in MR Model!");
			    }

			    List<String> independ_var = new ArrayList<String>();
			    for (int ti = 0, tk = properties.getDef_independ_var().size(); ti < tk; ti++) {
				String tmp = properties.getDef_independ_var().get(ti);
				if (!properties.getDepend_var().contains(tmp)) {
				    independ_var.add(tmp);
				}
			    }
			    properties.setIndepend_var(independ_var);
			    RMLogger.warn("Use default independent variable: " + properties.getIndepend_var());
			} else {
			    checkUserImputedVars(independentVarList, properties.getDef_depend_var(), properties
				    .getDef_independ_var());
			    properties.setIndepend_var(independentVarList);
			    RMLogger.info("Independent variable: " + properties.getIndepend_var());
			}

		    } else if (REQComponent.TYPE_EQUATION_SR.equals(eqitem.getKey())) {

			// get independent variable & dependent variable
			Attribute independentVar = (Attribute) cell.getDataMap().get(
				REQComponent.ATTR_EQUATION_INDEP_VAR);
			Attribute dependentVar = (Attribute) cell.getDataMap().get(REQComponent.ATTR_EQUATION_DEP_VAR);

			String dependentVarValue = (String) dependentVar.getValue();

			String independentVarValue = (String) independentVar.getValue();

			if (dependentVarValue == null || "".equals(dependentVarValue = dependentVarValue.trim())) {
			    if (properties.getDef_depend_var() == null || properties.getDef_depend_var().size() == 0) {
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Dependent Variable in SR Model!");
			    }
			    properties.setDepend_var(properties.getDef_depend_var());
			    RMLogger.warn("Use default dependent variable: " + properties.getDepend_var());
			} else {
			    List<String> depend_var = new ArrayList<String>();
			    depend_var.add(dependentVarValue);
			    checkUserImputedVars(depend_var, properties.getDef_depend_var(), properties
				    .getDef_independ_var());
			    properties.setDepend_var(depend_var);
			}

			if (independentVarValue == null || "".equals(independentVarValue = independentVarValue.trim())) {
			    if (properties.getDef_independ_var() == null
				    || properties.getDef_independ_var().size() == 0) {
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Independent Variable in SR Model!");
			    }

			    // Use the first numeric variable for independent
			    // variable
			    List<String> independ_var = new ArrayList<String>();
			    for (int ti = 0, tk = properties.getDef_independ_var().size(); ti < tk; ti++) {
				String tmp = properties.getDef_independ_var().get(ti);
				if (!properties.getDepend_var().contains(tmp)) {
				    independ_var.add(tmp);
				    break;
				}
			    }

			    properties.setIndepend_var(independ_var);
			    RMLogger.warn("Use default independent variable: " + independ_var);
			} else {
			    List<String> independ_var = new ArrayList<String>();
			    independ_var.add(independentVarValue);

			    checkUserImputedVars(independ_var, properties.getDef_depend_var(), properties
				    .getDef_independ_var());
			    properties.setIndepend_var(independ_var);
			}
		    } else if (REQComponent.TYPE_EQUATION_SEM.equals(eqitem.getKey() + "TODO")) {// TODO
												 // remove
												 // this
												 // function
												 // temporary

			Attribute endoVar = (Attribute) cell.getDataMap().get("ENDO");
			Attribute exoVar = (Attribute) cell.getDataMap().get("EXO");

			String endoVarValue = null;
			List<String> endoList = null;
			List<String> exoVarList = null;

			List<String> equationList = new ArrayList<String>();

			for (int n = 1; n <= 10; n++) {
			    Attribute equationVar = (Attribute) cell.getDataMap().get(
				    REQComponent.ATTR_EQUATION_EQUATION + n);
			    Object equ = null;
			    if (equationVar != null)
				equ = equationVar.getValue();

			    if (equ != null && equ instanceof String && ((String) equ).trim().length() > 0)
				equationList.add(((String) equ).trim());
			}

			boolean auto = false;

			if (endoVar == null && exoVar == null && equationList.size() < 1) {
			    auto = true;
			} else {
			    if (endoVar != null)
				endoList = (List<String>) endoVar.getValue();
			    if (exoVar != null)
				exoVarList = (List<String>) exoVar.getValue();

			    if ((endoVar == null || endoList == null || endoList.size() == 0)
				    && (exoVar == null || exoVarList == null || exoVarList.size() == 0)
				    && equationList.size() < 1) {
				auto = true;
			    }

			}
			System.out.println("auto " + auto);
			if (auto) {
			    System.out.println();
			    List<String> deList = properties.getDef_depend_var();
			    List<String> inList = properties.getDef_independ_var();
			    System.out.println(deList);
			    System.out.println(inList);
			    if (null == deList || deList.size() == 0)
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Endogeneous Variable in SEM Model!");

			    if (null == inList || inList.size() == 0)
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Exogenous Variable in SEM Model!");

			    endoList = new ArrayList<String>();
			    exoVarList = new ArrayList<String>();

			    String depend = deList.get(0);
			    if (depend == null || depend.trim().length() == 0)
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Endogeneous Variable in SEM Model!");
			    else
				endoList.add(depend);

			    for (String in : inList) {
				if (in != null || in.trim().length() > 0)
				    exoVarList.add(in.trim());
			    }

			    if (exoVarList.size() == 0)
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Exogenous Variable in SEM Model!");
			    else {
				StringBuffer sb = new StringBuffer();
				sb.append(depend).append("=");
				int n = 1;
				for (String exo : exoVarList) {
				    if ((n++) == exoVarList.size())
					sb.append(exo);
				    else
					sb.append(exo).append("+");
				}

				equationList.add(sb.toString());
				properties.setExoVars(exoVarList);
				properties.setEndoVars(endoList);
				properties.setSemEquations(equationList);

			    }

			} else {

			    if (endoVar == null || endoList == null || endoList.size() == 0) {
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Endogeneous Variable in SEM Model!");
			    }

			    if (exoVar == null || exoVarList == null || exoVarList.size() == 0) {
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Exogenous Variable in SEM Model!");
			    }

			    if (equationList.size() == 0) {
				throw new RMException(cell.getUserObject()
					+ ":  Please imput structure equation in SEM Model!");
			    } else {
				checkSEMEquation(exoVarList, endoList, equationList);
				properties.setExoVars(exoVarList);
				properties.setEndoVars(endoList);
				properties.setSemEquations(equationList);

			    }
			}

			if (properties.getEndoVars() == null || properties.getEndoVars().size() < 1) {
			    throw new RMException("Endogeneous variable is not available !");
			}
			if (properties.getExoVars() == null || properties.getExoVars().size() < 1) {
			    throw new RMException("Exogenous variable is not available !");
			}
			if (properties.getSemEquations() == null || properties.getSemEquations().size() < 1) {
			    throw new RMException("Equation is not available !");
			}

			RMLogger.info("Endogeneous variable: " + properties.getEndoVars());
			RMLogger.info("Exogenous variable: " + properties.getExoVars());
			RMLogger.info("Equation variable: " + properties.getSemEquations());

			RSEMComponent sem = new RSEMComponent(cell.getKey());
			flow.addItem(sem);

		    } else if (REQComponent.TYPE_EQUATION_GLM.equals(cell.getKey())) {
			Attribute att = (Attribute) cell.getDataMap().get(REQComponent.ATTR_EQUATION_FUNC_FAMILY);
			String value = (String) att.getValue();
			if (value == null || value.trim().length() <= 0)
			    throw new RMException("Family of GLM is not available !");
			else
			    properties.setFamily(value.trim());

			// check whether the data file just include 1
			// independent variables
			if (properties.getDef_independ_var() != null && properties.getDef_independ_var().size() == 1) {
			    throw new RMException(cell.getUserObject()
				    + ":  Only Simple Regression can be selected for this data file!");
			}

			// get independent variable & dependent variable
			Attribute dependentVar = (Attribute) cell.getDataMap().get("DEPENDENT");

			String dependentVarValue = null;
			if (dependentVar != null) {
			    List<String> dependentList = (List<String>) dependentVar.getValue();
			    if (dependentList.size() > 0) {
				dependentVarValue = dependentList.get(0);
			    }
			}

			Attribute independentVar = (Attribute) cell.getDataMap().get("INDEPENDENT");

			List independentVarList = null;

			if (independentVar != null) {
			    independentVarList = (List) independentVar.getValue();
			}
			RMLogger.debug("independentVarList---->  " + independentVarList);

			if (dependentVarValue == null || "".equals(dependentVarValue = dependentVarValue.trim())) {
			    // get the first var from data file by default
			    if (properties.getDef_depend_var() == null || properties.getDef_depend_var().size() == 0) {
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Dependent Variable in MR Model!");
			    }

			    properties.setDepend_var(properties.getDef_depend_var());
			    RMLogger.warn("Use default dependent variable: " + properties.getDepend_var());

			} else {
			    List<String> depend_var = new ArrayList<String>();
			    depend_var.add(dependentVarValue);
			    checkUserImputedVars(depend_var, properties.getDef_depend_var(), properties
				    .getDef_independ_var());
			    properties.setDepend_var(depend_var);

			}

			if (independentVarList == null || independentVarList.size() == 0) {
			    // if user not input, use default vars
			    if (properties.getDef_independ_var() == null
				    || properties.getDef_independ_var().size() == 0) {
				throw new RMException(cell.getUserObject()
					+ ":  Please imput the Independent Variable in MR Model!");
			    }

			    List<String> independ_var = new ArrayList<String>();
			    for (int ti = 0, tk = properties.getDef_independ_var().size(); ti < tk; ti++) {
				String tmp = properties.getDef_independ_var().get(ti);
				if (!properties.getDepend_var().contains(tmp)) {
				    independ_var.add(tmp);
				}
			    }
			    properties.setIndepend_var(independ_var);
			    RMLogger.warn("Use default independent variable: " + properties.getIndepend_var());
			} else {
			    checkUserImputedVars(independentVarList, properties.getDef_depend_var(), properties
				    .getDef_independ_var());
			    properties.setIndepend_var(independentVarList);
			    RMLogger.info("Independent variable: " + properties.getIndepend_var());
			}

		    }

		} else if (RESComponent.TYPE_ESTIMATION.equals(cellType)) {
		    if (RESComponent.TYPE_ESTIMATION_OLS.equals(cell.getKey())) {
			RMRComponent esitem = new RMRComponent(cell.getKey(), properties);
			esitem.validation();
			flow.addItem(esitem);
		    } else if (RESComponent.TYPE_ESTIMATION_MLE.equals(cell.getKey())) {
			RMLEComponent mle = new RMLEComponent(cell.getKey(), properties);
			mle.validation();
			flow.addItem(mle);
		    } else if (RESComponent.TYPE_ESTIMATION_GLS.equals(cell.getKey())) {
			RGLSComponent gls = new RGLSComponent(cell.getKey(), properties);
			gls.validation();
			flow.addItem(gls);
		    }

		} else if (REVComponent.TYPE_EVALUATION.equals(cellType)) {

		    Attribute att = (Attribute) cell.getDataMap().get(REVComponent.ATTR_EVALUATION_LOG_FUNCTION);

		    String value = (String) att.getValue();

		    if (REVComponent.TYPE_EVALUATION_RS.equals(cell.getKey())) {
			RRSquareComponent rsquare = new RRSquareComponent(cell.getKey(), value);
			rsquare.validation();
			flow.addItem(rsquare);
		    } else if (REVComponent.TYPE_EVALUATION_PV.equals(cell.getKey())) {
			RPValueComponent pvalue = new RPValueComponent(cell.getKey(), value);
			pvalue.validation();
			flow.addItem(pvalue);
		    } else if (REVComponent.TYPE_EVALUATION_RSTUD.equals(cell.getKey())) {
			RRstudentComponent rstudent = new RRstudentComponent(cell.getKey(), value);
			rstudent.validation();
			flow.addItem(rstudent);
		    } else if (REVComponent.TYPE_EVALUATION_VIF.equals(cell.getKey())) {
			RVIFComponent vif = new RVIFComponent(cell.getKey(), value);
			vif.validation();
			flow.addItem(vif);
		    } else if (REVComponent.TYPE_EVALUATION_NCV.equals(cell.getKey())) {
			RNcvComponent ncv = new RNcvComponent(cell.getKey(), value);
			ncv.validation();
			flow.addItem(ncv);
		    } else if (REVComponent.TYPE_EVALUATION_AUTOCORRECT.equals(cell.getKey())) {
			RAutoCorrectComponent auto = new RAutoCorrectComponent(cell.getKey(), value);
			auto.validation();
			flow.addItem(auto);
		    } else if (REVComponent.TYPE_EVALUATION_NORMALITY.equals(cell.getKey())) {
			RNormalityComponent norm = new RNormalityComponent(cell.getKey(), value);
			norm.validation();
			flow.addItem(norm);
		    } else if (REVComponent.TYPE_EVALUATION_NFI.equals(cell.getKey())) {
			RNFIComponent nfi = new RNFIComponent(cell.getKey(), value);
			nfi.validation();
			flow.addItem(nfi);
		    } else if (REVComponent.TYPE_EVALUATION_RMSEA.equals(cell.getKey())) {
			RRmseaComponent rmsea = new RRmseaComponent(cell.getKey(), value);
			rmsea.validation();
			flow.addItem(rmsea);
		    } else if (REVComponent.TYPE_EVALUATION_COOKS.equals(cell.getKey())) {
			RCookComponent cook = new RCookComponent(cell.getKey(), value);
			cook.validation();
			flow.addItem(cook);
		    }else if (REVComponent.TYPE_EVALUATION_DFFITS.equals(cell.getKey())) {
			RDffitsComponent dfftis = new RDffitsComponent(cell.getKey(), value);
			dfftis.validation();
			flow.addItem(dfftis);
		    }else if (REVComponent.TYPE_EVALUATION_HAT.equals(cell.getKey())) {
			RHATComponent hat = new RHATComponent(cell.getKey(), value);
			hat.validation();
			flow.addItem(hat);
		    }
		    
		    
		} else if (REXComponent.TYPE_EXPLANATION.equals(cellType)) {
		    if (REXComponent.TYPE_EXPLANATION_STD.equals(cell.getKey())) {
			RSTDExComponent std = new RSTDExComponent(cell.getKey());
			flow.addItem(std);
		    } else if (REXComponent.TYPE_EXPLANATION_COM.equals(cell.getKey())) {
			RComExComponent com = new RComExComponent(cell.getKey());
			flow.addItem(com);
		    } else if (REXComponent.TYPE_EXPLANATION_CUS.equals(cell.getKey())) {
			RCusExComponent cus = new RCusExComponent(cell.getKey());
			flow.addItem(cus);
		    } else if (REXComponent.TYPE_EXPLANATION_STD4SEM.equals(cell.getKey())) {
			RSTDExComponent std = new RSTDExComponent(cell.getKey());
			flow.addItem(std);
		    } else if (REXComponent.TYPE_EXPLANATION_COM4SEM.equals(cell.getKey())) {
			RComExComponent com = new RComExComponent(cell.getKey());
			flow.addItem(com);
		    } else if (REXComponent.TYPE_EXPLANATION_CUS4SEM.equals(cell.getKey())) {
			RCusExComponent cus = new RCusExComponent(cell.getKey());
			flow.addItem(cus);
		    }

		}

	    }
	    flow.setProperties(properties);
	    runnableFlows.add(flow);
	}
	return runnableFlows;

    }

    private static void checkSEMEquation(List<String> exoVarList, List<String> endoList, List<String> equationList)
	    throws RMException {
	Pattern p = Pattern.compile("^[a-zA-Z][a-zA-Z0-9_\\.]*\\s*[=]\\s*[a-zA-Z][a-zA-Z0-9_\\.]*\\s*.*");

	boolean passed = true;
	String eq = "";
	for (int i = 0; i < equationList.size(); i++) {
	    String equation = equationList.get(i);
	    Matcher m = p.matcher(equation);
	    eq = equation;
	    if (!m.matches()) {
		passed = false;
		break;
	    } else {
		String left = equation.substring(0, equation.indexOf("="));
		String right = equation.substring(equation.indexOf("=") + 1);
		int enc = 0;
		for (String endo : endoList) {
		    if (endo.equals(left.trim())) {
			enc++;
			break;
		    }
		}

		if (enc <= 0) {
		    passed = false;
		    break;
		}

		if (!right.contains("+")) {
		    int c = 0;
		    for (String endo : endoList) {
			if (endo.equals(right.trim())) {
			    c++;
			    break;
			}
		    }
		    if (c <= 0)
			for (String exo : exoVarList) {
			    if (exo.equals(right.trim())) {
				c++;
				break;
			    }
			}

		    if (c <= 0) {
			passed = false;
			break;
		    }
		} else {
		    String[] vars = right.split("\\+");
		    for (String var : vars) {
			int c = 0;
			for (String endo : endoList) {
			    if (endo.equals(var.trim())) {
				c++;
				break;
			    }
			}
			if (c <= 0)
			    for (String exo : exoVarList) {
				if (exo.equals(var.trim())) {
				    c++;
				    break;
				}
			    }

			if (c <= 0) {
			    passed = false;
			    break;
			}
		    }
		    if (!passed)
			break;
		}
	    }
	    Pattern pp = Pattern.compile("\\s+");
	    m = pp.matcher(equation);
	    equationList.set(i, m.replaceAll(""));

	}

	if (!passed)
	    throw new RMException(" '" + eq + "' is not a correct equation.");

    }

    private static void checkUserImputedVars(List<String> inputVarList, List<String> defDepend, List<String> defIndepend)
	    throws Exception {
	List<String> tmp = new ArrayList<String>();
	tmp.addAll(defDepend);
	tmp.addAll(defIndepend);
	for (int i = 0; i < inputVarList.size(); i++) {
	    String var = inputVarList.get(i);
	    if (!tmp.contains(var)) {
		RMLogger.warn("Numeric variables are " + tmp);
		throw new RMException("'" + var + "' is not a correct variable.");
	    }
	}
    }

    public static void cleanCurrentOutputWindow() {
	if (CURRENT_OUTPUT_GRAGH_WINDOW != null) {
	    CURRENT_OUTPUT_GRAGH_WINDOW.dispose();
	    CURRENT_OUTPUT_GRAGH_WINDOW = null;
	}
	if (CURRENT_OUTPUT_TEXT_WINDOW != null) {
	    CURRENT_OUTPUT_TEXT_WINDOW.dispose();
	    CURRENT_OUTPUT_TEXT_WINDOW = null;
	}

    }

    /**
     * 
     * @return
     */
    public static boolean displayCurrentOutputWindow() {
	boolean ok = false;
	if (CURRENT_OUTPUT_GRAGH_WINDOW != null) {
	    CURRENT_OUTPUT_GRAGH_WINDOW.setVisible(true);
	    ok = true;
	}
	if (CURRENT_OUTPUT_TEXT_WINDOW != null) {
	    CURRENT_OUTPUT_TEXT_WINDOW.setVisible(true);
	    ok = true;
	}
	return ok;
    }

}
