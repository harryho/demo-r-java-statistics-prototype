package com.rm.app.validate;

import java.util.Iterator;
import java.util.Set;

import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Port;

import com.rm.app.graph.RMGraphCell;
import com.rm.app.graph.RMGraphEdge;
import com.rm.app.r.component.data.RDataComponent;
import com.rm.app.r.component.explanation.REXComponent;

public class RMFlowBaseRuleService implements IRMFlowRuleService, IRMFlowValidRuleSet {

    public boolean exam(Object object) {
	boolean isValid = false;
	if (object instanceof Set) {
	    Iterator iterator = ((Set) object).iterator();
	    while (iterator.hasNext()) {

		Object obj = iterator.next();
		isValid = rule01_IsCompleteEdge(obj);
		if (isValid) {
		    isValid = rule02_IsCorrectRelation(obj);
		    if (!isValid) {
			break;
		    }
		} else {
		    break;
		}

	    }
	}
	return isValid;
    }

    public boolean rule01_IsCompleteEdge(Object object) {
	boolean isValid = false;

	if (object != null && object instanceof RMGraphEdge) {

	    RMGraphEdge edge = (RMGraphEdge) object;
	    try {

		Object sourceObject = edge.getSource();
		Object targetObject = edge.getTarget();

		if (sourceObject == null || targetObject == null) {
		    RMFlowValidationMessage.setErrorMessage(RMFlowValidationMessage.KEY
			    + RMFlowValidationMessage.INVALID_RELATIONHSIP, null,
			    RMFlowValidationMessage.TYPE_INVALID_RELATIONSHIP);

		} else {
		    isValid = true;
		}
	    } catch (Exception e) {
		RMFlowValidationMessage.setErrorMessage(RMFlowValidationMessage.KEY + RMFlowValidationMessage.INVALID,
			new Object[] { e.toString() }, RMFlowValidationMessage.TYPE_INVALID_FLOW);
	    }
	} else if (object != null && object instanceof RMGraphCell) {
	    RMGraphCell cell = (RMGraphCell) object;
	    try {
		Port port = (Port) cell.getChildren().get(0);
		Iterator iterator = port.edges();
		if (RDataComponent.TYPE_DATA_MODEL.equals(cell.getClassifierKey())
			|| REXComponent.TYPE_EXPLANATION.equals(cell.getClassifierKey())) {
		    int count = 0;
		    while (iterator.hasNext()) {
			iterator.next();
			count++;
		    }

		    if (count == 1)
			isValid = true;
		    else if (count < 1)
			RMFlowValidationMessage.setErrorMessage(RMFlowValidationMessage.KEY
				+ RMFlowValidationMessage.INVALID_COMPONENT, new Object[] { cell.getUserObject() },
				RMFlowValidationMessage.TYPE_INVALID_COMPONENT);
		    else if (count > 1)
			RMFlowValidationMessage.setErrorMessage(RMFlowValidationMessage.KEY
				+ RMFlowValidationMessage.INVALID_STARTEND, new Object[] { cell.getUserObject() },
				RMFlowValidationMessage.TYPE_INVALID_COMPONENT);
		} else {
		    int in = 0;
		    int out = 0;

		    while (iterator.hasNext()) {
			Object o = iterator.next();
			RMGraphEdge edge = (RMGraphEdge) o;
			if (cell.getId().equals(edge.getSourceId()))
			    out++;
			if (cell.getId().equals(edge.getTargetId()))
			    in++;
		    }
		    if (in >= 1 && out >= 1)
			isValid = true;
		    else
			RMFlowValidationMessage.setErrorMessage(RMFlowValidationMessage.KEY
				+ RMFlowValidationMessage.INVALID_COMPONENT, new Object[] { cell.getName() },
				RMFlowValidationMessage.TYPE_INVALID_COMPONENT);
		}
	    } catch (Exception e) {

		RMFlowValidationMessage.setErrorMessage(RMFlowValidationMessage.KEY + RMFlowValidationMessage.INVALID,
			new Object[] { e.toString() }, RMFlowValidationMessage.TYPE_INVALID_FLOW);
	    }
	}
	return isValid;
    }

    private boolean rule02_IsCorrectRelation(Object object) {
//	System.out.println(this.getClass() + "        rule02_IsCorrectRelation     ");
	boolean isValid = false;
	if (object != null && object instanceof RMGraphEdge) {
	    RMGraphEdge edge = (RMGraphEdge) object;
	    RMGraphCell sourceCell = (RMGraphCell) ((DefaultPort) edge.getSource()).getParent();
	    RMGraphCell targetCell = (RMGraphCell) ((DefaultPort) edge.getTarget()).getParent();

	    String[][] corrRelations = RMFLOW_CORR_RELATIONSHIP;

	    for (String[] relatonship : corrRelations) {
//		System.out.println(" real  " + sourceCell.getClassifierKey() + "   " + targetCell.getClassifierKey());
//		System.out.println(" rule " + relatonship[0] + "   " + relatonship[1]);
		if (relatonship[0].equals(sourceCell.getClassifierKey())
			&& relatonship[1].equals(targetCell.getClassifierKey())) {
		    isValid = true;
		    break;
		}
	    }
	    if (!isValid) {
		RMFlowValidationMessage.setErrorMessage(RMFlowValidationMessage.KEY
			+ RMFlowValidationMessage.INCORRECT_RELATOINSHIP, new Object[] { sourceCell.getName(),
			targetCell.getName() }, RMFlowValidationMessage.TYPE_INCORRECT_RELATIONSHIP);
	    }

	} else if (object != null && object instanceof RMGraphCell)
	    isValid = true;

	return isValid;
    }

}
