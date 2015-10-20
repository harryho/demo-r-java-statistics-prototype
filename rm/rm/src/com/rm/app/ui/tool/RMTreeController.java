package com.rm.app.ui.tool;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import com.rm.app.RMAppConst;
import com.rm.app.RMAppContext;
import com.rm.app.log.RMLogger;

public class RMTreeController { // extends TreeDataLocator implements
    // TreeController

    public RMTreeController() {
	// this.setNodeNameAttribute("description");
    }

    public TreeModel getTreeModel() {

	// DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	// Map componentModel = (Map) RMAppContext.get(RMAppConst.RM_COMPONENT);
	DefaultMutableTreeNode root = (DefaultMutableTreeNode) RMAppContext.get(RMAppConst.RM_COMPONENT);
	// getTree(root, componentModel);
	RMLogger.debug("            &&&&&&&&&&&&&&&&&&&&&&&& " + root);
	RMLogger.debug("            &&&&&&&&&&&&&&&&&&&&&&&& " + root.getChildCount());

	DefaultTreeModel model = new DefaultTreeModel(root.getRoot());
	
	return model;
    }

//    private void getTree(DefaultMutableTreeNode node, Object componentModel) {
//	// TODO Auto-generated method stub
//	Logger.debug("   " + componentModel.getClass());
//	if (componentModel instanceof HashMap) {
//	    Map map = (Map) componentModel;
//	    Set keySet = map.keySet();
//	    Iterator iterator = keySet.iterator();
//	    Logger.debug(keySet + "              " + iterator);
//	    while (iterator.hasNext()) {
//		String key = (String) iterator.next();
//
//		Object value = map.get(key);
//		// DefaultMutableTreeNode subNode
//		DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(RMResources.getString(key
//			+ RMResources.SUFFIX_LABEL));
//		getTree(subNode, value);
//		// String i =RMResources.getString(key+
//		// RMResources.SUFFIX_INDEX);
//		// if(null!=i){
//		// int index = Integer.valueOf(i);
//		// Logger.debug(" >>>>>>>>>>>>>>>"+ index);
//		// node.insert(subNode, index);
//		// }else
//		node.add(subNode);
//
//	    }
//
//	} else if (componentModel instanceof ArrayList) {
//
//	    ArrayList list = (ArrayList) componentModel;
//	    for (int i = 0; i < list.size(); i++) {
//		RMGraphCell cell = (RMGraphCell) list.get(i);
//		node.add(cell);
//	    }
//	} else if (componentModel instanceof RMGraphCell) {
//	    RMGraphCell cell = (RMGraphCell) componentModel;
//	    node.add(cell);
//	}
//
//	// node.add(cell);
//
//    }

    /**
     * doubleClick
     * 
     * @param node
     *            DefaultMutableTreeNode
     */
    public void doubleClick(DefaultMutableTreeNode node) {
    }

    /**
     * leftClick
     * 
     * @param node
     *            DefaultMutableTreeNode
     */
    public void leftClick(DefaultMutableTreeNode node) {
    }

    /**
     * rightClick
     * 
     * @param node
     *            DefaultMutableTreeNode
     * @return boolean
     */
    public boolean rightClick(DefaultMutableTreeNode node) {
	return true;
    }

}
