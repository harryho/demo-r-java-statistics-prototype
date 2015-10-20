package com.rm.app;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JTree;

import org.jgraph.JGraph;

import com.rm.app.graph.RMGraph;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.ui.tool.RMAppDocumentBuilder;
import com.rm.app.ui.tool.RMAppKit;
import com.rm.app.util.RMGraphDropTargetAdapter;

public class RMAppContext extends RMAppConst {

	private static Map map = new HashMap();

	public static RMGraphCell cell;

	public static boolean treeDragged;

	public static Object rmGraphListener = new RMGraphDropTargetAdapter();

	public static JGraph activeGraph;

	public static String applicationPath = null;

	public static String documentPath = null;

	public static List activeRMFlows = null;

	public static Map persistenceDelegates = new HashMap();

	public static String cachePath = null;

	public static String rmFlowPath = null;

	public static String rmMyFlowPath = null;

	public static String rmCompanyFlowPath = null;
	
	public static String graphvizPath = null;

	public static AbstractButton runButton = null;

	public static AbstractButton stopButton = null;

	public static JTree flowTree = null;

	public static String R_VERSION = "";
	


	static {
		File a = new File("");
		applicationPath = a.getAbsolutePath().replaceAll("\\\\", "/");

		documentPath = applicationPath + "/" + "doc" + "/";

		cachePath = applicationPath + "/" + "cache" + "/";

		rmFlowPath = applicationPath + "/" + "rmflow" + "/";

		rmMyFlowPath = applicationPath + "/" + "rmflow" + "/" + "My Flows"
				+ "/";

		rmCompanyFlowPath = applicationPath + "/" + "rmflow" + "/"
				+ "Company Flows" + "/";
		
		graphvizPath=applicationPath + "/" + "gvz" + "/"+ "bin" + "/";

		a = new File(cachePath);
		if (!a.exists()) {
			a.mkdirs();
		}
	}

	public Map getAll() {
		return map;
	}

	public static void set(Object key, Object value) {
		map.put(key, value);
	}

	public static Object get(Object key) {
		return map.get(key);
	}

	public static RMAppKit getKit() {
		return (RMAppKit) map.get(RM_APP_KIT);
	}

	public static RMAppDocumentBuilder getRMAppSettings() {
		return (RMAppDocumentBuilder) map.get(RM_APP_SETTINGS);
	}

	public static RMGraph getGraph() {
		return (RMGraph) map.get(RM_GRAPH);
	}

	public static RMApp getRMApp() {
		return (RMApp) map.get(RM_APP);
	}

}
