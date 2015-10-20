package com.rm.app.util;

import java.awt.Point;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

import com.rm.app.RMAppContext;
import com.rm.app.graph.RMGraphCell;
import com.rm.app.log.RMLogger;

public class RMGraphDropTargetAdapter implements DropTargetListener, MouseMotionListener {

    public void drop(DropTargetDropEvent e) {
	if (RMAppContext.cell != null && RMAppContext.treeDragged) {
	    JGraph graph = RMAppContext.getGraph();

	    long seq = System.currentTimeMillis();
	    RMAppContext.cell.setId(  RMAppContext.cell.getName()+"_"+Long.toString(seq));	    
	    graph.getGraphLayoutCache().insert(RMAppContext.cell);
	    RMAppContext.treeDragged = false;
	    RMAppContext.cell = null;
	    RMAppContext.activeGraph = graph;
	}
    }

    public void dragEnter(DropTargetDragEvent e) {

    }

    public void dragExit(java.awt.dnd.DropTargetEvent e) {
    };

    public void dropActionChanged(DropTargetDragEvent e) {

    }

    public void dragOver(DropTargetDragEvent e) {

	if (RMAppContext.cell != null && RMAppContext.treeDragged) {

	    AttributeMap am = RMAppContext.cell.getAttributes();

	    Point p = e.getLocation();
	    GraphConstants.setBounds(am, new Rectangle2D.Double(p.getX(), p.getY(), 0, 0));
	    RMAppContext.cell.setAttributes(am);
	    RMAppContext.cell.setPosX(new Double(p.getX()));
	    RMAppContext.cell.setPosY(new Double(p.getY()));

	}

    }

    public void mouseDragged(MouseEvent e) {
	if (e.getSource() instanceof JTree) {

	    JTree tree = (JTree) e.getSource();
	    int selRow = tree.getRowForLocation(e.getX(), e.getY());
	    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
	    DefaultMutableTreeNode nodel = null;
	    if (null != selPath) {
		nodel = selPath.getLastPathComponent() != null ? (DefaultMutableTreeNode) selPath
			.getLastPathComponent() : null;

		RMLogger.debug("  last comp class " + nodel);
	    }
	    if (selRow > 0 && nodel != null && nodel.isLeaf() && (nodel instanceof RMGraphCell)) {
		RMLogger.debug("  is leaf >>>  " + nodel.isLeaf() + "  " + nodel.getChildCount() + "  "
			+ nodel.getUserObject());
		RMAppContext.treeDragged = true;
		

		
		RMGraphCell tmp= (RMGraphCell)nodel;
		 RMAppContext.cell =(RMGraphCell)tmp.clone();
		
		RMAppContext.cell.setAllowsChildren(true);
		RMAppContext.cell.addPort();

		AttributeMap am = RMAppContext.cell.getAttributes();
//		RMLogger.debug(RMAppContext.cell.getUserObject().getClass() + "    " + RMAppContext.cell.getUserObject());
		RMLogger.debug(am);
		RMAppContext.cell.setUserObject(RMAppContext.cell.getViewLabel());
		e.setSource(RMAppContext.cell);
		RMAppContext.getGraph().setIsSaved(false);
	    }
	} else {
	    JGraph graph = RMAppContext.getGraph();
	    graph.getMarqueeHandler().mouseDragged(e);
	    RMAppContext.getGraph().setIsSaved(false);
	}

    }

    public void mouseMoved(MouseEvent e) {

	// if (RMAppContext.cell != null && RMAppContext.treeDragged) {
	// AttributeMap am = RMAppContext.cell.getAttributes();
	//
	// Logger.debug(" source " + e.getSource());
	// Logger.debug(" moved >>>> " + e.getX() + " , " + e.getY());
	// GraphConstants.setBounds(am, new Rectangle2D.Double(e.getX(),
	// e.getY(), 0, 0));
	// RMAppContext.cell.setAttributes(am);
	// // mouseReleased(e);
	// }
    }

}
