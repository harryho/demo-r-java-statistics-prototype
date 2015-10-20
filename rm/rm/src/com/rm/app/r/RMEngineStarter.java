package com.rm.app.r;

import com.rm.app.log.SystemLogger;
import com.rm.app.util.SwingWorker;

public class RMEngineStarter extends SwingWorker {

	
	public Object construct() {
		SystemLogger.fileLogger.info("Try to start RMEngine.");
		new RMEngine();
		SystemLogger.fileLogger.info("Finish to start RMEngine.");
		return null;
	}

	public RMEngineStarter() {
		
	}
}
