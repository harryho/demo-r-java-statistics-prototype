package com.rm.app.reg;

import java.io.IOException;

import ca.beq.util.win32.registry.RegistryKey;
import ca.beq.util.win32.registry.RegistryValue;
import ca.beq.util.win32.registry.RootKey;
import ca.beq.util.win32.registry.ValueType;

import com.rm.app.log.SystemLogger;
import com.rm.app.util.LogicUtil;

public class RDetecter {

	private static final String sys_path_str = "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment";
	private static final String usr_path_str = "Environment";

	public static String[] getRVersionAndPath() {
		String version = null;
		String path = null;
		try {
			RegistryKey r = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE,
					"Software\\R-core\\R");

			if (r.hasValue("Current Version")) {
				RegistryValue v = r.getValue("Current Version");
				version = v.getStringValue();
			}
			if (r.hasValue("InstallPath")) {
				RegistryValue v = r.getValue("InstallPath");
				path = v.getStringValue();
			}
		} catch (Exception e) {
			SystemLogger.fileLogger.error(e.getMessage());
		}
		return new String[] { version, path };
	}

	public static boolean checkRInSystemPath() {
		String[] infor = getRVersionAndPath();
		String path = infor[1];

		if (path == null || "".equals(path.trim())) {
			return false;
		}
		path = path.trim() + "\\bin";
		String pathValue = getSystemPathValue();
		return (pathValue.indexOf(path.trim()) != -1);
	}

	public static boolean setR2SystemPath() {
		String[] infor = getRVersionAndPath();
		String version = infor[0];
		String rpath = infor[1];
		SystemLogger.fileLogger.info("Rversion: " + version);
		SystemLogger.fileLogger.info("R_path: " + rpath);
		if (checkdRVersion(version)) {
			if (rpath != null && !"".equals(rpath.trim())) {
				rpath = rpath.trim() + "\\bin";
				String pathValue = getSystemPathValue();
				SystemLogger.fileLogger.info("system_path: " + pathValue);
				if (pathValue.indexOf(rpath) == -1) {
					pathValue = rpath+";"+pathValue;
					setSystemPathValue(pathValue);
				}
				// set to User path, and refresh it
				String userPath = getUserPathValue();
				SystemLogger.fileLogger.info("user_path: " + userPath);
				if (userPath.indexOf(rpath) == -1) {
					userPath = rpath +";"+ userPath;
					setUserPathValue(userPath);
				}
			}
		}
		return true;
	}

	public static String getUserPathValue() {
		String value = "";
		try {
			RegistryKey r = new RegistryKey(RootKey.HKEY_CURRENT_USER,
					usr_path_str);
			if (r.hasValue("Path")) {
				RegistryValue v = r.getValue("Path");
				value = v.getStringValue();
			}
		} catch (Exception e) {
			SystemLogger.fileLogger.error(e.getMessage());
		}
		return value;
	}

	public static boolean setUserPathValue(String path) {
		boolean ret = false;
		SystemLogger.fileLogger.info("setUserPathValue: "+path);
		String[] commands = { "cmd.exe", "/C", "setx.exe", "PATH", path };
		try {
			Runtime.getRuntime().exec(commands);			
		} catch (IOException e) {
			e.printStackTrace();
			SystemLogger.fileLogger.error(e.getMessage());
		}
		return ret;
	}

	public static String getSystemPathValue() {
		String value = "";
		try {
			RegistryKey r = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE,
					sys_path_str);
			if (r.hasValue("Path")) {
				RegistryValue v = r.getValue("Path");
				value = v.getStringValue();
			}
		} catch (Exception e) {
			SystemLogger.fileLogger.error(e.getMessage());
		}
		return value;
	}

	public static boolean setSystemPathValue(String path) {
		boolean ret = false;
		try {
			RegistryKey r = new RegistryKey(RootKey.HKEY_LOCAL_MACHINE,
					sys_path_str);
			RegistryValue aa = new RegistryValue("Path", ValueType.REG_SZ, path);
			r.setValue(aa);
			SystemLogger.fileLogger.info("setSystemPathValue: "+ path);
			ret = true;
		} catch (Exception e) {
			SystemLogger.fileLogger.error(e.getMessage());
		}
		return ret;
	}

	public static boolean checkdRVersion(String version) {
		if (version == null || "".equals(version)) {
			return false;
		}
		version= version.trim();
		int d1 = version.indexOf(".");
		int d2 = version.lastIndexOf(".");
		if (d1 != d2) {
			version = version.substring(0, d2)
					+ version.substring(d2+1 , version.length());
		}
		return LogicUtil.checkLogicByExpression(version, ">=2.70" ) & LogicUtil.checkLogicByExpression(version, "<=2.81");//& LogicUtil.checkLogicByExpression(version, "<=2.81");
	}

	public static boolean checkdRVersion() {
		String[] infor = getRVersionAndPath();
		String version = infor[0];
		return checkdRVersion(version);
	}
	
	
	public static void main(String[] args){
		System.out.println(checkdRVersion(" 2.7.2"));
	}

}
