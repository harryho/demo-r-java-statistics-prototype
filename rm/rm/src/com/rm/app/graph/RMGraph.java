package com.rm.app.graph;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.VertexView;

import com.rm.app.RMAppContext;
import com.rm.app.log.RMLogger;
import com.rm.app.r.component.data.RDataComponent;

public class RMGraph extends JGraph implements Serializable {

	private static final long serialVersionUID = 1L;

	private String referXML;

	private boolean save = true;

	protected RMGraphEdge activeEdge = null;

	// Construct the Graph using the Model as its Data Source
	public RMGraph(GraphModel model) {
		this(model, null);
	}

	// Construct the Graph using the Model as its Data Source
	public RMGraph(GraphModel model, GraphLayoutCache cache) {
		super(model, cache);
		// Make Ports Visible by Default
		setPortsVisible(true);
		// Use the Grid (but don't make it Visible)
		setGridEnabled(false);
		// Set the Grid Size to 10 Pixel
		setGridSize(6);
		// Set the Tolerance to 2 Pixel
		setTolerance(2);
		// Accept edits if click on background
		setInvokesStopCellEditing(true);
		// Allows control-drag
		setCloneable(true);
		// Jump to default port on connect
		setJumpToDefaultPort(true);

		this.addComponentListener(new DefaultGraphComponentListener());
		this.addMouseMotionListener(new MyMouseMotionListener());
		this.addMouseListener(new MyMouseListener());
		this.addKeyListener(new MyKeyListener());
	}

	public List<RMGraphCell> getCurrentDataComponents() {
		List<RMGraphCell> imp = getComponents(RDataComponent.TYPE_DATA_MODEL_IMP);
		List<RMGraphCell> sel = getComponents(RDataComponent.TYPE_DATA_MODEL_SEL);
		imp.addAll(sel);
		return imp;
	}

	/**
	 * 
	 * @return
	 */
	public List<RMGraphCell> getComponents(String key) {
		CellView[] cv = getGraphLayoutCache().getCellViews();
		List<RMGraphCell> ret = new ArrayList<RMGraphCell>();
		int k = 0;
		for (int i = 0; i < cv.length; i++) {
			Object object = cv[i];
			if (object instanceof VertexView) {
				Object obj = ((VertexView) object).getCell();
				RMGraphCell cell = (RMGraphCell) obj;
				RMLogger.debug(cell.getKey());
				if (key.equals(cell.getKey())) {
					ret.add(cell);
				}
			}
		}
		return ret;
	}

	/**
	 * 
	 * @return the refering XML document
	 */
	public String getReferXML() {
		return referXML;
	}

	public void setReferXML(String referXML) {
		this.referXML = referXML;
	}

	/**
	 * 
	 * @return whether the graph has been saved
	 */
	public boolean isSaved() {
		return this.save;
	}

	/**
	 * 
	 * @param isSaved
	 */
	public void setIsSaved(boolean isSaved) {
		this.save = isSaved;
	}

	/**
	 * 
	 * @return whether existed component on graph
	 */
	public boolean isEmpty() {
		CellView[] views = this.getGraphLayoutCache().getAllViews();
		return (views == null || views.length == 0);
	}

	private class DefaultGraphComponentListener implements ComponentListener {

		protected DefaultGraphComponentListener() {

		}

		public void componentHidden(ComponentEvent e) {
			RMAppContext.getGraph().setIsSaved(false);
		}

		public void componentMoved(ComponentEvent e) {

			RMAppContext.getGraph().setIsSaved(false);

		}

		public void componentResized(ComponentEvent e) {

		}

		public void componentShown(ComponentEvent e) {

			RMAppContext.getGraph().setIsSaved(false);
		}

	}

	private class MyMouseMotionListener implements MouseMotionListener {

		public void mouseDragged(MouseEvent e) {

			RMGraph graph = RMAppContext.getGraph();
			Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());

			if (cell != null && cell instanceof RMGraphCell) {
				RMGraphCell ncell = (RMGraphCell) cell;
				ncell.setPosX(new Double(e.getX()));
				ncell.setPosY(new Double(e.getY()));
				graph.setIsSaved(false);
			} else if (cell != null && cell instanceof RMGraphEdge) {
				activeEdge = (RMGraphEdge) cell;
			}
		}

		public void mouseMoved(MouseEvent e) {
		}

	}

	public class MyMouseListener implements MouseListener {

		public void mousePressed(MouseEvent e) {
			RMGraph graph = RMAppContext.getGraph();
			Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());

			if (cell != null && cell instanceof RMGraphEdge) {
				activeEdge = (RMGraphEdge) cell;
				// activeEdge.
			}
		}

		public void mouseReleased(MouseEvent e) {
			RMGraph graph = RMAppContext.getGraph();
			Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());

			if (cell != null && cell instanceof RMGraphCell) {
				RMGraphCell ncell = (RMGraphCell) cell;
				// ncell.setPosX(new Double(e.getX()));
				// ncell.setPosY(new Double(e.getY()));
				if (activeEdge != null) {

					Object sObj = activeEdge.getSource();
					Object tObj = activeEdge.getTarget();
					if (null != sObj && null != tObj) {
						RMGraphCell sourceCell = (RMGraphCell) ((DefaultPort) sObj)
								.getParent();
						RMGraphCell targetCell = (RMGraphCell) ((DefaultPort) tObj)
								.getParent();
						if (ncell.getId().equals(sourceCell.getId())) {
							activeEdge.setSourceType(ncell.getClassifierKey());
							activeEdge.setSourceId(ncell.getId());
						} else if (ncell.getId().equals(targetCell.getId())) {
							activeEdge.setTargetType(ncell.getClassifierKey());
							activeEdge.setTargetId(ncell.getId());
						}
					}
					activeEdge = null;
				}
				graph.setIsSaved(false);
			} else {
				activeEdge = null;
			}
		}

		public void mouseClicked(MouseEvent e) {
			RMGraph graph = RMAppContext.getGraph();
			Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());
			if (cell != null && cell instanceof RMGraphEdge) {
				activeEdge = (RMGraphEdge) cell;
			}
		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}

	}

	private class MyKeyListener implements KeyListener {

		public void keyPressed(KeyEvent e) {
			int id = e.getID();
			String keyString;
			if (id == KeyEvent.KEY_TYPED) {
				char c = e.getKeyChar();
				keyString = String.valueOf(c);
			} else {
				int keyCode = e.getKeyCode();
				keyString = KeyEvent.getKeyText(keyCode);
			}

			if ("delete".equals(keyString.toLowerCase())) {

				RMGraph graph = RMAppContext.getGraph();
				Object cell = graph.getSelectionCell();
				if (cell != null) {
					graph.setIsSaved(false);
				}
			}

		}

		public void keyReleased(KeyEvent e) {

		}

		public void keyTyped(KeyEvent e) {

		}

	}
}
