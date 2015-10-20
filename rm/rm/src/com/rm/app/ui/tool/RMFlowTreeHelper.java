package com.rm.app.ui.tool;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.rm.app.RMAppContext;
import com.rm.app.ui.action.ActionHelper;
import com.rm.app.ui.action.RMAppFTAction;

/**
 * Display a file system in a JTree view
 * 
 */
public class RMFlowTreeHelper { // extends JPanel {
	private RMFlowTreeHelper() {

	}

	private static String REMOVE_OPEN_FLOW = "REMOVE_OPEN_FLOW";
	private static String ADD_FOLDER_NEW_FLOW = "ADD_FOLDER_NEW_FLOW";
	private static String ADD_FOLDER_NEW_FLOW_REMOVE = "ADD_FOLDER_NEW_FLOW_REMOVE";

	/** Construct a RMFlowTreeHelper */
	public static JTree genRMFlowTree(String fileName) {

		// setLayout(new BorderLayout());
		File dir = new File(fileName);

		// Make a tree list with all the nodes, and make it a JTree
		final JTree tree = new JTree(addNodes(null, dir));

		// Add a listener
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				// DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
				// .getPath().getLastPathComponent();
				// System.out.println("You selected " + e.getPath());
			}
		});

		tree.addMouseListener(new MouseAdapter() {

			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				RMAppContext.flowTree = tree;
				if (SwingUtilities.isRightMouseButton(e)) {
					TreePath selPath = tree.getPathForLocation(e.getX(), e
							.getY());
					tree.setSelectionPath(selPath);
					// Create PopupMenu for the Cell
					JPopupMenu menu = createPopupMenu(e.getPoint(), selPath);
					// Display PopupMenu
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			public void mouseClicked(MouseEvent e) {
				RMAppContext.flowTree = tree;
				if (e.getClickCount() == 2) {
					Object object = tree.getLastSelectedPathComponent();
					RMFlowTreeNode flowTreeNode = object instanceof RMFlowTreeNode ? (RMFlowTreeNode) object
							: null;
					if (flowTreeNode != null) {
						File f = flowTreeNode.getFile();
						if (f.exists() && f.isFile()) {
							//ActionHelper.doFlowOpenAction(null);
							ActionHelper.openRFlow(f.getPath());
						}
					}
				}
			}

		});

		return tree;
	}

	/** Add nodes from under "dir" into curTop. Highly recursive. */
	private static RMFlowTreeNode addNodes(RMFlowTreeNode curTop, File dir) {

		File f;

		String curPath = dir.getPath();

		String curName = dir.getName();
		if ("rmflow".equals(curName))
			curName = null;
		RMFlowTreeNode curDir = new RMFlowTreeNode(curName);
		curDir.setFile(dir);

		if (curTop != null) { // should only be null at root
			curTop.add(curDir);
		}
		Vector<String> ol = new Vector<String>();
		String[] tmp = dir.list();
		for (int i = 0; i < tmp.length; i++) {
			if (tmp[i].startsWith("."))
				continue;
			else
				ol.addElement(tmp[i]);

		}

		// Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);

		Vector<String> files = new Vector<String>();
		// Make two passes, one for Dirs and one for Files. This is #1.
		for (int i = 0; i < ol.size(); i++) {
			String thisObject = ol.elementAt(i);
			String newPath;

			if (curPath.equals("."))
				newPath = thisObject;
			else
				newPath = curPath + File.separator + thisObject;

			f = new File(curPath);

			// curDir.setFile(f);

			if ((f = new File(newPath)).isDirectory()) {
				addNodes(curDir, f);
			} else {
				files.addElement(thisObject);
			}
		}
		// Pass two: for files.
		for (int fnum = 0; fnum < files.size(); fnum++) {
			String name = files.elementAt(fnum);
			if (name.toLowerCase().indexOf(".rm4es") < 0) {
				continue;
			}
			RMFlowTreeNode flowTreeNode = new RMFlowTreeNode(name);
			String fileName = curPath + File.separator + name;
			File file = new File(fileName);
			flowTreeNode.setFile(file);
			curDir.add(flowTreeNode);
		}
		return curDir;
	}

	public Dimension getMinimumSize() {
		return new Dimension(200, 400);
	}

	public Dimension getPreferredSize() {
		return new Dimension(200, 400);
	}

	private static JPopupMenu createPopupMenu(final Point pt, final Object node) {
		JPopupMenu menu = new JPopupMenu();

		if (node != null && node instanceof TreePath) {

			JMenuItem paraItem = new JMenuItem("New a flow..");
			paraItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					Action action = RMAppContext.getKit().getAction(
							RMAppFTAction.FT_NEW);
					action.actionPerformed(e);
				}
			});
			menu.add(paraItem);

			menu.addSeparator();

			JMenuItem paraItemAdd = new JMenuItem("Add a folder..");
			paraItemAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// remove.actionPerformed(e);
					Action action = RMAppContext.getKit().getAction(
							RMAppFTAction.FT_ADD);
					// RMAppContext.cell = null;
					action.actionPerformed(e);
				}
			});

			menu.add(paraItemAdd);

			menu.addSeparator();

			JMenuItem paraItemOpen = new JMenuItem("Open...");
			paraItemOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// remove.actionPerformed(e);
					Action action = RMAppContext.getKit().getAction(
							RMAppFTAction.FT_OPEN);
					// RMAppContext.cell = null;
					action.actionPerformed(e);
				}
			});

			menu.add(paraItemOpen);

			menu.addSeparator();

			JMenuItem paraItemRm = new JMenuItem("Remove");
			paraItemRm.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Action action = RMAppContext.getKit().getAction(
							RMAppFTAction.FT_DELETE);
					action.actionPerformed(e);
				}
			});

			menu.add(paraItemRm);

			menu.addSeparator();

			JMenuItem paraItemRefresh = new JMenuItem("Refresh");
			paraItemRefresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ActionHelper.doRMFlowTreeRefresh();
				}
			});

			menu.add(paraItemRefresh);

			paraItemOpen.setEnabled(false);
			
			String type = checkNode((TreePath) node);
			System.out.println(type);
			if (ADD_FOLDER_NEW_FLOW.equals(type)) {
				paraItemRm.setEnabled(false);
				
			} else if (REMOVE_OPEN_FLOW.equals(type)) {
				paraItem.setEnabled(false);
				paraItemAdd.setEnabled(false);
				paraItemOpen.setEnabled(true);
			} else if (!ADD_FOLDER_NEW_FLOW_REMOVE.equals(type)) {
				paraItem.setEnabled(false);
				paraItemAdd.setEnabled(false);
				paraItemRm.setEnabled(false);
			}

		}

		return menu;
	}

	private static String checkNode(TreePath path) {
		String type = "";

		if (null != path) {

			TreePath parentPath = path.getParentPath();
			DefaultMutableTreeNode currNode = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath
					.getLastPathComponent();
			if (("My Flows".equals(currNode.getUserObject()) || "Company Flows"
					.equals(currNode.getUserObject()))
					&& parentNode.isRoot()) {
				type = ADD_FOLDER_NEW_FLOW;
			} else if (((String) currNode.getUserObject()).endsWith(".rm4es")) {
				type = REMOVE_OPEN_FLOW;
			} else {
				type = ADD_FOLDER_NEW_FLOW_REMOVE;
			}
		}
		return type;
	}

}
