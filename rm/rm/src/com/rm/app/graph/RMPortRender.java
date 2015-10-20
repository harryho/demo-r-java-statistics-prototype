package com.rm.app.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.PortRenderer;
import org.jgraph.graph.PortView;

public class RMPortRender extends PortRenderer {

    public Component getRendererComponent(JGraph graph, CellView view, boolean sel, boolean focus, boolean preview) {

	// Check type
	if (view instanceof PortView && graph != null) {
	    graphBackground = Color.GREEN; // graph.getBackground();
	    this.view = (PortView) view;
	    this.hasFocus = true; //focus;
	    this.selected = true ; //sel;
	    this.preview = true; //preview;
	    this.xorEnabled = true; //graph.isXorEnabled();

	    GraphConstants.setResize(this.view.getAttributes(), true);

	    this.view.setPortSize(8);

	    return this;
	}
	return null;
    }

    public void paint(Graphics g) {	
	Dimension d = getSize();	
	
	 g.setColor(Color.GREEN);
	 
	if (preview) {
	    g.drawRect(0,0, d.width-2, d.height-2);
	    g.drawRect(1, 1,  d.width-4, d.height-4);
	} 
    }

}
