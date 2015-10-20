package com.rm.app.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.rm.app.ui.action.StatusBarGraphListener;

public class RMAppStatusBar extends StatusBarGraphListener implements MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	protected JLabel leftSideStatus;

	/**
	 * contains the scale for the current graph
	 */
	protected JLabel middleSideStatus;

	protected JLabel rightSideStatus;

	private static Object object = new RMAppStatusBar();
	/**
	 * Constructor for GPStatusBar.
	 * 
	 */
	private RMAppStatusBar() {
		super();
		// Add this as graph model change listener
		setLayout(new BorderLayout());
		leftSideStatus = new JLabel("RM4Es");
		middleSideStatus = new JLabel("0/0Mb");
		rightSideStatus = new JLabel("0:0");
		leftSideStatus.setBorder(BorderFactory.createLoweredBevelBorder());
		middleSideStatus.setBorder(BorderFactory.createLoweredBevelBorder());
		rightSideStatus.setBorder(BorderFactory.createLoweredBevelBorder());
		add(leftSideStatus,BorderLayout.WEST);
		add(middleSideStatus, BorderLayout.CENTER);
		add(rightSideStatus, BorderLayout.EAST);
	}
	
	public static Object getInstance(){
	    return object;
	}
	
	

	protected void updateStatusBar() {
		Runtime runtime = Runtime.getRuntime();
		int freeMemory = (int) (runtime.freeMemory() / 1024);
		int totalMemory = (int) (runtime.totalMemory() / 1024);
		int usedMemory = (totalMemory - freeMemory);
		String str = (usedMemory / 1024) + "/" + (totalMemory / 1024) + "Mb";
		middleSideStatus.setText(str);
		String strPost = e.getX() + " : " + e.getY();
		rightSideStatus.setText(strPost);

	}

	void eventOutput(String eventDescription, MouseEvent e) {
		// textArea.append(eventDescription
		// + " (" + e.getX() + "," + e.getY() + ")"
		// + " detected on "
		// + e.getComponent().getClass().getName()
		// + NEWLINE);
		// textArea.setCaretPosition(textArea.getDocument().getLength());

	}

	MouseEvent e;

	public void mouseMoved(MouseEvent e) {
		this.e = e;
		updateStatusBar();
	}

	public void mouseDragged(MouseEvent e) {
		this.e = e;
		updateStatusBar();
	}

	public JLabel getMiddleSideStatus() {
		return middleSideStatus;
	}

	public void setMiddleSideStatus(JLabel middleSideStatus) {
		this.middleSideStatus = middleSideStatus;
	}

	/**
	 * @return Returns the leftSideStatus.
	 */
	public JLabel getLeftSideStatus() {
		return leftSideStatus;
	}

	/**
	 * @param leftSideStatus
	 *            The leftSideStatus to set.
	 */
	public void setLeftSideStatus(JLabel leftSideStatus) {
		this.leftSideStatus = leftSideStatus;
	}

	/**
	 * @return Returns the middleSideStatus.
	 */
	public JLabel getRightSideStatus() {
		return rightSideStatus;
	}

	/**
	 * @param middleSideStatus
	 *            The middleSideStatus to set.
	 */
	public void setRightSideStatus(JLabel rightSideStatus	) {
		this.rightSideStatus = rightSideStatus;
	}

	public MouseEvent getE() {
		return e;
	}

	public void setE(MouseEvent e) {
		this.e = e;
	}
}
