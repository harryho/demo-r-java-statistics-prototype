package com.rm.app.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Map;

import javax.swing.JDialog;

import com.rm.app.graph.Attribute;
import com.rm.app.r.component.RComponent;
import com.rm.app.r.component.evaluation.REVComponent;
import com.rm.app.r.component.explanation.REXComponent;
import com.rm.app.r.flow.RFlowProperties;
import com.rm.app.ui.tool.RMEvCookAdjDialogBuilder;
import com.rm.app.ui.tool.RMEvDfftisAdjDialogBuilder;
import com.rm.app.ui.tool.RMEvHatAdjDialogBuilder;
import com.rm.app.ui.tool.RMEvRstudAdjDialogBuilder;
import com.rm.app.ui.tool.RMExCusDialogBuilder;
import com.rm.app.ui.tool.RMFlowAdjustDialogBuilder;

public class RMFlowRunningDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    Map<String, Attribute> dataMap = null;

    String actionComand = null;

    private static String[] compsToBeExam = { "equation.mr", "equation.glm" };

    public String getActionComand() {
	return actionComand;
    }

    public void setActionComand(String actionComand) {
	this.actionComand = actionComand;
    }

    public RMFlowRunningDialog(RComponent comp, RFlowProperties properties) throws Exception {

	RMFlowAdjustDialogBuilder builder = null;

	if (comp.getKey().equals(REVComponent.TYPE_EVALUATION_RSTUD)) {
	    builder = new RMEvRstudAdjDialogBuilder(properties);
	} else if (comp.getKey().equals(REVComponent.TYPE_EVALUATION_COOKS)) {
	    builder = new RMEvCookAdjDialogBuilder(properties);

	} else if (comp.getKey().equals(REVComponent.TYPE_EVALUATION_DFFITS)) {
	    builder = new RMEvDfftisAdjDialogBuilder(properties);

	} else if (comp.getKey().equals(REVComponent.TYPE_EVALUATION_HAT)) {
	    builder = new RMEvHatAdjDialogBuilder(properties);

	} else if (comp.getKey().equals(REXComponent.TYPE_EXPLANATION_CUS)) {
	    builder = new RMExCusDialogBuilder(properties);
	} else if (comp.getKey().equals(REXComponent.TYPE_EXPLANATION_CUS4SEM)) {
	    builder = new RMExCusDialogBuilder(properties);
	}
	builder.buildDialogLayout(comp, this);
	Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	int x = (int) ((dimension.getWidth() - getWidth()) / 2 - 200);
	int y = (int) ((dimension.getHeight() - getHeight()) / 2 - 200);


	setLocation(x, y);
	setResizable(false);
	pack();
	setVisible(true);
    }

}
