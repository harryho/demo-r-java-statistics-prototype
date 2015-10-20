package com.rm.app.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.jgraph.graph.BasicMarqueeHandler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rm.app.RMAppConst;
import com.rm.app.RMAppContext;
import com.rm.app.RMResources;
import com.rm.app.graph.RMMarqueeHandler;
import com.rm.app.log.RMLogger;
import com.rm.app.ui.action.RMAppAction;
import com.rm.app.ui.action.RMAppToolbarAction;
import com.rm.app.ui.tool.RMAppDocumentBuilder;
import com.rm.app.ui.tool.RMAppKit;
import com.rm.app.ui.tool.RMFlowTreeHelper;
import com.rm.app.ui.tool.RMTreeCellRenderer;

public class RMAppFactory {

	/**
	 * Defines the nodename for separators.
	 */
	public static final String NODENAME_SEPARATOR = "separator";

	/**
	 * Defines the nodename for items.
	 */
	public static final String NODENAME_ITEM = "item";

	/**
	 * Defines the nodename for menus.
	 */
	public static final String NODENAME_MENU = "menu";

	public static final String NODENAME_MENUITEM = "menuitem";

	/**
	 * Defines the nodename for groups.
	 */
	public static final String NODENAME_GROUP = "group";

	/**
	 * Defines the suffix for actionname resources. This is used to replace the
	 * default action name, which is its key, eg.
	 * <code>openFile.label=Open...</code>.
	 */
	public static final String SUFFIX_ACTION = ".action";

	/**
	 * Defines the suffix for toolname resources. This is used to replace the
	 * default tool name, which is its key, eg.
	 * <code>openFile.label=Open...</code>.
	 */
	public static final String SUFFIX_TOOL = ".tool";

	/**
	 * Defines the suffix for label resources, eg.
	 * <code>open.label=Open...</code>.
	 */
	public static final String SUFFIX_LABEL = ".label";

	/**
	 * Defines the suffix for icon resources, eg.
	 * <code>open.icon=/com/jgraph/pad/images/open.gif</code>.
	 */
	public static final String SUFFIX_ICON = ".icon";

	/**
	 * Defines the suffix for mnemonic resources, eg.
	 * <code>open.mnemonic=o</code>.
	 */
	public static final String SUFFIX_MNEMONIC = ".mnemonic";

	/**
	 * Defines the suffix for shortcut resources, eg.
	 * <code>open.shortcut=control O</code>.
	 */
	public static final String SUFFIX_SHORTCUT = ".shortcut";

	/**
	 * Defines the suffix for tooltip resources, eg.
	 * <code>open.tooltip=Open a file</code>.
	 */
	public static final String SUFFIX_TOOLTIP = ".tooltip";

	/**
	 * Constant for menubar creation.
	 */
	protected static final int ITEMTYPE_MENUBAR = 0;

	/**
	 * Constant for toolbar creation.
	 */
	protected static final int ITEMTYPE_TOOLBAR = 1;

	/**
	 * Shared separator instance.
	 */
	protected static final Component SEPARATOR = new JButton();

	/**
	 * Holds the (name, factory method) pairs
	 */
	protected Map factoryMethods = new Hashtable();

	protected RMAppKit kit;

	/**
	 * Shortcut method to {@link RMResources#getString(String)}.
	 * 
	 * @return Returns the resource string for <code>key</code>.
	 */
	protected String getString(String key) {
		return RMResources.getString(key);
	}

	/**
	 * Shortcut method to {@link RMAppDocumentBuilder#getKeyAttributeValue(Node)}.
	 */
	protected String getKey(Node node) {
		return RMAppDocumentBuilder.getAttributeValue(node,
				RMAppDocumentBuilder.ATTRIBUTENAME_KEY);
	}

	/**
	 * Constructs a factory for the specified kit.
	 * 
	 * @return
	 */
	public RMAppFactory(RMAppKit kit) {
		setKit(kit);
	}

	private void setKit(RMAppKit kit) {
		this.kit = kit;
	}

	/**
	 * Returns the editor kit.
	 * 
	 * @return Returns the kit.
	 */
	public RMAppKit getKit() {
		return kit;
	}

	/**
	 * Returns the action for the resource under <code>key+SUFFIX_ACTION</code>
	 * from the editor kit. If no such resource exists then the action for
	 * <code>key</code> is returned or <code>null</code> if no action can be
	 * found.
	 * 
	 * @return Returns the action for the <code>key+SUFFIX_ACTION</code>
	 *         resource or the action for <code>key</code>.
	 * 
	 * @see #getString(String)
	 * @see RMEditorKit#getAction(String)
	 */
	public RMAppAction getAction(String key) {
		String tmp = getString(key + SUFFIX_ACTION);
		if (tmp != null)
			key = tmp;

		RMLogger.debug(getKit());
		// Logger.debug(getKit().getActions());
		return getKit().getAction(key);
	}

	/**
	 * Returns a new {@link JScrollPane} containing the specified component.
	 * 
	 * @return Returns a new scrollpane containing the specified component.
	 */
	public static JScrollPane createScrollPane(Component component) {
		JScrollPane scrollPane = new JScrollPane(component);
		scrollPane.setFocusable(false);
		scrollPane.getHorizontalScrollBar().setFocusable(false);
		scrollPane.getVerticalScrollBar().setFocusable(false);
		return scrollPane;
	}

	/**
	 * Returns a new {@link JSplitPane} containing the specified component with
	 * the specified orientation.
	 * 
	 * @return Returns a new splitpane containing the the specified components.
	 */
	public static JSplitPane createSplitPane(Component first, Component second,
			int orientation) {
		JSplitPane splitPane = new JSplitPane(orientation, first, second);
		splitPane.setBorder(null);
		splitPane.setFocusable(false);
		return splitPane;
	}

	/**
	 * Returns a new empty {@link JTabbedPane}.
	 * 
	 * @return Returns a new tabbed pane.
	 */
	public static JTabbedPane createTabbedPane(int tabPlacement) {
		JTabbedPane tabPane = new JTabbedPane(tabPlacement);
		tabPane.setFocusable(false);
		return tabPane;
	}

	public static JTabbedPane createConsolePropertyPane(int tabPlacement) {
		JTabbedPane tabPane = new JTabbedPane(tabPlacement);
		// tabPane.setPreferredSize(new Dimension(200,50));
		tabPane.setEnabled(false);

		tabPane.setPreferredSize(new Dimension(300, 40));
		tabPane.setSize(200, 40);
		tabPane.setFocusable(false);
		// tabPane.setVisible(false);
		// tabPane.addTab("Console", new JPanel());
		return tabPane;
	}

	/**
	 * Hook for subclassers to configure a toolbox button for <code>tool</code>.
	 * 
	 * @param button
	 *            The button to be configured.
	 * @param name
	 *            The name of the tool or action to configure the button for.
	 */
	protected void configureAbstractButton(AbstractButton button, String name) {
		button.setFocusable(false);
		button.setText("");

		// Configures the tooltip
		String tip = getString(name + SUFFIX_TOOLTIP);
		if (tip == null)
			tip = getString(name + SUFFIX_LABEL);
		button.setToolTipText((tip != null) ? tip : name);

		// Configures the icon and size
		ImageIcon icon = RMResources.getImage(getString(name + SUFFIX_ICON));
		if (icon != null) {
			button.setIcon(icon);
			button.setForeground(new Color(216, 221, 240));
			// button.setBackground(Color.WHITE);
			button.setOpaque(true);

			Dimension d = new Dimension(icon.getIconWidth() + 8, icon
					.getIconHeight() + 10);
			button.setMaximumSize(d);
			button.setPreferredSize(d);
		}
	}

	//
	// Menus
	//

	/**
	 * Returns a new {@link JMenuBar} configured using
	 * {@link #configureMenuBar(Container, Node)}.
	 * 
	 * @param configuration
	 *            The configuration to create the menubar with.
	 * @return Returns a new toolbox.
	 */
	public JMenuBar createMenuBar(Node configuration) {
		JMenuBar menuBar = new JMenuBar();
		configureMenuBar(menuBar, configuration);
		return menuBar;
	}

	/**
	 * Returns a new {@link JPopupMenu} configured using
	 * {@link #configureMenuBar(Container, Node)}.
	 * 
	 * @param configuration
	 *            The configuration to create the popup menu with.
	 * @return Returns a new toolbox.
	 */
	public JPopupMenu createPopupMenu(Node configuration) {
		JPopupMenu menuBar = new JPopupMenu();
		configureMenuBar(menuBar, configuration);
		return menuBar;
	}

	/**
	 * Hook for subclassers to configure a new menu based on
	 * <code>configuration</code>. This is used for menubars and submenus.
	 * 
	 * @param menu
	 *            The menu to be configured.
	 * @param configuration
	 *            The configuration to configure the menubar with.
	 * 
	 * @see #createMenuItem(Node, boolean)
	 */

	protected void configureMenuBar(Container menu, Node configuration) {
		if (configuration != null) {

			String key = getKey(configuration);

			if (menu instanceof JMenu) {
				JMenu tmp = (JMenu) menu;
				tmp.setText(getString(key + SUFFIX_LABEL));

			}

			NodeList nodes = configuration.getChildNodes();

			for (int i = 0; i < nodes.getLength(); i++) {
				Node child = nodes.item(i);
				String name = child.getNodeName();
				// Logger.debug(" child " + child);
				// Logger.debug(" name " + name);

				if (name.equals(NODENAME_MENU)) {

					// Creates and adds a submenu
					Container subMenu = createMenu(child);
					configureMenuBar(subMenu, child); // recurse
					menu.add(subMenu);

				} else if (name.equals(NODENAME_GROUP)) {

					// Creates a button group
					ButtonGroup group = new ButtonGroup();
					NodeList groupNodes = child.getChildNodes();
					for (int j = 0; j < groupNodes.getLength(); j++) {
						Node groupChild = groupNodes.item(j);
						RMLogger.debug("  groupChild  " + groupChild);
						RMLogger.debug("  name " + groupChild.getNodeName());
						if (groupChild.getNodeName().equals(NODENAME_ITEM)) {
							AbstractButton button = createMenuItem(groupChild,
									true);
							if (button != null) {
								menu.add(button);
								group.add(button);
							}
						}
					}

					// Create Item
				} else if (name.equals(NODENAME_ITEM)) {

					AbstractButton item = createMenuItem(child, false);
					// Logger.debug(" item key " + getKey(child));
					// Logger.debug(" is null " + (item != null) +
					// item.getText() + item.getClass());
					if (item != null)
						menu.add(item);
				} else if (name.equals(NODENAME_SEPARATOR)
						&& menu instanceof JMenu) {
					((JMenu) menu).addSeparator();
				} else if (name.equals(NODENAME_SEPARATOR)
						&& menu instanceof JPopupMenu) {
					((JPopupMenu) menu).addSeparator();
				}
			}
		}
	}

	/**
	 * Hook for subclassers to create a new menu. This implementation returns a
	 * new instance of {@link JMenuBar}.
	 * 
	 * @param configuration
	 *            The configuration to create the menu with.
	 * @return Returns a new menubar.
	 */
	protected Container createMenu(Node configuration) {
		return new JMenu();
	}

	/**
	 * Returns a new {@link JCheckBoxMenuItem} or {@link JMenuItem} configured
	 * using {@link #configureActionItem(AbstractButton, RMAppAction)}.
	 * 
	 * @param configuration
	 *            The configuration to create the menu item with.
	 * @param radio
	 *            Whether the created item should be a
	 *            {@link JRadioButtonMenuItem}.
	 * @return Returns a new menu item.
	 */
	public AbstractButton createMenuItem(Node configuration, boolean radio) {

		RMAppAction action = getAction(getKey(configuration));

		if (action == null) {

			action = new RMAppAction(getKey(configuration)) {

				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
				}
			};
		}

		AbstractButton item = (radio) ? new JRadioButtonMenuItem() : (action
				.isToggleAction()) ? new JCheckBoxMenuItem() : new JMenuItem();
		configureActionItem(item, action);

		return item;
	}

	/**
	 * Hook for subclassers to configure an action item for <code>action</code>.
	 * Valid action items are toolbar buttons and menu items, but not toolbox
	 * buttons.
	 * 
	 * @param button
	 *            The button to be configured.
	 * @param action
	 *            The action to configure the button for.
	 */
	public void configureActionItem(AbstractButton button, RMAppAction action) {
		String name = action.getActionObjectName();
		button.setFocusable(false);
		button.setAction(action);
		button.setEnabled(action.isEnabled());
		button.setSelected(action.isSelected());

		// Listens to changes of the action state and upates the button
		action.addPropertyChangeListener(createActionChangeListener(button));

		// Configures the label
		String label = getString(name + SUFFIX_LABEL);
		// Logger.debug(" label " + label);
		button.setText((label != null && label.length() > 0) ? label : name);

		// Configures the icon
		ImageIcon icon = RMResources.getImage(getString(name + SUFFIX_ICON));
		if (icon != null)
			button.setIcon(icon);

		// Configures the mnemonic
		String mnemonic = getString(name + SUFFIX_MNEMONIC);
		if (mnemonic != null && mnemonic.length() > 0)
			button.setMnemonic(mnemonic.toCharArray()[0]);

		// Configures the tooltip
		String tooltip = getString(name + SUFFIX_TOOLTIP);
		if (tooltip != null)
			button.setToolTipText(tooltip);

		// Configures the shortcut aka. accelerator
		String shortcut = getString(name + SUFFIX_SHORTCUT);
		if (shortcut != null && button instanceof JMenuItem)
			((JMenuItem) button).setAccelerator(KeyStroke
					.getKeyStroke(shortcut));
	}

	//
	// Toolbar
	//

	/**
	 * Returns a new {@link JToolBar} configured using
	 * {@link #configureToolBar(Container, Node)}.
	 * 
	 * @param configuration
	 *            The configuration to create the toolbar with.
	 * @return Returns a new toolbar.
	 */
	public JToolBar createToolBar(Node configuration) {
		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		// toolBar.setBackground(Color.WHITE);
		configureToolBar(toolBar, configuration);
		return toolBar;

	}

	/**
	 * Hook for subclassers to configure a toolbar with
	 * <code>configuration</code>.
	 * 
	 * @param toolBar
	 *            The toolBar to be configured.
	 * @param configuration
	 *            The configuration to configure the toolbar with.
	 */
	protected void configureToolBar(Container toolBar, Node configuration) {
		if (configuration != null) {
			NodeList nodes = configuration.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node child = nodes.item(i);
				String name = child.getNodeName();

				Component c = null; // executeMethod(getKey(child), child);
				if (c != null) {
					toolBar.add(c);
				} else if (name.equals(NODENAME_ITEM)) {
					AbstractButton button = createToolBarButton(child);

					if (button != null) {
						RMAppAction action = (RMAppAction) button.getAction();
						String command = action.getActionObjectName();

						if ("run".equals(command))
							RMAppContext.runButton = button;

						if ("terminal".equals(command))
							RMAppContext.stopButton = button;

						toolBar.add(button);
					}
				} else if (name.equals(NODENAME_SEPARATOR)
						&& toolBar instanceof JToolBar) {
					((JToolBar) toolBar).addSeparator();
				}
			}
		}
	}

	public static RMAppDiagramPane createDiagramPane() {
		return new RMAppDiagramPane(RMAppContext.getGraph());
	}

	/**
	 * Returns a new {@link JToggleButton} or {@link JButton} configured using
	 * {@link #configureActionItem(AbstractButton, RMAppAction)} and
	 * {@link #configureAbstractButton(AbstractButton, String)} (in this order).
	 * 
	 * @param configuration
	 *            The configuration to create the toolbar with.
	 * @return Returns a new toolbar.
	 */
	protected AbstractButton createToolBarButton(Node configuration) {
		// RMAppAction action = getAction(getKey(configuration));
		RMAppAction action = new RMAppToolbarAction(getKey(configuration)) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		};
		if (action != null) {
			AbstractButton button = null;
			if (action.isToggleAction())
				button = new JToggleButton();
			else
				button = new JButton();

			configureActionItem(button, action);
			configureAbstractButton(button, action.getActionObjectName());
			return button;
		}
		return null;
	}

	/**
	 * Returns a new property change listener that updates <code>button</code>
	 * according to property change events.
	 * 
	 * @param button
	 *            The button to create the listener for.
	 * @return Returns a new property change listener for actions.
	 */
	public PropertyChangeListener createActionChangeListener(
			AbstractButton button) {
		return new ActionChangedListener(button);
	}

	/**
	 * Updates <code>button</code> based on property change events.
	 */
	protected class ActionChangedListener implements PropertyChangeListener {

		/**
		 * References the button that is to be updated.
		 */
		AbstractButton button;

		/**
		 * Constructs a action changed listener for the specified button.
		 * 
		 * @param button
		 *            The button to create the listener for.
		 */
		ActionChangedListener(AbstractButton button) {
			this.button = button;
		}

		/**
		 * Updates the button state based on changes of the action state.
		 */
		public void propertyChange(PropertyChangeEvent e) {
			String propertyName = e.getPropertyName();
			if (propertyName.equals("enabled")) {
				Boolean enabledState = (Boolean) e.getNewValue();
				button.setEnabled(enabledState.booleanValue());
			} else if (propertyName.equals(RMAppAction.PROPERTY_ISSELECTED)) {
				Boolean selectedState = (Boolean) e.getNewValue();
				if (selectedState != null) {
					boolean selected = selectedState.booleanValue();
					if (button instanceof JCheckBoxMenuItem)
						((JCheckBoxMenuItem) button).setState(selected);
					else if (button instanceof JRadioButtonMenuItem)
						((JRadioButtonMenuItem) button).setSelected(selected);
					else if (button instanceof JToggleButton)
						((JToggleButton) button).setSelected(selected);
				}
			}
		}
	}

	public BasicMarqueeHandler createMarqueeHandler() {
		return new RMMarqueeHandler(RMAppContext.getGraph());
	}

	public Object createStatusBar() {
		return RMAppStatusBar.getInstance();
	}

	public JPanel createMainPanel() {
		RMAppMainPanel appMainPanel = new RMAppMainPanel();
		// appMainPanel.setFactory(factory);
		// appMainPanel.app = app;
		return appMainPanel.getInstance();
	}

	public void configRMModel() {

		DefaultMutableTreeNode root = (DefaultMutableTreeNode) RMAppContext
				.get(RMAppConst.RM_COMPONENT);

		int rootSize = root.getChildCount();

		for (int i = 0; i < rootSize; i++) {
			DefaultMutableTreeNode modelRoot = (DefaultMutableTreeNode) root
					.getChildAt(i);
			TreeModel treeModel = new DefaultTreeModel(modelRoot);

			if (RMAppConst.RM_MODEL_TREE.equals(modelRoot.getUserObject())) {
				RMAppContext.set(RMAppConst.RM_MODEL_TREE, buildJTree(
						treeModel, false));
			} else if (RMAppConst.RM_DATA_TREE
					.equals(modelRoot.getUserObject())) {
				RMAppContext.set(RMAppConst.RM_DATA_TREE, buildJTree(treeModel,
						true));
			}

		}

	}

	public void configRMMyFlow() {

		String rmFlowPath = RMAppContext.rmFlowPath;
		File file = new File(rmFlowPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		RMAppContext.set(RMAppConst.RM_FLOW_TREE, RMFlowTreeHelper
				.genRMFlowTree(rmFlowPath));

	}

	// public void configRMCompanyFlow() {
	// String fileName = RMAppContext.rmCompanyFlowPath;
	// RMAppContext.set(RMAppConst.RM_COMPANY_FLOW_TREE,
	// RMFlowTreeHelper.genRMFlowTree(fileName));
	// }

	private JTree buildJTree(TreeModel treeModel, boolean showRoot) {
		JTree tree = new JTree(treeModel);
		tree.setCellRenderer(new RMTreeCellRenderer());
		// tree.setRootVisible(false);
		tree.setDragEnabled(true);

		tree
				.addMouseMotionListener((MouseMotionListener) RMAppContext.rmGraphListener);
		return tree;
	}

	public void initRMAppSettings(RMAppDocumentBuilder appSettings) {
		appSettings = (RMAppDocumentBuilder) RMAppContext
				.get(RMAppConst.RM_APP_SETTINGS);
		try {
			appSettings.add(NAME_UICONFIG, RMAppDocumentBuilder.parse(RMResources
					.getInputStream(PATH_UICONFIG)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final String NAME_UICONFIG = "ui";

	private static final String PATH_UICONFIG = "com/rm/config/ui.xml";

	private static final String NODENAME_MENUBAR = "menubar";

	private static final String NODENAME_TOOLBAR = "toolbar";

	public void init() {
		RMAppDocumentBuilder appSettings = (RMAppDocumentBuilder) RMAppContext
				.get(RMAppConst.RM_APP_SETTINGS);
		initRMAppSettings(appSettings);

		NodeList nodeList = appSettings.getDocument(NAME_UICONFIG)
				.getDocumentElement().getChildNodes();
		Node menuBarConfiguration = RMAppDocumentBuilder.getNodeByName(nodeList,
				NODENAME_MENUBAR);
		Node toolBarConfiguration = RMAppDocumentBuilder.getNodeByName(nodeList,
				NODENAME_TOOLBAR);
		RMAppContext.set(RMAppConst.RM_MENUBAR,
				createMenuBar(menuBarConfiguration));
		RMAppContext.set(RMAppConst.RM_TOOLBAR,
				createToolBar(toolBarConfiguration));
		RMAppContext.set(RMAppConst.RM_STATUSBAR, createStatusBar());
		configRMModel();
		configRMMyFlow();
		// configRMCompanyFlow();
		// RMAppContext.set(RMAppConst.RM_COMPONENT_TREE, createRMTree());
		RMAppContext.set(RMAppConst.RM_LOG_CONSOLE_PANE,
				createConsolePropertyPane(JTabbedPane.TOP));
		RMAppContext.set(RMAppConst.RM_MARQUEE_HANDLER, createMarqueeHandler());
		RMAppContext.set(RMAppConst.RM_MAIN_PANEL, createMainPanel());

	}

}
