package com.rm.app.ui.tool;

import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import com.rm.app.RMResources;

public class RMFlowTreeCellRender extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = -587368191046343636L;

	private static final Icon closeFolder = RMResources.getImage("/com/rm/images/closefolder.gif");

    private static final Icon openFolder = RMResources.getImage("/com/rm/images/openfolder.gif");

    private static final Icon fileIcon = RMResources.getImage("/com/rm/images/flowfile.gif");

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
	    boolean leaf, int row, boolean hasFocus) {
	super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

	RMFlowTreeNode node = (RMFlowTreeNode) value;

	TreeNode[] nodes = node.getPath();

	if (node.isRoot()) {
	    setIcon(null);
	} else {
	    for (int i = 0; i < nodes.length; i++) {
		RMFlowTreeNode node2 = (RMFlowTreeNode) nodes[i];
		    File file = node2.getFile();

		    if (file != null) {
			
			if (file.isFile()) {
			    setIcon(fileIcon);
			} else if (file.isDirectory()) {
			    setClosedIcon(closeFolder);
			    setOpenIcon(openFolder);
			    setLeafIcon(closeFolder);
			}
		    }
	    }
	}

	return this;
    }

    
}
