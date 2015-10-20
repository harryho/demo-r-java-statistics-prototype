package com.rm.app.ui.action;

import javax.swing.JPanel;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;

public class StatusBarGraphListener extends JPanel implements GraphModelListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Graph Model change event
	 */
	public void graphChanged(GraphModelEvent e) {
		updateStatusBar();
	}

	protected void updateStatusBar() {

	}

}
