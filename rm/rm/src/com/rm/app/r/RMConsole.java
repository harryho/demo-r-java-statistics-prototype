package com.rm.app.r;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Vector;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

import com.rm.app.RMAppContext;

public class RMConsole implements RMainLoopCallbacks {

	private String workspace = RMAppContext.applicationPath;

	public StringBuffer callBackMessage = new StringBuffer();
	
	private final Object lock = new Object();

	public  void cleanCallBackMessage() {
		synchronized (lock) {
			callBackMessage = new StringBuffer();
		}
	}

	private void recordCallBackMessge(String text){
		synchronized (lock) {
			callBackMessage.append(text);
		}
	}
	public void rWriteConsole(Rengine re, String text) {
		rWriteConsole(re, text, 0);
	}

	public void rWriteConsole(Rengine re, String text, int oType) {
		recordCallBackMessge(text);
	}

	public void rBusy(Rengine re, int which) {
	}

	public String rReadConsole(Rengine re, String prompt, int addToHistory) {
		if (prompt.indexOf("Save workspace") > -1) {
			String retVal = RMEngine.exit();
			if (workspace != null && retVal.indexOf('y') >= 0) {
				RMEngine.R.eval("save.image(\"" + workspace.replace('\\', '/')
						+ "\")");
				return "n\n";
			} else
				return retVal;
		} else {
			String s = RMEngine.rSync.waitForNotification();
			return (s == null || s.length() == 0) ? "\n" : s + "\n";
		}
	}

	public void rShowMessage(Rengine re, String message) {
		//RMLogger.debug("rShowMessage \"" + message + "\"");
	}

	public String rChooseFile(Rengine re, int newFile) {
		//RMLogger.debug("rChooseFile ===> ");
		FileDialog fd = new FileDialog(new Frame(),
				(newFile == 0) ? "Select a file" : "Select a new file",
				(newFile == 0) ? FileDialog.LOAD : FileDialog.SAVE);
		fd.setVisible(true);
		String res = null;
		if (fd.getDirectory() != null)
			res = fd.getDirectory();
		if (fd.getFile() != null)
			res = (res == null) ? fd.getFile() : (res + fd.getFile());
		return res;
	}

	public void rFlushConsole(Rengine re) {
		//RMLogger.debug("rFlushConsole ===> ");
	}

	public void rLoadHistory(Rengine re, String filename) {
		File hist = null;
		try {
			if ((hist = new File(filename)).exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(hist));
				if (RMEngine.RHISTORY == null)
					RMEngine.RHISTORY = new Vector<String>();
				while (reader.ready())
					RMEngine.RHISTORY.add(reader.readLine() + "\n");
				reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void rSaveHistory(Rengine re, String filename) {
		//RMLogger.debug("rSaveHistory ===> ");
	}
}
