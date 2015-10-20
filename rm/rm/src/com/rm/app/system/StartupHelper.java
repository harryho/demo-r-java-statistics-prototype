package com.rm.app.system;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.rm.app.RMAppConst;
import com.rm.app.RMAppContext;
import com.rm.app.log.SystemLogger;
import com.rm.app.r.RMEngineStarter;
import com.rm.app.reg.RDetecter;

public class StartupHelper {

	private final static String SYSTEM_FILES_LOCK = "_F.LOCK";

	public static void startup() {
		printSystemInfor();
		verifyVistaPrivileges();
		verifyR();
		verifySystemFiles();
	}

	private static void printSystemInfor() {
		SystemLogger.fileLogger.info("os.name: "
				+ System.getProperty("os.name"));
		SystemLogger.fileLogger.info("user.language: "
				+ System.getProperty("user.language"));
		SystemLogger.fileLogger.info("user.region: "
				+ System.getProperty("user.region"));
		SystemLogger.fileLogger.info("file.encoding: "
				+ System.getProperty("file.encoding"));
		SystemLogger.fileLogger.info("os.version: "
				+ System.getProperty("os.version"));
	}

	private static void verifyVistaPrivileges() {

		if (System.getProperty("os.name").toLowerCase().contains("vista")) {
			SystemLogger.fileLogger.info("Checking Vista privileges...");
			String testFileName = RMAppContext.cachePath
					+ System.currentTimeMillis() + ".tmp";
			File file = new File(testFileName);
			try {
				OutputStream out = new BufferedOutputStream(
						new FileOutputStream(file));
				out.write(1000000);
				out.flush();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(!file.exists() || !file.canWrite()){
				SystemLogger.fileLogger.info(RMAppContext.applicationPath
						+ " can not read/write.");
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(RMAppContext.getRMApp(),
								RMAppConst.ERROR_NEED_VISTA_ADMIN, "Warning",
								JOptionPane.WARNING_MESSAGE);
						RMAppConst.WARM_R_NOT_AVAILABLE_DETAIL_MSG = RMAppConst.ERROR_NEED_VISTA_ADMIN;
					}
				});
			}			
		}
	}

	/**
	 * Verify all the files when system startup
	 */
	private static void verifySystemFiles() {

		File lock = new File(RMAppContext.applicationPath + "/"
				+ SYSTEM_FILES_LOCK);
		if (lock.exists()) {
			return;
		}
		SystemLogger.fileLogger
				.info("<-----------------------List file begin--------------------------->");
		File file = new File(RMAppContext.applicationPath);
		verifySubFolder(file);
		try {
			lock.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			SystemLogger.fileLogger.error(e);
		}
		SystemLogger.fileLogger
				.info("<------------------------List file end------------------------------>");
	}

	private static void verifySubFolder(File file) {
		if (file == null)
			return;
		SystemLogger.fileLogger.info(file.getPath());

		if (file.isDirectory()) {
			File[] subfile = file.listFiles();
			if (subfile != null && subfile.length > 0) {
				for (int i = 0, j = subfile.length; i < j; i++) {
					verifySubFolder(subfile[i]);
				}
			}
		}
	}

	private static void verifyR() {
		String[] rinfo = RDetecter.getRVersionAndPath();
		String rversion = rinfo[0];
		SystemLogger.fileLogger.info("R-version: " + rversion);

		if (null == rversion) {// not install R on system
			SystemLogger.fileLogger.error("R is not installed on system.");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(RMAppContext.getRMApp(),
							RMAppConst.ERROR_NOT_R_MSG, "Warning",
							JOptionPane.WARNING_MESSAGE);
					RMAppConst.WARM_R_NOT_AVAILABLE_DETAIL_MSG = RMAppConst.ERROR_NOT_R_MSG;
				}
			});
			return;
		} else if (!RDetecter.checkdRVersion(rversion)) {// has R installed,but
			// not 2.7 or 2.8
			SystemLogger.fileLogger.error(RMAppConst.ERROR_VERSION_R_MSG);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(RMAppContext.getRMApp(),
							RMAppConst.ERROR_VERSION_R_MSG, "Warning",
							JOptionPane.WARNING_MESSAGE);
					RMAppConst.WARM_R_NOT_AVAILABLE_DETAIL_MSG = RMAppConst.ERROR_VERSION_R_MSG;
				}
			});
			return;
		}

		RMAppContext.R_VERSION = rversion;
		// R2.7 or R2.8 is found
		String rpath = rinfo[1];
		String sysPath = RDetecter.getSystemPathValue();
		String usrPath = RDetecter.getUserPathValue();
		SystemLogger.fileLogger.info("sysPath: " + sysPath);
		SystemLogger.fileLogger.info("usrPath: " + usrPath);
		try {
			if (rpath != null && !"".equals(rpath.trim())) {
				rpath = rpath + "\\bin";
				SystemLogger.fileLogger.info("rpath: " + rpath);
				if (usrPath.indexOf(rpath) != -1
						|| sysPath.indexOf(rpath) != -1) {
					SystemLogger.fileLogger.info("R is found in PATH.");
					// start R engine
					if (checkRVersionInRuntime()) {
						SystemLogger.fileLogger.info("Start R engine!");
						new RMEngineStarter().start();
					}

				} else { // R is not in PATH
					SystemLogger.fileLogger
							.info("R is not found in PATH, try to set it.");
					RDetecter.setR2SystemPath();

					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(RMAppContext
									.getRMApp(),
									RMAppConst.WARM_NEED_RESTART_MSG,
									"Warning", JOptionPane.WARNING_MESSAGE);
							RMAppConst.WARM_R_NOT_AVAILABLE_DETAIL_MSG = RMAppConst.WARM_NEED_RESTART_MSG;
						}
					});

				}
			}
		} catch (Exception e) {
			SystemLogger.fileLogger.error(e);
		}

	}

	/**
	 * 
	 * @return
	 */
	public static boolean checkRVersionInRuntime() {
		boolean end = false;
		String[] commands = { "cmd.exe", "/C", "Rscript", "--version", "null" };
		int ret = -1;// not R installed
		try {
			Process process = Runtime.getRuntime().exec(commands);

			InputStream eis = process.getErrorStream();

			InputStream is = process.getInputStream();

			// FileInputStream fis = new FileInputStream(args[0]);
			InputStreamReader isr = new InputStreamReader(is);
			InputStreamReader eisr = new InputStreamReader(eis);
			BufferedReader br = new BufferedReader(isr);

			String s;
			while (!end && (s = br.readLine()) != null)
				SystemLogger.fileLogger.info(s);

			is.close();
			br = new BufferedReader(eisr);
			while (!end && (s = br.readLine()) != null) {
				SystemLogger.fileLogger.info(s);
				if ((s.indexOf("version") > -1 || s.indexOf("VERSION") > -1)) {
					if (s.indexOf("2.7") > -1 || s.indexOf("2.8") > -1) {
						ret = 1;// R version is correct
						break;
					} else {
						ret = 0; // R install, but not correct
					}
				}
			}
			eis.close();
			process.destroy();

		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
		SystemLogger.fileLogger.info("Check Runtime R version,return code "
				+ ret);
		if (ret == 1) {// R version is correct
			SystemLogger.fileLogger
					.info("Runtime R version checking result ok.");
			return true;
		} else if (ret == 0) {// R install, but not correct
			SystemLogger.fileLogger.error(RMAppConst.ERROR_VERSION_R_MSG);
			JOptionPane.showMessageDialog(RMAppContext.getRMApp(),
					RMAppConst.ERROR_VERSION_R_MSG);
			RMAppConst.WARM_R_NOT_AVAILABLE_DETAIL_MSG = RMAppConst.ERROR_VERSION_R_MSG;
		} else if (ret == -1) {// R is not found in this runtime.
			SystemLogger.fileLogger.info("R is not found in runtime!");
			JOptionPane.showMessageDialog(RMAppContext.getRMApp(),
					RMAppConst.WARM_NEED_RESTART_MSG);
			RMAppConst.WARM_R_NOT_AVAILABLE_DETAIL_MSG = RMAppConst.WARM_NEED_RESTART_MSG;
		}
		SystemLogger.fileLogger
				.info("Runtime R version checking result failed.");
		return false;
	}

}
