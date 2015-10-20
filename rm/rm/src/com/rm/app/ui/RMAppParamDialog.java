package com.rm.app.ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.rm.app.graph.Attribute;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.data.RDataCache;
import com.rm.app.r.component.data.RDataComponent;
import com.rm.app.r.component.equation.REQComponent;
import com.rm.app.ui.tool.RMEqSEMParamDialogBuilder;
import com.rm.app.ui.tool.RMImportDataDialogBuilder;
import com.rm.app.ui.tool.RMParamDialogBuilder;

public class RMAppParamDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	Map<String, Attribute> dataMap = null;

	String actionComand = null;

	private static String[] compsToBeExam = { "equation.mr", "equation.glm" ,  "equation.sem" };

	public String getActionComand() {
		return actionComand;
	}

	public void setActionComand(String actionComand) {
		this.actionComand = actionComand;
	}

	public RMAppParamDialog(RMGraphCell cell) throws Exception {
		String key = cell.getKey();
		boolean compExam = false;
		for (int i = 0; i < compsToBeExam.length; i++) {
			if (compsToBeExam[i].equals(key)) {
				compExam = true;
			}
		}

		if (compExam) {
			RDataCache cache = RDataCache.getIntance();
			cache.initial(false);

			if (cache.getDataInitailizedStatus() == RDataCache.PROCESS_FAILED_DUPLICATE_DATACOM
					.intValue()) {
				JOptionPane
						.showMessageDialog(
								null,
								"There are more than one data component. \n Please confirm only one valid data component!");
				return;
			} else if (cache.getDataInitailizedStatus() == RDataCache.PROCESS_FAILED_LOADDATA
					.intValue()
					|| cache.getDataInitailizedStatus() == RDataCache.PROCESS_NOT_INIT
							.intValue()) {
				JOptionPane
						.showMessageDialog(
								null,
								"Please import or select valid data set or file \n before you edit current component!");
				return;

			}
		}

		RMLogger.debug("Key: " + key);
		RMLogger.debug("id: " + cell.getId());
		RMParamDialogBuilder builder = null;

		if (RDataComponent.TYPE_DATA_MODEL_IMP.equals(key)) {
			builder = new RMImportDataDialogBuilder();
		} else if (REQComponent.TYPE_EQUATION_SEM.equals(key)) {
			builder = new RMEqSEMParamDialogBuilder();
		} else {
			builder = new RMParamDialogBuilder();
		}
		builder.buildDialogLayout(cell, this);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) / 2 - 200);
		int y = (int) ((dimension.getHeight() - getHeight()) / 2 - 200);

		setLocation(x, y);
		setResizable(true);
//		setResizable(false);
		pack();
		setVisible(true);
	}

}
