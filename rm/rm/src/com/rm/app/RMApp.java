package com.rm.app;

import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.BeanInfo;
import java.beans.DefaultPersistenceDelegate;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jgraph.JGraph;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphUndoManager;

import com.rm.app.graph.RMGraph;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.graph.RMGraphFactory;
import com.rm.app.log.RMLogger;
import com.rm.app.log.SystemLogger;
import com.rm.app.system.StartupHelper;
import com.rm.app.ui.RMAppFactory;
import com.rm.app.ui.StartupWindow;
import com.rm.app.ui.action.ActionHelper;
import com.rm.app.ui.action.RMAppEditAction;
import com.rm.app.ui.action.RMAppFTAction;
import com.rm.app.ui.action.RMAppFileAction;
import com.rm.app.ui.action.RMAppHelpMenuAction;
import com.rm.app.ui.action.RMAppRMAction;
import com.rm.app.ui.action.RMAppWindowMenuAction;
import com.rm.app.ui.action.StatusBarGraphListener;
import com.rm.app.ui.tool.RMAppDocumentBuilder;
import com.rm.app.ui.tool.RMAppKit;
import com.rm.app.util.RMAppFocusManager;

/**
 * 
 * 
 */
public class RMApp extends JFrame implements GraphModelListener, KeyListener,
		GraphSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Actions which Change State
	protected Action hide, collapse, expand, expandAll, configure;

	/**
	 * File chooser for loading and saving graphs. Note that it is lazily
	 * instaniated, always call initFileChooser before use.
	 */
	protected JFileChooser fileChooser = null;

	// JGraph instance
	protected RMGraph graph;

	// Undo Manager
	protected GraphUndoManager undoManager;

	// Actions which Change State
	protected Action undo, redo, remove, group, ungroup, tofront, toback, cut,
			copy, paste;

	// cell count that gets put in cell label
	protected int cellCount = 0;

	// Status Bar
	protected StatusBarGraphListener statusBar;

	static {

		RMResources.addBundles(new String[] { "com.rm.config.rmcomponent",
				"com.rm.config.actions", "com.rm.config.tools",
				"com.rm.config.menus", "com.rm.config.strings" });

	}

	public static void makeCellViewFieldsTransient(Class clazz) {
		try {
			BeanInfo info = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] propertyDescriptors = info
					.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; ++i) {
				PropertyDescriptor pd = propertyDescriptors[i];
				if (!pd.getName().equals("cell")
						&& !pd.getName().equals("attributes")) {
					pd.setValue("transient", Boolean.TRUE);
				}
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
	}

	public RMApp() {
		// welcome image window
		StartupWindow startup = new StartupWindow(this);
		try {
			JDialog.setDefaultLookAndFeelDecorated(true);
			JFrame.setDefaultLookAndFeelDecorated(true);
			UIManager
					.setLookAndFeel("de.muntjak.tinylookandfeel.TinyLookAndFeel");
		} catch (Exception e) {
			// take an appropriate action here
		}

		this.setTitle(RMAppConst.RM_APP_TITLE);
		// need to control this more.
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent event) {

				try {
					boolean quit = ActionHelper
							.confirmSaveCurrentFlow(RMAppConst.MSG_CONFIRM_FOLW_SAVE_EXIT);
					if (quit) {
						SystemLogger.fileLogger.info("Demo System Exit(-1).");
						RMAppContext.getRMApp().setVisible(false);
						cleanAppCache();
						cleanLog();
						System.exit(-1);
					}
				} catch (Exception e) {
					e.printStackTrace();
					RMLogger.debug(e.getMessage());
				}
			}

		});

		configPersistenceDelegate();
		RMAppDocumentBuilder appSettings = new RMAppDocumentBuilder();
		RMAppKit kit = new RMAppKit();
		RMAppContext.set(RMAppConst.RM_APP, this);
		RMAppContext.set(RMAppConst.RM_APP_SETTINGS, appSettings);

		configureKit(kit);
		RMAppContext.set(RMAppConst.RM_APP_KIT, kit);
		RMLogger.debug(kit.getActions());
		RMAppFactory appFactory = new RMAppFactory(kit);

		RMGraphFactory graphFactory = new RMGraphFactory();
		graphFactory.init();

		appFactory.init();

		RMAppContext.set(RMAppConst.RM_GRAPH_FACTORY, graphFactory);
		RMAppContext.set(RMAppConst.RM_APP_FACTORY, appFactory);

		JMenuBar menuBar = (JMenuBar) RMAppContext.get(RMAppConst.RM_MENUBAR); // (

		setJMenuBar(menuBar);
		// Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setPreferredSize(new Dimension(950, 720));

		// MouseMotionListener mouseListener = (MouseMotionListener)object;
		addMouseMotionListener((MouseMotionListener) RMAppContext
				.get(RMAppConst.RM_STATUSBAR));
		// addMouseListener((MouseListener) this);

		URL componentUrl = getClass().getClassLoader().getResource(
				RMAppConst.LOGO_TITLE_IMG);
		ImageIcon componentIcon = new ImageIcon(componentUrl);
		setIconImage(componentIcon.getImage());
		setJMenuBar(menuBar);
		getContentPane().add(
				(JPanel) RMAppContext.get(RMAppConst.RM_MAIN_PANEL));

		this.addComponentListener(new MyComponentListener());

		pack();
		SystemLogger.fileLogger.info("----------------------------------->");
		SystemLogger.fileLogger.info("Demo System startup finished!");
		StartupHelper.startup();

		startup.setVisible(false);
		setVisible(true);
		startup.dispose();
	}

	protected void init() {

	}

	protected void installListeners(JGraph graph) {
		// Add Listeners to Graph
		//
		// Register UndoManager with the Model
		graph.getModel().addUndoableEditListener(undoManager);
		// Update ToolBar based on Selection Changes
		graph.getSelectionModel().addGraphSelectionListener(
				(GraphSelectionListener) this);
		// Listen for Delete Keystroke when the Graph has Focus
		graph.addKeyListener(this);
		graph.getModel().addGraphModelListener(statusBar);
	}

	protected void uninstallListeners(JGraph graph) {
		graph.getModel().removeUndoableEditListener(undoManager);
		graph.getSelectionModel().removeGraphSelectionListener(
				(GraphSelectionListener) this);
		graph.removeKeyListener(this);
		graph.getModel().removeGraphModelListener(statusBar);
		// graph.removeMouseListener(foldingManager);
	}

	// From GraphSelectionListener Interface
	public void valueChanged(GraphSelectionEvent e) {
		// Group Button only Enabled if more than One Cell Selected
		// JGraph graph = settings.getGraph();
		group.setEnabled(graph.getSelectionCount() > 1);
		// Update Button States based on Current Selection
		boolean enabled = !graph.isSelectionEmpty();
		remove.setEnabled(enabled);
		ungroup.setEnabled(enabled);
		tofront.setEnabled(enabled);
		toback.setEnabled(enabled);
		copy.setEnabled(enabled);
		cut.setEnabled(enabled);
	}

	//
	// KeyListener for Delete KeyStroke
	//
	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		// Listen for Delete Key Press
		if (e.getKeyCode() == KeyEvent.VK_DELETE)
			// Execute Remove Action on Delete Key Press
			remove.actionPerformed(null);
	}

	public void graphChanged(GraphModelEvent arg0) {
		super.repaint();
	}

	protected void addActions(RMAppKit kit) {

		// Adds action bundles for each of the menus. To simplify
		// searching for specific actions all actions reside in
		// the classes corresponding to the menu they are in.
		kit.addBundle(new RMAppEditAction.AllActions());
		kit.addBundle(new RMAppFileAction.AllActions());
		kit.addBundle(new RMAppHelpMenuAction.AllActions());
		kit.addBundle(new RMAppWindowMenuAction.AllActions());
		kit.addBundle(new RMAppRMAction.AllActions());
		kit.addBundle(new RMAppFTAction.AllActions());
	}

	protected void configureKit(final RMAppKit kit) {
		addActions(kit);

		KeyboardFocusManager focusManager = KeyboardFocusManager
				.getCurrentKeyboardFocusManager();
		focusManager.addPropertyChangeListener(new PropertyChangeListener() {

			/*
			 * (non-Javadoc)
			 */
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals("permanentFocusOwner"))
					kit.update();

			}
		});

		RMAppFocusManager.getCurrentGraphFocusManager()
				.addPropertyChangeListener(new PropertyChangeListener() {

					/*
					 * (non-Javadoc)
					 */
					public void propertyChange(PropertyChangeEvent e) {
						kit.update();
					}
				});
		kit.update();
	}

	protected void configPersistenceDelegate() {
		Map<Class<?>, DefaultPersistenceDelegate> persistenceDelegate = new HashMap<Class<?>, DefaultPersistenceDelegate>();

		persistenceDelegate.put(RMGraphCell.class,
				new DefaultPersistenceDelegate(new String[] { "userObject" }));
		persistenceDelegate.put(RMGraph.class, new DefaultPersistenceDelegate(
				new String[] { "graphLayoutCache", "model" }));

		persistenceDelegate.put(GraphModel.class,
				new DefaultPersistenceDelegate(new String[] { "roots",
						"attributes", "connectionSet" }));
		persistenceDelegate.put(GraphLayoutCache.class,
				new DefaultPersistenceDelegate(new String[] { "model",
						"factory", "partial" }));

		RMAppContext.persistenceDelegates = persistenceDelegate;
	}

	private class MyComponentListener extends ComponentAdapter {
		public void componentResized(ComponentEvent event) {

		}
	}

	private void cleanAppCache() {
		String cacheFolder = RMAppContext.cachePath;
		File cache = new File(cacheFolder);
		if (cache.exists()) {
			File[] files = cache.listFiles();
			if (files == null || files.length == 0) {
				return;
			}
			for (int i = 0, k = files.length; i < k; i++) {
				SystemLogger.fileLogger.info("Delete eache:"
						+ files[i].getPath());
				files[i].delete();
			}
		}
	}

	private void cleanLog() {
		String cacheFolder = RMAppContext.applicationPath + "/logs";
		File logs = new File(cacheFolder);

		if (logs.exists()) {
			File[] files = logs.listFiles();
			if (files == null || files.length == 0) {
				return;
			}

			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String[] dates = new String[5];
			for (int i = 0; i < dates.length; i++) {
				dates[i] = formatter.format(calendar.getTime())
						.substring(0, 10);
				calendar.add(Calendar.DAY_OF_MONTH, -1);
			}

			for (int i = 0, k = files.length; i < k; i++) {
				boolean delFlag = true;
				if (files[i].isFile())
					for (int q = 0; q < dates.length; q++) {
						String fileName = files[i].getName();
						if (fileName.length() > 10)
							fileName = fileName
									.substring(fileName.length() - 10);
						if (dates[q].equals(fileName))
							delFlag = false;
					}

				if (delFlag) {
					SystemLogger.fileLogger.info("Delete obsolete log:"
							+ files[i].getPath());
					files[i].delete();
				}

			}
		}
	}
}
