package com.rm.app.r.component;

import com.rm.app.r.flow.RFlowProperties;

public interface RComponent {

	public final static int ERROR = -1;

	public final static int SUCEESS = 1;

	public final static int REPROCESS = 2;
	
	public final static int SKIP = 3;

	// const component types

	public static long HOLD_MS = 2000L;

	/**
	 * 
	 * @return the Type key of each component
	 */
	public String getKey();

	/**
	 * Validation for each component before running.
	 * 
	 * @return
	 */
	public boolean validation() throws Exception;

	/**
	 * 
	 * @return commands which can execute in R system
	 */
	public String getRCommand(RFlowProperties properties);

	/**
	 * 
	 * @return If >0, this command need to sleep to wait callback
	 */
	public long holdedMS();

	/**
	 * 
	 * @param condition
	 * @return
	 */
	public int handleCallBack(StringBuffer sb, RFlowProperties properties);

}
