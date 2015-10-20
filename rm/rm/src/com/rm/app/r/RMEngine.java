package com.rm.app.r;

import java.io.File;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

import com.rm.app.RMAppContext;
import com.rm.app.log.RMLogger;
import com.rm.app.log.SystemLogger;

/**
 * 
 * 
 */
public class RMEngine {

	public static Vector<String> RHISTORY = new Vector<String>();

	public static Rengine R = null;

	public static ConsoleSync rSync = new ConsoleSync();

	/** arguments for Rengine */
	private static String[] rargs = { "--save" };

	public static RMConsole MAINRCONSOLE = null;

	public RMEngine() {
		init();
	}

	private void init() {
		if (!Rengine.versionCheck()) {
			RMLogger
					.error("Version mismatch - Java files don't match library version.");
			SystemLogger.fileLogger
					.error("Version mismatch - Java files don't match library version.");
		}
		MAINRCONSOLE = new RMConsole();
		try {
			R = new Rengine(rargs, true, MAINRCONSOLE);
		} catch (Exception e) {
			SystemLogger.fileLogger.error("e=" + e.getLocalizedMessage());
		}
		RMLogger.info("R version is " + RMAppContext.R_VERSION);
		initialREnv();
		// new Refresher().run();
	}

	/**
	 * Load all necessary function when start
	 */
	private void initialREnv() {
		execute("setwd('" + RMAppContext.applicationPath + "')", false);
		execute("Sys.setenv(LANGUAGE='en')", false);

		// use as library()
		String loadLibFunc = "load<-function(lib){vc <- c(.libPaths(),'$VISTA_LIB_PATH'); for ( k in 1 : length(vc)) { pgk <-  paste(vc[k], '/', sep = ''); pgk <-  paste(pgk, lib, sep = '');  if( file.exists(pgk)){ t<-try(library(lib, pos=2, lib.loc=vc[k], character.only=TRUE)); break;} } }";
		String userHome = System.getProperty("user.home")+"/My Documents/R/win-library/2.8";
		userHome = userHome.replaceAll("\\\\", "\\/");
		File file = new File(userHome);
		if(file.isDirectory()){
			loadLibFunc = loadLibFunc.replace("$VISTA_LIB_PATH", userHome);
		}else {
			loadLibFunc = loadLibFunc.replace("$VISTA_LIB_PATH", "");
		}
		execute(loadLibFunc, false);
	}

	/**
	 * 
	 * @param cmd
	 * @param seconds
	 * @param addToHist
	 * @return
	 */
	public static String executeAndWaitReturn(String cmd, long seconds,
			boolean addToHist) {

		MAINRCONSOLE.cleanCallBackMessage();
		int i = 0;
		if (null == cmd || "".equals(cmd))
			return "";
		execute(cmd, addToHist);
		while (i < 5) {
			try {
				RMLogger.debug("sleeping ...");
				Thread.sleep(seconds);
				if (MAINRCONSOLE.callBackMessage.length() > 0) {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
		}
		return MAINRCONSOLE.callBackMessage.toString();
	}

	/**
	 * Execute a command and add it into history.
	 * 
	 * @param cmd
	 *            command for execution
	 * @param addToHist
	 *            indicates wether the command should be added to history or not
	 */
	public static void execute(String cmd, boolean addToHist) {
		RMLogger.debug("RMEngine.execute:" + cmd);
		MAINRCONSOLE.cleanCallBackMessage();
		if (cmd == null || "".equals(cmd)) {
			return;
		}
		if (addToHist && RMEngine.RHISTORY.size() == 0)
			RMEngine.RHISTORY.add(cmd);
		else if (addToHist && cmd.trim().length() > 0
				&& RMEngine.RHISTORY.size() > 0
				&& !RMEngine.RHISTORY.lastElement().equals(cmd.trim()))
			RMEngine.RHISTORY.add(cmd);

		String[] cmdArray = cmd.split("\n");
		String c = null;
		for (int i = 0; i < cmdArray.length; i++) {
			c = cmdArray[i];
			RMEngine.rSync.triggerNotification(c.trim());
		}
	}

	public static String exit() {
		int exit = JOptionPane.showConfirmDialog(null, "Save workspace?",
				"Close RM4Es Demo System", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);

		if (exit == 0) {
			return "y\n";
		} else if (exit == 1) {
			return "n\n";
		} else
			return "c\n";
	}

	/**
	 * Refresher, which is looking for new keywords and objects in workspace and
	 * refreshes highlight and autocompletion information.
	 */
	class Refresher implements Runnable {

		public Refresher() {
		}

		public void run() {
			while (true)
				try {
					Thread.sleep(60000);
					REXP x = R.idleEval("try(.refreshKeyWords(),silent=TRUE)");
					String[] r = null;
					if (x != null && (r = x.asStringArray()) != null)
						setKeyWords(r);
					x = R.idleEval("try(.refreshObjects(),silent=TRUE)");
					r = null;
					if (x != null && (r = x.asStringArray()) != null)
						setObjects(r);
				} catch (Exception e) {
					RMLogger.debug(e.getMessage());
					e.printStackTrace();
				}
		}

		private void setKeyWords(String[] r) {
		}

		private void setObjects(String[] r) {

		}
	}

}
