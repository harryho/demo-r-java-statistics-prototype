package com.rm.app.log;

/**
 * The logger of RM4es System.
 * 
 * 
 */
public class RMLogger {

	private static final boolean isDebug = true;

	/**
	 * 
	 * @param msg
	 */
	public static void info(String msg) {
		if (msg == null) {
			return;
		}
		LogConsole.getInstance().infor(msg);
		SystemLogger.fileLogger.info(msg);

	}

	/**
	 * 
	 * @param msg
	 */
	public static void warn(String msg) {
		LogConsole.getInstance().warn(msg);
		SystemLogger.fileLogger.warn(msg);
	}

	/**
	 * 
	 * @param msg
	 */
	public static void error(String msg) {
		LogConsole.getInstance().error(msg);
		SystemLogger.fileLogger.error(msg);
	}

	/**
	 * 
	 * @param msg
	 */
	public static void debug(String msg) {
		if (isDebug) {
			System.out.println("Debug:  " + msg);
		}
	}

	public static void debug(Object ob) {
		if (ob != null)
			debug(ob.toString());
		else
			debug("Null");
	}

	public static void track(String mes) {

	}

	public static void cleanConsole() {
		LogConsole.getInstance().cleanConsole();
	}

}
