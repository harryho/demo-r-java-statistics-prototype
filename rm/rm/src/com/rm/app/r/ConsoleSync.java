package com.rm.app.r;

import java.util.Vector;



public class ConsoleSync {
	Vector<String> msgs;

	public ConsoleSync() {
		msgs = new Vector<String>();
	}

	private boolean notificationArrived = false;

	/**
	 * this internal method waits until {@link #triggerNotification} is called
	 * by another thread. It is implemented by using {@link wait()} and checking
	 * {@link notificationArrived}.
	 */
	public synchronized String waitForNotification() {
		while (!notificationArrived)
			try {
				wait(100);
				if (RMEngine.R != null)
					RMEngine.R.rniIdle();
			} catch (InterruptedException e) {
			}
		String s = null;
		if (msgs.size() > 0) {
			s = (String) msgs.elementAt(0);
			msgs.removeElementAt(0);
		}
		if (msgs.size() == 0)
			notificationArrived = false;
		return s;
	}

	/**
	 * this methods awakens {@link #waitForNotification}. It is implemented by
	 * setting {@link #notificationArrived} to <code>true</code>, setting
	 * {@link #lastNotificationMessage} to the passed message and finally
	 * calling {@link notifyAll()}.
	 */
	public synchronized void triggerNotification(String msg) {
		notificationArrived = true;
		msgs.addElement(msg);
		notifyAll();
	}
}
