package com.rm.app.ui.tool;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeCellRenderer;

public class RMTreeCellRenderer extends DefaultTreeCellRenderer{
	
	private static final long serialVersionUID = 1L;

	public RMTreeCellRenderer(){	    
		URL componentUrl = getClass().getClassLoader().getResource("com/rm/images/component.jpg");
		ImageIcon componentIcon = new ImageIcon(componentUrl);
		setLeafIcon(componentIcon);
		componentUrl = getClass().getClassLoader().getResource("com/rm/images/nodeopen.jpg");
		componentIcon = new ImageIcon(componentUrl);
		setClosedIcon(componentIcon);
		componentUrl = getClass().getClassLoader().getResource("com/rm/images/nodeclose.jpg");
		componentIcon = new ImageIcon(componentUrl);
		setOpenIcon(componentIcon);
	}
	
}
