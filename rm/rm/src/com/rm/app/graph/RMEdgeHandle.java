package com.rm.app.graph;

import java.awt.event.MouseEvent;

import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphContext;

public class RMEdgeHandle extends EdgeView.EdgeHandle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param edge
	 * @param ctx
	 */
	public RMEdgeHandle(EdgeView edge, GraphContext ctx) {
		super(edge, ctx);
	}

	// Override Superclass Method
	public boolean isAddPointEvent(MouseEvent event) {
		// Points are Added using Shift-Click
		return event.isShiftDown();
	}

	// Override Superclass Method
	public boolean isRemovePointEvent(MouseEvent event) {
		// Points are Removed using Shift-Click
		return event.isShiftDown();
	}

}
