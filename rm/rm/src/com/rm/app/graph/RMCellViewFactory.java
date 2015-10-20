package com.rm.app.graph;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortView;

public class RMCellViewFactory extends DefaultCellViewFactory {

    protected PortView createPortView(Object cell) {
	PortView pView = new PortView(cell);

	GraphConstants.setResize(pView.getAttributes(), true);
	pView.setPortSize(8);

	pView.renderer = new RMPortRender();

	return pView;
    }
}
