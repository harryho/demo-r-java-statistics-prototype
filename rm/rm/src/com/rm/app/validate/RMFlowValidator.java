package com.rm.app.validate;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

import com.rm.app.RMAppContext;
import com.rm.app.graph.RMGraph;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.graph.RMGraphEdge;
import com.rm.app.r.component.equation.REQComponent;
import com.rm.app.r.component.evaluation.REVComponent;
import com.rm.app.r.component.explanation.REXComponent;

public class RMFlowValidator implements IRMFlowValidRuleSet  {

//    static IRMFlowRuleService defaultRule = null;
//
//    static Class extraRule = null;

    static Map componentMap = null;

    // static final Comparator RULE_ORDER = new Comparator() {
    // public int compare(Object m1, Object m2) {
    // return ((Method) m1).getName().compareTo(((Method) m2).getName());
    // }
    // };

    public static boolean isValidResearchFlow(RMGraph graph) {
	boolean isValid = false;

	CellView[] cv = graph.getGraphLayoutCache().getCellViews();
	Set elements = new HashSet();
	List edgeList = new ArrayList();
	componentMap = new HashMap();

	for (int i = 0; i < cv.length; i++) {
	    Object object = cv[i];
	    if (object instanceof EdgeView) {
		EdgeView edgeView = (EdgeView) object;
		RMGraphEdge edge = (RMGraphEdge) edgeView.getCell();
		elements.add(edge);
		edgeList.add(edge);
	    } else if (object instanceof VertexView) {
		Object obj = ((VertexView) object).getCell();
		RMGraphCell cell = (RMGraphCell) obj;
		componentMap.put(cell.getId(), cell);
		elements.add(cell);
	    }
	}

	if (!elements.isEmpty()) {
	    isValid = validateByRuleSet( elements);
	} else {
	    RMFlowValidationMessage.setErrorMessage(RMFlowValidationMessage.KEY + RMFlowValidationMessage.INCOMPLETE,
		    null, RMFlowValidationMessage.TYPE_INCOMPLETE_FLOW);
	}
	System.out.println(" =========================" + isValid);
	if (isValid && componentMap.size() > 0) {
	    try {
		initialRunningFlow(edgeList);
	    } catch (Exception e) {
		e.printStackTrace();
		if (RMFlowValidationMessage.getErrorMessage() == null)
		    RMFlowValidationMessage
			    .setErrorMessage(
				    RMFlowValidationMessage.KEY + RMFlowValidationMessage.UNKNOWN,
				    new Object[] { "There is some errors in the reserch flow. \n Please check it carefully again." },
				    RMFlowValidationMessage.TYPE_UNKNOW_ERROR);
		isValid = false;
	    }
	}
	return isValid;
    }

    private static boolean validateByRuleSet( Object object) {
	boolean isValid = true;

	Iterator iterator = componentMap.keySet().iterator();
	String[] unitype = RMFLOW_UNIQUE_RCOMPT_TYPE;
	int[] counts = new int[unitype.length];

	String duplicateType = "";

	boolean specialHandle = false;

	while (iterator.hasNext()) {
	    String key = (String) iterator.next();
	    Object obj = componentMap.get(key);
	    RMGraphCell cell = (RMGraphCell) obj;

	    for (int i = 0; i < unitype.length; i++) {
		if (unitype[i].equals(cell.getClassifierKey())) {
		    counts[i] = counts[i] + 1;
		}

		if (counts[i] > 1) {
		    isValid = false;
		    duplicateType = unitype[i];
		    break;
		}
	    }
	    
	    if(cell.getKey().equals(SPECIAL_CMPT))
		specialHandle=true;

	}

	if (isValid) {
//	    System.out.println("    specialHandle ========================================" + specialHandle);
	    if (!specialHandle) {
		
		IRMFlowRuleService flowRuleService = RMFlowRuleManager.createRMFlowValidateService(RM_BASIC_RULE_SERVICE);
		isValid = flowRuleService.exam(object);
		
	    } else {
//		 System.out.println("    specialHandle ========================================" + RM_GLS_RULE_SERVICE);
		IRMFlowRuleService flowRuleService = RMFlowRuleManager.createRMFlowValidateService(RM_GLS_RULE_SERVICE);
		isValid = flowRuleService.exam(object);
		
	    }
	} else {
	    RMFlowValidationMessage.setErrorMessage(RMFlowValidationMessage.KEY
		    + RMFlowValidationMessage.INCORRECT_DUPLICATE, new Object[] { duplicateType.toUpperCase() },
		    RMFlowValidationMessage.TYPE_INCORRECT_LOGIC);
	}

	return isValid;
    }

    private static void initialRunningFlow(List edgeList) throws Exception {
	RMAppContext.activeRMFlows = new ArrayList();
	String[] flowStruct = RMFLOW_STRUCT;

	List list = new ArrayList();
	String nextId = "0";
	String eq = "";
	for (int i = 0; i < flowStruct.length; i++) {
	    String sourceType = flowStruct[i];
	    System.out.println("    sourceType  " + sourceType + " i ========================================" + i);
	    for (int k = 0; k < edgeList.size(); k++) {
		Object object = edgeList.get(k);
		if (object instanceof RMGraphEdge) {
		    RMGraphEdge edge = (RMGraphEdge) object;
		    if ((i == 0 && sourceType.equals(edge.getSourceType()))
			    || (nextId.equals(edge.getSourceId()) && sourceType.equals(edge.getSourceType()))) {
			System.out.println("   ADD   ");
			RMGraphCell cell = (RMGraphCell) componentMap.get(edge.getSourceId());
			list.add(cell);
			nextId = edge.getTargetId();
			System.out.println("    NNNNNNNNNNNNNNNNNNNNNNNNNNNNnext ID  " + nextId);
			if (sourceType.equals(REQComponent.TYPE_EQUATION))
			    eq = cell.getKey();

			if (edge.getSourceType().equals(REVComponent.TYPE_EVALUATION)
				&& edge.getTargetType().equals(REVComponent.TYPE_EVALUATION)) {
			    k = -1;
			} else {
			    break;
			}
		    } else if (nextId.equals(edge.getTargetId()) && sourceType.equals(edge.getTargetType())
			    && i == flowStruct.length - 1) {
			System.out.println("   LAST   ");
			RMGraphCell cell = (RMGraphCell) componentMap.get(edge.getTargetId());
			if (eq.equals(REQComponent.TYPE_EQUATION_SEM)) {
			    if (cell.getKey().equals(REXComponent.TYPE_EXPLANATION_CUS))
				cell.setKey(REXComponent.TYPE_EXPLANATION_CUS4SEM);
			    else if (cell.getKey().equals(REXComponent.TYPE_EXPLANATION_STD))
				cell.setKey(REXComponent.TYPE_EXPLANATION_STD4SEM);
			    else if (cell.getKey().equals(REXComponent.TYPE_EXPLANATION_COM))
				cell.setKey(REXComponent.TYPE_EXPLANATION_COM4SEM);
			}
			list.add(cell);
		    }
		}
	    }
	}
	RMAppContext.activeRMFlows.add(list);
    }

}
