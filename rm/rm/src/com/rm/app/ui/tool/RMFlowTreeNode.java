package com.rm.app.ui.tool;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

public class RMFlowTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 4894022692191651545L;

	public RMFlowTreeNode(Object node) {
		super(node);
	}

	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}
