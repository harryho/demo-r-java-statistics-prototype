package com.rm.app.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

public class StartupWindow extends JWindow implements MouseListener{

	private static final long serialVersionUID = 1L;

	public StartupWindow(final JFrame frame) {
		super(frame);
		URL componentUrl = getClass().getClassLoader().getResource(
				"com/rm/images/startup_logo.jpg");
		JLabel lab_logo = new JLabel(new ImageIcon(componentUrl));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = lab_logo.getPreferredSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2),
				screenSize.height / 2 - (labelSize.height));

		JPanel pan_logo = new JPanel();
		pan_logo.add(lab_logo, SwingConstants.CENTER);

		MouseAdapter mouseAdapter = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				setVisible(false);
				frame.setVisible(true);
				dispose();
			}
		};
		this.addMouseListener(mouseAdapter);
		Font font = new Font("Times New Roman", Font.BOLD, 14);
		JLabel lab_ver = new JLabel("RM4Es Software Version 0.2");
		lab_ver.setFont(font);
		JPanel pan_ver = new JPanel();
		pan_ver.add(lab_ver, SwingConstants.CENTER);

		JLabel lab_institute = new JLabel("The RM Institute");
		lab_institute.setFont(new Font("Times New Roman", Font.BOLD, 13));
		JPanel pan_institute = new JPanel();
		pan_institute.add(lab_institute, SwingConstants.CENTER);

		JLabel lab_weblink = new JLabel("www.ResearchMethods.org");
		lab_weblink
				.setText("<html><a href=#'>www.ResearchMethods.org</a></html>");
		font = new  Font("Times New Roman", Font.BOLD, 11);
		lab_weblink.setFont(font);
		lab_weblink.addMouseListener(this);
		JPanel pan_weblink = new JPanel();
		pan_weblink.add(lab_weblink, SwingConstants.CENTER);
		

		JLabel lab_copyright = new JLabel("Copyright @ the RM Institute");
		font = new  Font("Times New Roman", Font.PLAIN, 10);
		lab_copyright.setFont(font);
		JPanel pan_copyright = new JPanel();
		pan_copyright.add(lab_copyright, SwingConstants.CENTER);

		Box bv = Box.createVerticalBox();

		bv.add(pan_logo);
		bv.add(pan_ver);
		bv.add(pan_institute);
		bv.add(pan_weblink);
		bv.add(pan_copyright);

		getContentPane().add(bv, BorderLayout.CENTER);
		pack();
		this.setAlwaysOnTop(true);
		setVisible(true);
	}

	public void mouseClicked(MouseEvent e) {
		try {
			Runtime.getRuntime().exec("cmd.exe  /c  start http://www.researchmethods.org/");   

		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	public void mouseEntered(MouseEvent e) {
		JLabel lab_weblink = (JLabel)e.getSource();
		lab_weblink.setCursor(new  Cursor(Cursor.HAND_CURSOR));   
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}

}
