package com.rm.app.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseMotionListener;
import java.awt.image.ImageObserver;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import org.jgraph.JGraph;

import com.rm.app.RMAppConst;
import com.rm.app.RMAppContext;
import com.rm.app.log.LogConsole;
import com.rm.app.ui.action.StatusBarGraphListener;

public class RMAppMainPanel {
    private JGraph graph;

    public JGraph getGraph() {
	return graph;
    }

    public void setGraph(JGraph graph) {
	this.graph = graph;
    }

    public JPanel getInstance() {
	Object app = RMAppContext.get(RMAppConst.RM_APP);

	graph = RMAppContext.getGraph();

	JPanel mainPanel = new JPanel(new BorderLayout());

	//RMTreeController treeController = new RMTreeController();

	//JPanel leftBottomPanel = new JPanel();
	// leftBottomPanel.add(textField);

	JTabbedPane leftTabbedPane = RMAppFactory.createTabbedPane(JTabbedPane.TOP);
	JPanel leftTabAnalyPane = new JPanel();
	leftTabAnalyPane.setLayout(new BorderLayout());

	ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("com/rm/images/rmcomponents.jpg"));
	icon.setImageObserver(new ImageObserver() {
	    public boolean imageUpdate(Image icon, int infoflags, int x, int y, int width, int height) {
	
		return false;
	    }
	});
	JLabel label = new JLabel();
	label.setIcon(icon);
	leftTabAnalyPane.add(label, BorderLayout.NORTH);
	leftTabbedPane.add("Analysis ", new RMAnalysisTaskPane());
	leftTabbedPane.add("Data", new RMDataTaskPane());
	leftTabbedPane.setMinimumSize(new Dimension(250, 300));
	leftTabbedPane.setOpaque(true);

	RMAppDiagramPane scrollPane = RMAppFactory.createDiagramPane();
	scrollPane.setAutoscrolls(true);

	RMAppContext.set(RMAppConst.RM_RESEARCH_FLOW_PANE, scrollPane);
	
	// 
	JTabbedPane rightTabbedPane = (JTabbedPane) RMAppContext.get(RMAppConst.RM_LOG_CONSOLE_PANE);// new

	rightTabbedPane.setPreferredSize(new Dimension(500, 120));
	
	// add log console	
	LogConsole console = LogConsole.getInstance();
	rightTabbedPane.add("Log", console);
	RMAppContext.set(RMAppConst.RM_LOG_CONSOLE, console);
	
	
	JPanel rightPane = new JPanel();
	RMAppContext.set(RMAppConst.RM_RIGHT_PANE, rightPane);
	rightPane.setLayout(new BorderLayout());
	rightPane.add(scrollPane, BorderLayout.CENTER);
	
	rightPane.add(rightTabbedPane, BorderLayout.SOUTH);
	
	
	JToolBar toolBar = (JToolBar) RMAppContext.get(RMAppConst.RM_TOOLBAR);
	
	JSplitPane leftPane = RMAppFactory.createSplitPane(leftTabbedPane, null, JSplitPane.VERTICAL_SPLIT);
	leftPane.setOneTouchExpandable(true);

	JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, rightPane);
	splitPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	splitPane.setOneTouchExpandable(true);

	StatusBarGraphListener statusBarGraphListener = (StatusBarGraphListener) RMAppContext
		.get(RMAppConst.RM_STATUSBAR);

	mainPanel.setLayout(new BorderLayout());
	mainPanel.add(toolBar, BorderLayout.NORTH);
	mainPanel.add(splitPane, BorderLayout.CENTER);
	mainPanel.add(statusBarGraphListener, BorderLayout.SOUTH);
	mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
	mainPanel.addMouseMotionListener((MouseMotionListener) statusBarGraphListener);

	//rightTabbedPane.
	
	return mainPanel;
    }
}
