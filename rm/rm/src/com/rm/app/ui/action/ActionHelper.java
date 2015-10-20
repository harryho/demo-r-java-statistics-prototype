package com.rm.app.ui.action;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultTreeModel;

import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import com.rm.app.RMAppConst;
import com.rm.app.RMAppContext;
import com.rm.app.exception.RMException;
import com.rm.app.graph.RMGraph;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.graph.RMModel;
import com.rm.app.io.RMXMLLoader;
import com.rm.app.io.RMXMLSaver;
import com.rm.app.log.RMLogger;
import com.rm.app.log.SystemLogger;
import com.rm.app.r.RHelper;
import com.rm.app.r.RMEngine;
import com.rm.app.ui.RMAppParamDialog;
import com.rm.app.ui.tool.RMFlowTreeHelper;
import com.rm.app.ui.tool.RMFlowTreeNode;
import com.rm.app.validate.RMFlowValidationMessage;
import com.rm.app.validate.RMFlowValidator;

public class ActionHelper {

	private static JFileChooser saveChooser = new JFileChooser();

	private static RMFileFilter xmlFilter = new RMFileFilter(".rm4es",
			"RM4Es Format (*.rm4es) ");

	public static Pattern prePattern = Pattern.compile("^\\.");
	public static Pattern genNamePattern = Pattern
			.compile("[\\\\\\/\\*\\<\\>\\?\\|]");
	public static Pattern flowNamePattern = Pattern.compile("^*\\.rm4es$",
			Pattern.CASE_INSENSITIVE);

	public static boolean doSaveAction() throws Exception {

		RMGraph graph = RMAppContext.getGraph();

		boolean ret = false;
		RMLogger.debug("xml : " + graph.getReferXML());
		RMLogger.debug("isSaved : " + graph.isSaved());
		RMLogger.debug("isEmpty : " + graph.isEmpty());
		if (null == graph.getReferXML() && !graph.isEmpty()) {
			doSaveAsAction();
		} else if (null != graph.getReferXML() && !graph.isSaved()) {
			String fileName = RMAppContext.getGraph().getReferXML();
			ret = RMXMLSaver.saveToXML(fileName);
			RMLogger.info("Save to " + fileName);
		}

		return ret;
	}

	public static void doSaveAsAction() {
		JFrame parent = RMAppContext.getRMApp();
		saveChooser.setFileFilter(xmlFilter);
		saveChooser.addChoosableFileFilter(xmlFilter);
		String fileName = editorFileDialog(parent, "Save", "", false, new File(
				RMAppContext.rmMyFlowPath), saveChooser);
		if (null != fileName) {
			RMXMLSaver.saveToXML(fileName);
			RMLogger.info("Save to " + fileName + " successfully!");
		}

	}

	public static void doFileOpenActionWithChooser(String folder) {
		JFrame parent = RMAppContext.getRMApp();
		saveChooser.setFileFilter(xmlFilter);
		saveChooser.addChoosableFileFilter(xmlFilter);
		String fileName = editorFileDialog(parent, "Open", "", true, new File(
				folder), saveChooser);
		openRFlow(fileName);
	}

	public static void openRFlow(String fileName) {
		boolean opened = false;
		if (fileName != null) {
			try {
				if (!RMAppContext.getGraph().isEmpty()
						&& !RMAppContext.getGraph().isSaved()) {
					int returnCode = JOptionPane.showConfirmDialog(RMAppContext
							.getRMApp(), RMAppConst.MSG_CONFIRM_FOLW_SAVE_OPEN);
					if (0 == returnCode) {// yes
						doSaveAction();
					} else if (1 == returnCode) {// not
					} else if (2 == returnCode) {// cancel
						return;
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			RHelper.cleanCurrentOutputWindow();
			try {
				opened = RMXMLLoader.loadXML(fileName);
			} catch (Exception e) {
				File file = new File(fileName);
				// if the file is empty, then ignore exception,otherwise alert
				// message
				if (0 == file.length()) {
					opened = true;
					// clean current workspace
					cleanCurrentSpaceToFile(null, fileName);
				} else {
					opened = false;
				}

			}
			if (opened) {
				RMAppContext.getGraph().setReferXML(fileName);
				RMAppContext.getGraph().setIsSaved(true);
				RMLogger.info("Open " + fileName);
			} else {
				JOptionPane.showMessageDialog(RMAppContext.getRMApp(),
						"Open failed! File format is incorrect.", "Warning",
						JOptionPane.WARNING_MESSAGE);
				RMLogger.warn("Open " + fileName + " failed ");
			}

		}
	}

	/**
	 * clean current graph workspace, and set referring file to new workspace
	 * 
	 * @param file
	 */
	public static void cleanCurrentSpaceToFile(RMGraph graph, String file) {
		if (graph == null) {
			graph = RMAppContext.getGraph();
		}
		GraphModel model = new RMModel();
		CellViewFactory cellViewFactory = new DefaultCellViewFactory();
		GraphLayoutCache layoutCache = new GraphLayoutCache(model,
				cellViewFactory, true);
		graph.setGraphLayoutCache(layoutCache);
		graph.setIsSaved(false);
		graph.setReferXML(file);
		RHelper.cleanCurrentOutputWindow();
	}

	public static void doRunAction() {
		if(RHelper.isRunning){
			return ;
		}
		RMGraph graph = RMAppContext.getGraph();
		if (graph != null) {
			saveGraph4Log();
			RMLogger.cleanConsole();
			if (RMFlowValidator.isValidResearchFlow(graph)) {
				if (RMEngine.R == null) {
					RMLogger.error(RMAppConst.WARM_R_NOT_AVAILABLE_MSG);
					RMLogger.error(RMAppConst.WARM_R_NOT_AVAILABLE_DETAIL_MSG);
					return;
				}
				try {
					RHelper.runResearchFlow(RMAppContext.activeRMFlows);
				} catch (RMException re) {
					JOptionPane.showMessageDialog(null, re.getMessage());
					RMLogger.error(re.getMessage());
					RHelper.cleanRunningStatus();
					return;
				} catch (Exception e) {
					e.printStackTrace();
					RMLogger
							.error("Encounter unknown issue. Please check the flow carefully.");
					RHelper.cleanRunningStatus();
					SystemLogger.fileLogger.error(e);
					return;
				}

			} else {
				RMLogger.warn(RMFlowValidationMessage.getErrorMessage());
				JOptionPane.showMessageDialog(null, RMFlowValidationMessage
						.getErrorMessage());
				RMFlowValidationMessage.cleanErrorMessage();
			}
		} else {

		}
	}

	public static Component getPermanentFocusOwnerOrParent() {
		Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.getPermanentFocusOwner();
		return comp;
	}

	public static boolean confirmSaveCurrentFlow(String msg) throws Exception {
		RMGraph graph = RMAppContext.getGraph();
		if (!graph.isSaved()) {
			int returnCode = JOptionPane.showConfirmDialog(RMAppContext
					.getRMApp(), msg);
			if (0 == returnCode) {// yes

			} else if (1 == returnCode) {// not
				if (!doSaveAction()) {
					return false;
				}
			} else if (2 == returnCode) {// cancel
				return false;
			}

		}
		return true;
	}

	/**
	 * 
	 * @param event
	 */
	public static void doHelpAction(ActionEvent event) {
		String helpFile = RMAppContext.documentPath + "help.chm";
		String[] command = { "cmd.exe", "/C", helpFile };
		//String[] command = { "cmd.exe","/C","Rterm.exe --vanilla --slave < d:/load.r > d:/a.txt" };
		try {
			Runtime.getRuntime().exec(command);
//			RMLogger.debug(RMEngine.executeAndWaitReturn("Sys.getenv('R_LIBS_USER')",100, false));
			//String cmd = "load('car')";
		//	RMLogger.debug(RMEngine.executeAndWaitReturn(cmd,100, false));
//			cmd = "getwd() \n";
//			RMLogger.debug(RMEngine.executeAndWaitReturn(cmd,100, false));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//  
	public static void doRMComponentEditAction(RMGraphCell cell) {
		try {
			new RMAppParamDialog(cell);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String editorFileDialog(Component component, String title,
			String filename, boolean open, File directory, JFileChooser chooser) {

		if (chooser != null) {
			chooser.setDialogTitle(title);

			chooser.setCurrentDirectory(directory);
			return showFileChooser(component, chooser, open);
		}
		return null;
	}

	protected static String showFileChooser(Component component,
			JFileChooser chooser, boolean open) {
		int returnValue = JFileChooser.CANCEL_OPTION;
		if (open)
			returnValue = chooser.showOpenDialog(component);
		else
			returnValue = chooser.showSaveDialog(component);

		if (returnValue == JFileChooser.APPROVE_OPTION
				&& chooser.getSelectedFile() != null) {
			String filename = chooser.getSelectedFile().getAbsolutePath();
			FileFilter tmp = chooser.getFileFilter();
			if (tmp instanceof RMFileFilter) {
				RMFileFilter filter = (RMFileFilter) tmp;
				String ext = filter.getExt().toLowerCase();
				if (!open && !filename.toLowerCase().endsWith(ext))
					filename += ext;
			}
			return filename;
		}
		return null;
	}

	public static boolean isURL(Object value) {
		return (value != null && (value.toString().startsWith("http://")
				|| value.toString().startsWith("mailto:")
				|| value.toString().startsWith("ftp://")
				|| value.toString().startsWith("file:")
				|| value.toString().startsWith("https://")
				|| value.toString().startsWith("webdav://") || value.toString()
				.startsWith("webdavs://")));
	}

	protected static class RMFileFilter extends FileFilter {
		String ext = ".rm4es";

		String desc = "RM4Es format";

		public RMFileFilter(String ext, String desc) {
			this.ext = ext;
			this.desc = desc;
		}

		public String getExt() {
			return ext;
		}

		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().endsWith(ext);
		}

		public String getDescription() {
			return desc;
		}

	}

	protected static void saveGraph4Log() {
		String fileName = RMAppContext.applicationPath + "/logs/graph.xml";
		RMXMLSaver.saveToXML4Log(fileName);

	}

	public static void doFTAddAction(ActionEvent e) {

		String nodeName = JOptionPane.showInputDialog(null,
				"Please enter the folder name:");

		if (nodeName != null) {
			if ("".equals(nodeName) || nodeName.trim().length() == 0) {
				JOptionPane.showMessageDialog(null,
						"Folder name can not be empty!");
			} else {

				Matcher m = prePattern.matcher(nodeName);
				Matcher m3 = genNamePattern.matcher(nodeName);
				Matcher m2 = flowNamePattern.matcher(nodeName);
				boolean result = m.find() || m3.find();

				if (result) {

					JOptionPane.showMessageDialog(null,
							"Please enter valid folder name!");
				} else if (m2.find()) {

					JOptionPane.showMessageDialog(null,
							"Please avoid \".rm4es\" in the folder name!");

				} else {

					JTree flowTree = RMAppContext.flowTree;

					Object object = flowTree.getLastSelectedPathComponent();
					RMFlowTreeNode flowTreeNode = object instanceof RMFlowTreeNode ? (RMFlowTreeNode) object
							: null;

					String sPath = null;

					if (flowTreeNode != null) {
						File curDir = flowTreeNode.getFile();

						sPath = curDir.getPath() + File.separator + nodeName;
						File f = new File(sPath);

						boolean flag = false;
						if (!f.isDirectory()) {
							flag = f.mkdirs();
						}

						if (flag) {
							DefaultTreeModel model = (DefaultTreeModel) flowTree
									.getModel();
							RMFlowTreeNode node = new RMFlowTreeNode(nodeName);
							node.setFile(f);
							model.insertNodeInto(node, flowTreeNode, node
									.getChildCount());
						}
					}
				}
			}
		}

	}

	public static void doFTNewAction() {

		String nodeName = JOptionPane.showInputDialog(null,
				"Please enter the file name:");
		if (nodeName != null) {
			if ("".equals(nodeName) || nodeName.trim().length() == 0) {
				JOptionPane.showMessageDialog(null,
						"File name can not be empty!");
			} else {
				Matcher m = prePattern.matcher(nodeName);
				Matcher m3 = genNamePattern.matcher(nodeName);
				Matcher m2 = flowNamePattern.matcher(nodeName);

				boolean result = m.find() || m3.find();
				if (result) {

					JOptionPane.showMessageDialog(null,
							"Please enter valid file name!");
				} else {

					if (!m2.find()) {
						nodeName = nodeName.endsWith(".") ? nodeName
								.concat("rm4es") : nodeName.concat(".rm4es");
					}

					JTree flowTree = RMAppContext.flowTree;
					Object object = flowTree.getLastSelectedPathComponent();
					RMFlowTreeNode flowTreeNode = object instanceof RMFlowTreeNode ? (RMFlowTreeNode) object
							: null;

					if (flowTreeNode != null) {
						String sPath = null;
						File curDir = flowTreeNode.getFile();
						sPath = curDir.getPath() + File.separator + nodeName;
						File f = new File(sPath);

						boolean flag = false;
						if (!f.isFile()) {
							try {
								flag = f.createNewFile();
							} catch (IOException e) {
								JOptionPane
										.showMessageDialog(null,
												"You can not create a new flow! Please contact your system admin.");
								e.printStackTrace();
							}
						}

						if (flag) {
							DefaultTreeModel model = (DefaultTreeModel) flowTree
									.getModel();
							RMFlowTreeNode node = new RMFlowTreeNode(nodeName);
							node.setFile(f);
							model.insertNodeInto(node, flowTreeNode, node
									.getChildCount());
						}
					}
				}
			}
		}
	}

	public static void doFTDeleteAction() {
		JTree flowTree = RMAppContext.flowTree;
		Object object = flowTree.getLastSelectedPathComponent();
		RMFlowTreeNode flowTreeNode = object instanceof RMFlowTreeNode ? (RMFlowTreeNode) object
				: null;
		if (flowTreeNode != null) {
			String nodeName = (String) flowTreeNode.getUserObject();
			if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null,
					"This action can not be recovered. Do you confirm to delete \"" + nodeName + "\" ?")) {
				String sPath = null;
				File f = flowTreeNode.getFile();
				boolean flag = false;
				if (f.exists()) {
					flag = deleteDirectory(f);
				}
				if (flag) {
					DefaultTreeModel model = (DefaultTreeModel) flowTree
							.getModel();
					model.removeNodeFromParent(flowTreeNode);
				}
			}
		}
	}

	public static void doRMFlowTreeRefresh() {

		JTree flowTree = RMAppContext.flowTree;

		String rmFlowPath = RMAppContext.rmFlowPath;
		File file = new File(rmFlowPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		JTree newTree = RMFlowTreeHelper.genRMFlowTree(rmFlowPath);

		DefaultTreeModel model = (DefaultTreeModel) newTree.getModel();
		flowTree.setModel(model);
		DefaultTreeModel currModel = (DefaultTreeModel) flowTree.getModel();
		RMAppContext.set(RMAppConst.RM_FLOW_TREE, flowTree);

	}

	private static boolean deleteDirectory(File path) {
		if (path.exists() && path.isDirectory()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

}
