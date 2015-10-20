package com.rm.app.r.flow.output;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

import javax.swing.*;

import com.rm.app.RMAppConst;

import att.grappa.*;

public class PathDiagramWindow extends JFrame implements GrappaConstants {
    // public DemoFrame frame = null;

    public final static String SCRIPT = "../formatDemo";


    public PathDiagramWindow(File file) {
	super("RM4Es.R.Graph");
	URL componentUrl = getClass().getClassLoader().getResource(RMAppConst.LOGO_TITLE_IMG);
	ImageIcon componentIcon = new ImageIcon(componentUrl);
	setIconImage(componentIcon.getImage());
	InputStream input = null;
	try {
	    input = new FileInputStream(file);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	Parser program = new Parser(input, System.err);
	try {
	    program.parse();
	} catch (Exception ex) {
	    System.err.println("Exception: " + ex.getMessage());
	    ex.printStackTrace(System.err);
	    System.exit(1);
	}
	Graph graph = null;

	graph = program.getGraph();
	System.err.println("The graph contains " + graph.countOfElements(Grappa.NODE | Grappa.EDGE | Grappa.SUBGRAPH)
		+ " elements.");

	graph.setEditable(true);
	graph.setErrorWriter(new PrintWriter(System.err, true));

	System.err.println("bbox=" + graph.getBoundingBox().getBounds().toString());

	setSize(480, 350);
	setLocation(100, 100);
	GrappaPanel gp;

	addWindowListener(new WindowAdapter() {
	    public void windowClosing(WindowEvent wev) {
		Window w = wev.getWindow();
		w.setVisible(false);
	    }
	});

	JScrollPane jsp = new JScrollPane();

	gp = new GrappaPanel(graph);
	gp.addGrappaListener(new GrappaAdapter());
	gp.setScaleToFit(false);

//	java.awt.Rectangle bbox = graph.getBoundingBox().getBounds();

//	GridBagLayout gbl = new GridBagLayout();
	GridBagConstraints gbc = new GridBagConstraints();

	gbc.gridwidth = GridBagConstraints.REMAINDER;
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.anchor = GridBagConstraints.NORTHWEST;

	getContentPane().add("Center", jsp);
	setVisible(true);
	jsp.setViewportView(gp);
    }
}
