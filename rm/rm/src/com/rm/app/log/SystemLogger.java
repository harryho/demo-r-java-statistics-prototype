package com.rm.app.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.rm.app.RMAppContext;

public class SystemLogger {

	public static Logger fileLogger;
	static {
		PropertyConfigurator.configure(RMAppContext.applicationPath
				+ "/log4j.properties");
		fileLogger = Logger.getLogger(SystemLogger.class);
		
	}
}
