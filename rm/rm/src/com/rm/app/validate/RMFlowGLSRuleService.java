package com.rm.app.validate;

import java.util.Iterator;
import java.util.Set;

import org.jgraph.graph.DefaultPort;

import com.rm.app.graph.RMGraphCell;
import com.rm.app.graph.RMGraphEdge;

public class RMFlowGLSRuleService extends RMFlowBaseRuleService {
    
    public boolean exam(Object object) {
	boolean isValid = false;
	if (object instanceof Set) {
	    Iterator iterator = ((Set) object).iterator();
	    while (iterator.hasNext()) {

		Object obj = iterator.next();
		isValid = super.rule01_IsCompleteEdge(obj);
		if (isValid) {
		    isValid = this.rule02_IsCorrectRelation(obj);
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
    
    private boolean rule02_IsCorrectRelation(Object object) {
//	System.out.println( " =========================== " + RMFlowGLSRuleService.class + "  method rule02_IsCorrectRelation ");
	boolean isValid = false;
	if (object != null && object instanceof RMGraphEdge) {
	    RMGraphEdge edge = (RMGraphEdge) object;
	    RMGraphCell sourceCell = (RMGraphCell) ((DefaultPort) edge.getSource()).getParent();
	    RMGraphCell targetCell = (RMGraphCell) ((DefaultPort) edge.getTarget()).getParent();

	    String[][] corrRelations = RMFLOW_GLS_CORR_RELATIONSHIP;

	    for (String[] relatonship : corrRelations) {
//		System.out.println(" real  "+ sourceCell.getClassifierKey()+ "   " + targetCell.getClassifierKey());
//		System.out.println(" rule "+ relatonship[0] + "   " + relatonship[1]);
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
