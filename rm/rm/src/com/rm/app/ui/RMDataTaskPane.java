package com.rm.app.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.l2fprod.common.demo.TaskPaneMain;
import com.l2fprod.common.swing.JTaskPane;
import com.l2fprod.common.swing.JTaskPaneGroup;
import com.l2fprod.common.swing.plaf.LookAndFeelAddons;
import com.l2fprod.common.swing.plaf.windows.WindowsLookAndFeelAddons;
import com.rm.app.RMAppConst;
import com.rm.app.RMAppContext;
import com.rm.app.RMResources;

public class RMDataTaskPane extends JTaskPane {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RMDataTaskPane() {
	UIManager.put("win.xpstyle.name", "metallic");
 
	try {
	    LookAndFeelAddons.setAddon(WindowsLookAndFeelAddons.class);
	} catch (InstantiationException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	JTaskPane taskPane = new JTaskPane();

	// "System" GROUP
	JTaskPaneGroup systemGroup = new JTaskPaneGroup();
	systemGroup.setTitle(RMResources.getString("taskPane.analysis.dataComponent.label"));
	systemGroup.setToolTipText(RMResources.getString("taskPane.analysis.dataComponent.tooltip"));
	systemGroup.setSpecial(true);
	systemGroup.setIcon(new ImageIcon(TaskPaneMain.class.getResource("icons/tasks-email.png")));

	JTree tree = (JTree) RMAppContext.get(RMAppConst.RM_DATA_TREE);
	tree.setRootVisible(false);
	JScrollPane sTree = new JScrollPane(tree);
	sTree.setBorder(new EmptyBorder(0, 0, 0, 0));
	systemGroup.add(sTree);
	taskPane.add(systemGroup);

	JScrollPane scroll = new JScrollPane(taskPane);
	scroll.setBorder(null);

	setLayout(new BorderLayout());
	add("Center", scroll);

	setBorder(null);
    }

    Action makeAction(String title, String tooltiptext, String iconPath) {
	Action action = new AbstractAction(title) {
	    public void actionPerformed(ActionEvent e) {
	    }
	};
	action.putValue(Action.SMALL_ICON, new ImageIcon(TaskPaneMain.class.getResource(iconPath)));
	action.putValue(Action.SHORT_DESCRIPTION, tooltiptext);
	return action;
    }


}
