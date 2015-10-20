package com.rm.app.r.flow.output;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.rm.app.RMAppConst;

public class GraphWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String currentPng = null;

	public GraphWindow() {
		super("RM4Es.R.Graph");
		URL componentUrl = getClass().getClassLoader().getResource(RMAppConst.LOGO_TITLE_IMG);
		ImageIcon componentIcon = new ImageIcon(componentUrl);
		setIconImage(componentIcon.getImage());
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent event) {
				disposeWindow();
			}

		});
		
	}

	public boolean displayPng(String file) {
		currentPng = file;
		File png = new File(currentPng);
		if (!png.exists()) {
			return false;
		}
		ImageIcon result = new ImageIcon(file);
		JLabel pngLab = new JLabel(result);
//		setBounds(new Rectangle(result.getIconHeight() + 3, result
//				.getIconWidth() + 10));
		
		JPanel panel = new JPanel(new BorderLayout());
		
		JScrollPane jsp = new JScrollPane();
		setSize(500, 400);
		jsp.setViewportView(pngLab);		
		panel.add(jsp, BorderLayout.CENTER );
		add( panel);
		setResizable(false);
		setVisible(true);
		return true;
	}

	public void disposeWindow() {
		setVisible(false);
	}

}
