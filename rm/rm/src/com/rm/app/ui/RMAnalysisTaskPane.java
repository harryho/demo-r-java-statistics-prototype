package com.rm.app.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeSelectionModel;

import com.l2fprod.common.demo.TaskPaneMain;
import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import com.l2fprod.common.swing.LookAndFeelTweaks;
import com.l2fprod.common.swing.plaf.LookAndFeelAddons;
import com.l2fprod.common.swing.plaf.windows.WindowsLookAndFeelAddons;
import com.rm.app.RMAppConst;
import com.rm.app.RMAppContext;
import com.rm.app.RMResources;
import com.rm.app.ui.action.ActionHelper;
import com.rm.app.ui.tool.RMFlowTreeCellRender;

public class RMAnalysisTaskPane extends JTaskPane {

    private static final long serialVersionUID = 1L;

    public RMAnalysisTaskPane() {
	UIManager.put("win.xpstyle.name", "metallic");

	try {
	    LookAndFeelAddons.setAddon(WindowsLookAndFeelAddons.class);
	} catch (InstantiationException e) {
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	}

	JTaskPane taskPane = new JTaskPane();

	taskPane.setBackground(new Color(2, 2, 2));

	// "System" GROUP
	JTaskPaneGroup systemGroup = new JTaskPaneGroup();
	systemGroup.setTitle(RMResources.getString("taskPane.analysis.rmComponent.label"));
	systemGroup.setToolTipText(RMResources.getString("taskPane.analysis.rmComponent.tooltip"));
	systemGroup.setSpecial(true);
	systemGroup.setIcon(new ImageIcon(TaskPaneMain.class.getResource("icons/tasks-email.png")));

	// RM Model Tree
	JTree model_tree = (JTree) RMAppContext.get(RMAppConst.RM_MODEL_TREE);
	model_tree.setRootVisible(false);
	JScrollPane sTree = new JScrollPane(model_tree);
	sTree.setBorder(new EmptyBorder(0, 0, 0, 0));
	sTree.setPreferredSize(new Dimension(220, 250));
	systemGroup.add(sTree);

	taskPane.add(systemGroup);

	// RM Research Flows
	JTree rmFlowTree = (JTree) RMAppContext.get(RMAppConst.RM_FLOW_TREE);
	rmFlowTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);


	JTaskPaneGroup officeGroup = new JTaskPaneGroup();
	officeGroup.setTitle(RMResources.getString("taskPane.analysis.researchFlow.label"));
	// JScrollPane fTree = new JScrollPane(myFlowTree);
	// officeGroup.add(new MyFlowAction());
	// officeGroup.add(new CompanyFlowAction());
	// JScrollPane scrollpane = new JScrollPane();
	// scrollpane.getViewport().add(rmFlowTree);

	// rmFlowTree.setRootVisible(false);

	rmFlowTree.setCellRenderer(new RMFlowTreeCellRender());

	JScrollPane jsTree = new JScrollPane(rmFlowTree);
	jsTree.setPreferredSize(new Dimension(220, 110));
	// scrollpane.getViewport().add(companyFlowTree);
	officeGroup.add(jsTree);

	officeGroup.setExpanded(true);

	// officeGroup.add(fTree);
	officeGroup.setScrollOnExpand(false);
	taskPane.add(officeGroup);

	// RM Research Maps
	JTaskPaneGroup detailsGroup = new JTaskPaneGroup();
	detailsGroup.setTitle(RMResources.getString("taskPane.analysis.researchMaps.label"));
	detailsGroup.setScrollOnExpand(true);

	JEditorPane detailsText = new JEditorPane("text/html", "<html>");
	LookAndFeelTweaks.makeMultilineLabel(detailsText);
	LookAndFeelTweaks.htmlize(detailsText);
	detailsText.setText(RMResources.getString("taskPane.analysis.researchMaps.label"));
	detailsGroup.add(detailsText);

	taskPane.add(detailsGroup);

	JScrollPane scroll = new JScrollPane(taskPane);
	scroll.setBorder(null);

	setLayout(new BorderLayout());
	add("Center", scroll);

	setBorder(null);
    }

    private class MyFlowAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public MyFlowAction() {
	    super(RMResources.getString("taskPane.analysis.researchFlow.myflow.label"));
	    this.putValue(Action.SMALL_ICON, new ImageIcon(TaskPaneMain.class.getResource("icons/tasks-writedoc.png")));
	    this.putValue(Action.SHORT_DESCRIPTION, "");
	}

	public void actionPerformed(ActionEvent e) {
	    ActionHelper.doFileOpenActionWithChooser(RMAppContext.rmMyFlowPath);
	}

    }

    private class CompanyFlowAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	public CompanyFlowAction() {
	    super(RMResources.getString("taskPane.analysis.researchFlow.companyflow.label"));
	    this.putValue(Action.SMALL_ICON, new ImageIcon(TaskPaneMain.class.getResource("icons/tasks-writedoc.png")));
	    this.putValue(Action.SHORT_DESCRIPTION, "");
	}

	public void actionPerformed(ActionEvent e) {
	    ActionHelper.doFileOpenActionWithChooser(RMAppContext.rmCompanyFlowPath);
	}

    }
}
