package com.rm.app.graph;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

import com.rm.app.RMAppContext;
import com.rm.app.ui.action.RMAppEditAction;
import com.rm.app.ui.action.RMAppRMAction;

public class RMMarqueeHandler extends BasicMarqueeHandler {

	// Holds the Start and the Current Point
	protected Point2D start, current;

	// Holds the First and the Current Port
	protected PortView port, firstPort;

	// Holds the Source and Target Cell
	protected RMGraphCell source, target;

	protected JGraph graph;
	
	private static String[] compsToBeExam = {"equation.mr","equation.glm"};

	public RMMarqueeHandler(JGraph graph) {
		this.graph = graph;
	}

	// Override to Gain Control (for PopupMenu and ConnectMode)
	public boolean isForceMarqueeEvent(MouseEvent e) {

		Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());

		if (e.isShiftDown())
			return false;

		// If Right Mouse Button we want to Display the PopupMenu
		if (SwingUtilities.isRightMouseButton(e))
			return true;

		if (cell != null && cell instanceof RMGraphEdge)
			return false;
		// Find and Remember Port
		port = getSourcePortAt(e.getPoint());
		// If Port Found and in ConnectMode (=Ports Visible)
		if (port != null && graph.isPortsVisible())
			return true;
		// Else Call Superclass
		return super.isForceMarqueeEvent(e);
	}

	// Display PopupMenu or Remember Start Location and First Port
	public void mousePressed(final MouseEvent e) {
		// If Right Mouse Button
		if (SwingUtilities.isRightMouseButton(e)) {
			// Find Cell in Model Coordinates
			Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());

//			if(RMAppContext.getGraph().getSelectionCount()>1){
				
			    // Create PopupMenu for the Cell
				  JPopupMenu menu = createPopupMenu(e.getPoint(), cell);
				  // Display PopupMenu
				  menu.show(graph, e.getX(), e.getY());
		
			// Else if in ConnectMode and Remembered Port is Valid
		} else if (port != null && graph.isPortsVisible()) {
			// Remember Start Location
			start = graph.toScreen(port.getLocation());
			// Remember First Port
			firstPort = port;

			source = (RMGraphCell) graph.getFirstCellForLocation(port
					.getLocation().getX(), port.getLocation().getY());
			// source =
		} else {
			// Call Superclass
			super.mousePressed(e);
		}
	}

	// Find Port under Mouse and Repaint Connector
	public void mouseDragged(MouseEvent e) {
		// If remembered Start Point is Valid
		if (start != null) {
			// Fetch Graphics from Graph
			Graphics g = graph.getGraphics();
			// Reset Remembered Port
			PortView newPort = getTargetPortAt(e.getPoint());
			// Do not flicker (repaint only on real changes)
			if (newPort == null || newPort != port) {
				// Xor-Paint the old Connector (Hide old Connector)
				paintConnector(Color.black, graph.getBackground(), g);
				// If Port was found then Point to Port Location
				port = newPort;
				if (port != null) {
					current = graph.toScreen(port.getLocation());

					target = (RMGraphCell) graph.getFirstCellForLocation(port
							.getLocation().getX(), port.getLocation().getY());
				}
				// Else If no Port was found then Point to Mouse Location
				else {
					current = graph.snap(e.getPoint());
				}
				// Xor-Paint the new Connector
				paintConnector(graph.getBackground(), Color.black, g);
			}
		}
		// Call Superclass
		super.mouseDragged(e);
	}

	public PortView getSourcePortAt(Point2D point) {
		// Disable jumping
		graph.setJumpToDefaultPort(false);
		PortView result;
		try {
			// Find a Port View in Model Coordinates and Remember
			result = graph.getPortViewAt(point.getX(), point.getY());
		} finally {
			graph.setJumpToDefaultPort(true);
		}
		return result;
	}

	// Find a Cell at point and Return its first Port as a PortView
	protected PortView getTargetPortAt(Point2D point) {
		// Find a Port View in Model Coordinates and Remember
		return graph.getPortViewAt(point.getX(), point.getY());
	}

	// Connect the First Port and the Current Port in the Graph or Repaint
	public void mouseReleased(MouseEvent e) {
		if (e == null) {
			return;
		}
		// If Valid Event, Current and First Port
		if (e != null && port != null && firstPort != null && firstPort != port) {
			// Then Establish Connection
			// connect((RMGraphCell)((Port) firstPort.getCell()), (RMGraphCell)
			// port.getCell());
			boolean isExisted = false;
			Port oriPort = (Port) firstPort.getCell();

			Iterator iterator = oriPort.edges();

			Object object = graph.getFirstCellForLocation(e.getX(), e.getY());
			RMGraphCell cell = null;
			if (object != null && object instanceof RMGraphCell)
				cell = (RMGraphCell) object;

			while (iterator.hasNext()) {
				RMGraphEdge edge = (RMGraphEdge) iterator.next();
				if (cell != null && edge.getTargetId().equals(cell.getId())) {
					isExisted = true;
					break;
				}
			}
			if (!isExisted) {
				connect((Port) firstPort.getCell(), (Port) port.getCell());
			} else {
				Graphics g = graph.getGraphics();
				paintConnector(graph.getBackground(), Color.black, g);
			}
			e.consume();
			// }
			// Else Repaint the Graph
		} else
			graph.repaint();
		// Reset Global Vars
		firstPort = port = null;
		start = current = null;
		source = target = null;
		// Call Superclass
		super.mouseReleased(e);
	}

	// Show Special Cursor if Over Port
	public void mouseMoved(MouseEvent e) {
		// Check Mode and Find Port
		if (e != null && getSourcePortAt(e.getPoint()) != null
				&& graph.isPortsVisible()) {
			// Set Cusor on Graph (Automatically Reset)
			graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
			// Consume Event
			// Note: This is to signal the BasicGraphUI's
			// MouseHandle to stop further event processing.
			e.consume();
		} else {
			// Call Superclass
			super.mouseMoved(e);
		}
	}

	// Use Xor-Mode on Graphics to Paint Connector
	protected void paintConnector(Color fg, Color bg, Graphics g) {

		// Set Foreground
		g.setColor(fg);
		// Set Xor-Mode Color
		g.setXORMode(bg);
		// Highlight the Current Port
		paintPort(graph.getGraphics());
		// If Valid First Port, Start and Current Point
		if (firstPort != null && start != null && current != null) {
			// Then Draw A Line From Start to Current Point
			g.drawLine((int) start.getX(), (int) start.getY(), (int) current
					.getX(), (int) current.getY());

		}
		RMAppContext.getGraph().setIsSaved(false);
	}

	// Use the Preview Flag to Draw a Highlighted Port
	protected void paintPort(Graphics g) {
		// If Current Port is Valid
		if (port != null) {
			// If Not Floating Port...
			boolean o = (GraphConstants.getOffset(port.getAllAttributes()) != null);
			// ...Then use Parent's Bounds
			Rectangle2D r = (o) ? port.getBounds() : port.getParentView()
					.getBounds();
			// Scale from Model to Screen
			r = graph.toScreen((Rectangle2D) r.clone());
			// Add Space For the Highlight Border
			r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r
					.getHeight() + 6);
			// Paint Port in Preview (=Highlight) Mode
			graph.getUI().paintCell(g, port, r, true);
		}
	}

	public void connect(Port source, Port target) {
		// Construct Edge with no label
		RMGraphEdge edge = createDefaultEdge();

		if (graph.getModel().acceptsSource(edge, source)
				&& graph.getModel().acceptsTarget(edge, target)) {

			edge.setSourceId(this.source.getId());
			edge.setTargetId(this.target.getId());
			edge.setSourceType(this.source.getClassifierKey());
			edge.setTargetType(this.target.getClassifierKey());
			// Create a Map thath holds the attributes for the edge
			edge.getAttributes().applyMap(createEdgeAttributes());
			// Insert the Edge and its Attributes
			graph.getGraphLayoutCache().insertEdge(edge, source, target);
			RMAppContext.getGraph().setIsSaved(false);
		}
	}

	public Map createEdgeAttributes() {
		Map map = new Hashtable();
		// Add a Line End Attribute
		GraphConstants.setLineStyle(map, GraphConstants.STYLE_ORTHOGONAL);
		GraphConstants.setRouting(map, GraphConstants.ROUTING_SIMPLE);
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_TECHNICAL);
		GraphConstants.setEditable(map, true);
		GraphConstants.setLineWidth(map, (float) 1.0);
		GraphConstants.setLineBegin(map, 10);
		// GraphConstants.setOpaque(map, true);
		// GraphConstants.setMoveable(map, true);
		// GraphConstants.setMoveableAxis(map, GraphConstants.X_AXIS);
		// GraphConstants.setMoveableAxis(map, GraphConstants.Y_AXIS);
		// Add a label along edge attribute
		GraphConstants.setLabelAlongEdge(map, false);
		return map;
	}


	protected RMGraphEdge createDefaultEdge() {
		return new RMGraphEdge();
	}

	public JPopupMenu createPopupMenu(final Point pt, final Object cell) {
		JPopupMenu menu = new JPopupMenu();
		// if (graph.isSelectionEmpty()) {
		if (cell != null && cell instanceof RMGraphCell) {
			RMAppContext.cell = (RMGraphCell) cell;
			// Edit
			JMenuItem paraItem = new JMenuItem("Parameters");
			if (RMAppContext.cell.getDataMap().keySet().size() == 0
				 &&  !"Y".equals(RMAppContext.cell.getFromDataCache()) && !"M".equals(RMAppContext.cell.getFromDataCache()) ) {
				paraItem.setEnabled(false);
			} else {
//			 if ("Y".equals(RMAppContext.cell.getFromDataCache()) || "M".equals(RMAppContext.cell.getFromDataCache())) {
				paraItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// graph.startEditingAtCell(cell);
						RMAppRMAction.doEditAction(e);
						RMAppContext.cell = null;
					}
				});
			 }
//				else if( RMAppContext.cell.getDataMap().keySet().size() == 0){
//				paraItem.setEnabled(false);
//			    }
			 
//			}
			menu.add(paraItem);

			menu.addSeparator();

			menu.add(new AbstractAction("Remove") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					// remove.actionPerformed(e);
					Action action = RMAppContext.getKit().getAction(
							RMAppEditAction.NAME_DELETE);
					action.actionPerformed(e);
				}
			});
		} else if (!graph.isSelectionEmpty()) {
			RMAppContext.cell = null;
			// menu.addSeparator();
			menu.add(new AbstractAction("Remove") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					// remove.actionPerformed(e);
					Action action = RMAppContext.getKit().getAction(
							RMAppEditAction.NAME_DELETE);
					action.actionPerformed(e);
				}
			});
		}

		return menu;
	}

}
