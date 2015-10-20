package com.rm.app.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rm.app.RMAppConst;
import com.rm.app.RMAppContext;
import com.rm.app.RMResources;
import com.rm.app.log.RMLogger;
import com.rm.app.ui.tool.RMAppDocumentBuilder;
import com.rm.app.util.RMAppFocusManager;

public class RMGraphFactory {

    public final static String RESEARCH_FLOW_GROUP = "RESEARCH_FLOW_GROUP";

    public final static String DATA_GROUP = "DATA_GROUP";

    public final static String RESEARCH_FLOW_COMPONENT = "RESEARCH_FLOW_COMPONENT";

    public final static String DATA_COMPONENT = "DATA_COMPONENT";

    public final static String NODENAME_RM = "rm";

    public final static String NODENAME_COMPONENTMODEL = "componentmodel";

    public final static String NODENAME_COMPONENTS = "components";

    public final static String NODENAME_COMPONENT = "component";

    public final static String DOT = ".";

    public final static String COMMA = ",";

    public static final String PATH_RMCONFIG = "com/rm/config/rm.xml";

    private static final String NAME_RMCONFIG = "rmconfig";

    private static final String NODENAME_GROUP = "group";

    public static final String ATTRIBUTENAME = "att";
    
    public static final String OPTIONENAME = "option";


    public void init() {
	initialSettings();
	RMAppContext.set(RMAppConst.RM_GRAPH, getRMGraph());

    }

    public RMGraph getRMGraph() {
	RMGraph graph = null;
	GraphModel model = new RMModel();
	RMCellViewFactory cellViewFactory = (RMCellViewFactory) RMAppContext.get(RMAppConst.RM_GRAPH_CELL_VIEW_FACTORY);
	if (graph == null) {
	    GraphLayoutCache layoutCache = new GraphLayoutCache(model, cellViewFactory, true);
	    Set locals = new HashSet();
	    locals.add(GraphConstants.BOUNDS);
	    layoutCache.setLocalAttributes(locals);

	    graph = new com.rm.app.graph.RMGraph(model, layoutCache);

	    // cellViewFactory.

	    graph.getGraphLayoutCache().setFactory(cellViewFactory);

	    graph.setMarqueeHandler(new RMMarqueeHandler(graph));
	    graph.setPortsVisible(true);
	    DropTarget dt = new DropTarget(graph, (DropTargetListener) RMAppContext.rmGraphListener);
	    // graph.addMouseListener((MouseListener)
	    // RMAppContext.rmGraphListener);
	    // graph.addMouseMotionListener((MouseMotionListener)
	    // RMAppContext.rmGraphListener);
	    graph.setVisible(true);
	    // graph.setPreferredSize(new Dimension(500, 350));

	    RMAppFocusManager graphFocusManager = RMAppFocusManager.currentGraphFocusManager;
	    graphFocusManager.setFocusedGraph(graph);
	    graph.setMinimumSize(new Dimension(800, 400));
	    // graph.setDropTarget(dt);

	    // graph.addKeyListener((KeyListener)
	    // settings.getRMSetting(RMConfigSettings.RM_APP));
	    // graph.addGraphSelectionListener((GraphSelectionListener)
	    // settings.getRMSetting(RMConfigSettings.RM_APP));
	    // graph = graph;
	    // graph.setPortsOnTop(true);
	    // graph.setPortsVisible(true);
	    // graph.getGraphLayoutCache().getFactory().createView(arg0, arg1)

	    // graph.set
	}
	// return RMGraph.getInstance();

	return graph;
    }

    private void initRMAppSettings(RMAppDocumentBuilder appSettings) {
	try {
	    appSettings.add(NAME_RMCONFIG, RMAppDocumentBuilder.parse(RMResources.getInputStream(PATH_RMCONFIG)));
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public Object configureRMComponents(Node configuration) {
	HashMap componentModel = new HashMap();
	DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Root");
	configureRMComponents(componentModel, configuration, treeNode);
	return treeNode;
    }

    private void configureRMComponents(Object componentModel, Node configuration, DefaultMutableTreeNode node) {
	if (configuration != null) {
	    String key = RMAppDocumentBuilder.getAttributeValue(configuration, RMAppDocumentBuilder.ATTRIBUTENAME_KEY);
	    NodeList nodes = configuration.getChildNodes();

	    for (int i = 0; i < nodes.getLength(); i++) {
		Node child = nodes.item(i);
		String name = child.getNodeName();

		if (NODENAME_COMPONENTMODEL.equals(name)) {

		    Object components = ((Map) componentModel).get(key);
		    if (null == components)
			components = new HashMap();

		    DefaultMutableTreeNode subSubNode = new DefaultMutableTreeNode(RMResources
			    .getString(RMAppDocumentBuilder.getAttributeValue(child,
				    RMAppDocumentBuilder.ATTRIBUTENAME_KEY)
				    + RMResources.SUFFIX_LABEL));
		    configureRMComponents(components, child, subSubNode);
		    ((HashMap) componentModel).put(key, components);
		    node.add(subSubNode);

		} else if (NODENAME_COMPONENTS.equals(name)) {

		    Object components = ((Map) componentModel).get(key);
		    if (null == components)
			components = new HashMap();

		    DefaultMutableTreeNode subSubNode = new DefaultMutableTreeNode(RMResources
			    .getString(RMAppDocumentBuilder.getAttributeValue(child,
				    RMAppDocumentBuilder.ATTRIBUTENAME_KEY)
				    + RMResources.SUFFIX_LABEL));

		    configureRMComponents(components, child, subSubNode);
		    ((HashMap) componentModel).put(key, components);
		    node.add(subSubNode);

		} else if (NODENAME_COMPONENT.equals(name)) {
                    String init = RMAppDocumentBuilder.getAttributeValue(child, RMAppDocumentBuilder.ATTRIBUTENAME_INIT);
                    if("N".equals(init))
                	continue;
		    RMGraphCell cell = createCell(child, configuration);
		    RMLogger.debug(cell);
		    if (cell != null) {
			((HashMap) componentModel).put(key, cell);
			node.add(cell);
		    }
		} else if (NODENAME_GROUP.equals(name)) {
		    List components = new ArrayList();

		    NodeList nodeList = child.getChildNodes();

		    for (int k = 0; k < nodeList.getLength(); k++) {
			Node groupChild = nodeList.item(k);
			if (NODENAME_COMPONENT.equals(groupChild.getNodeName())) {
			    String init = RMAppDocumentBuilder.getAttributeValue(groupChild, RMAppDocumentBuilder.ATTRIBUTENAME_INIT);
	                    if("N".equals(init))
	                	continue;
			    RMGraphCell cell = createCell(groupChild, configuration);
			    // cell.setAllowsChildren(false);
			    RMLogger.debug(cell);
			    if (cell != null) {
				components.add(cell);
				// subNode.add(cell);
				node.add(cell);
			    }
			}
		    }
		}
	    }
	}
    }

    private RMGraphCell createCell(Node node, Node parent) {
	String key = RMAppDocumentBuilder.getAttributeValue(node, RMAppDocumentBuilder.ATTRIBUTENAME_KEY);
	String classifierKey = RMAppDocumentBuilder.getAttributeValue(parent, RMAppDocumentBuilder.ATTRIBUTENAME_KEY);
	String label = RMResources.getString(key + RMResources.SUFFIX_LABEL);
	String name = RMResources.getString(key + RMResources.SUFFIX_NAME);
	String viewLabel = RMResources.getString(key + RMResources.SUFFIX_VIEWLABEL);
	String fromDataCache = RMResources.getString(key + RMResources.SUFFIX_FROMDATACACHE);

	String icon = RMResources.getString(key + RMResources.SUFFIX_ICON);
	String width = RMResources.getString(key + RMResources.SUFFIX_WIDTH);
	String height = RMResources.getString(key + RMResources.SUFFIX_HEIGHT);

	// Construct Vertex with no Label
	RMGraphCell vertex = createDefaultGraphCell(label);

	vertex.setName(name);
	// Create a Map that holds the attributes for the Vertex
	vertex.getAttributes().applyMap(
		createCellAttributes(new Point(20, 20), null, icon, Integer.valueOf(width).intValue(), Integer.valueOf(
			height).intValue()));
	vertex.setAllowsChildren(false);

	NodeList nodeList = node.getChildNodes();
	Map<String, Attribute> map = new HashMap<String, Attribute>();

	for (int k = 0; k < nodeList.getLength(); k++) {
	    Node subNode = nodeList.item(k);

	    if (subNode.getNodeName().equals(ATTRIBUTENAME)) {

		Attribute attribute = vertex.newAttribute();

		attribute.setDisplayOrder(k);

		attribute.setId(RMAppDocumentBuilder.getAttributeValue(subNode, RMAppDocumentBuilder.ATTRIBUTENAME_ID));

		attribute.setName(RMAppDocumentBuilder.getAttributeValue(subNode,
			RMAppDocumentBuilder.ATTRIBUTENAME_NAME));

		attribute.setEditable(Boolean.parseBoolean(RMAppDocumentBuilder.getAttributeValue(subNode,
			RMAppDocumentBuilder.ATTRIBUTENAME_EDITABLE)));

		attribute.setVisible(Boolean.parseBoolean(RMAppDocumentBuilder.getAttributeValue(subNode,
			RMAppDocumentBuilder.ATTRIBUTENAME_VISIBLE)));

		attribute.setValue(RMAppDocumentBuilder.getAttributeValue(subNode,
			RMAppDocumentBuilder.ATTRIBUTENAME_VALUE));

		attribute.setDiscription(RMAppDocumentBuilder.getAttributeValue(subNode,
			RMAppDocumentBuilder.ATTRIBUTENAME_DISCRIPTION));

		attribute.setType(RMAppDocumentBuilder.getAttributeValue(subNode,
			RMAppDocumentBuilder.ATTRIBUTENAME_TYPE));		
		
		if(!attribute.isVisible())
		    continue;

		map.put(attribute.getId(), attribute);
	    }
	}

	vertex.setKey(key);
	vertex.setClassifierKey(classifierKey);
	vertex.setDataMap(map);
	vertex.setViewLabel(viewLabel);
	vertex.setFromDataCache(fromDataCache);
		
	return vertex;
    }

    public void insert(Point2D point, JGraph graph, String name, String icon, int width, int height) {
	// Construct Vertex with no Label
	DefaultGraphCell vertex = createDefaultGraphCell(name);
	// Create a Map that holds the attributes for the Vertex
	vertex.getAttributes().applyMap(createCellAttributes(point, graph, icon, width, height));
	// Insert the Vertex (including child port and attributes)
	graph.getGraphLayoutCache().insert(vertex);
    }

    public RMGraphCell createCell(Point2D point, String name, String jpg, int width, int height) {
	// Construct Vertex with no Label
	RMGraphCell vertex = createDefaultGraphCell(name);
	// Create a Map that holds the attributes for the Vertex
	vertex.getAttributes().applyMap(createCellAttributes(point, null, jpg, width, height));
	vertex.setAllowsChildren(false);
	return vertex;
    }

    public Map createCellAttributes(Point2D point, JGraph graph, String img, int width, int height) {
	Map map = new Hashtable();
	// Snap the Point to the Grid
	if (graph != null) {
	    point = graph.snap((Point2D) point.clone());
	} else {
	    point = (Point2D) point.clone();
	}
	// Add a Bounds Attribute to the Map
	GraphConstants.setBounds(map, new Rectangle2D.Double(point.getX(), point.getY(), 0, 0));
	// Make sure the cell is resized on insert
	GraphConstants.setResize(map, true);
	// Add a nice looking gradient background
	GraphConstants.setGradientColor(map, Color.WHITE);
	// Add a Border Color Attribute to the Map
	GraphConstants.setBorderColor(map, Color.WHITE);
	// Add a White Background
	GraphConstants.setBackground(map, Color.white);
	// Make Vertex Opaque
	GraphConstants.setOpaque(map, true);

	URL url = null;
	url = getClass().getClassLoader().getResource(img);
	ImageIcon analysisIcon = new ImageIcon(url);
	GraphConstants.setIcon(map, analysisIcon);
	return map;
    }

    protected RMGraphCell createDefaultGraphCell(String desc) {
	RMGraphCell cell = new RMGraphCell(desc);

	cell.addPort();
	return cell;
    }

    public void initialSettings() {

	RMAppContext.set(RMAppConst.RM_GRAPH_CELL_VIEW_FACTORY, new RMCellViewFactory());
	RMAppDocumentBuilder appSettings = (RMAppDocumentBuilder) RMAppContext.get(RMAppConst.RM_APP_SETTINGS);
	initRMAppSettings(appSettings);
	Node compModelConfiguration = appSettings.getDocument(NAME_RMCONFIG).getDocumentElement();
	RMAppContext.set(RMAppConst.RM_COMPONENT, configureRMComponents(compModelConfiguration));

    }
}
