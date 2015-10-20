package com.rm.app.r;

import java.util.List;

import com.rm.app.r.flow.RFlow;
import com.rm.app.util.SwingWorker;

public class RMWorker extends SwingWorker {

	/**
	 * Called on the event dispatching thread (not on the worker thread) after
	 * the <code>construct</code> method has returned.
	 */
	public void finished() {
	}

	List<RFlow> rfolws;

	/**
	 * Start a thread that will call the <code>construct</code> method and then
	 * exit.
	 */
	public RMWorker(List<RFlow> rfolws) {
		super();
		this.rfolws = rfolws;

	}

	
	public Object construct() {
		return null;
	}
}
