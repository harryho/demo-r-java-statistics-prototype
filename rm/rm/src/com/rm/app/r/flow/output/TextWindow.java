package com.rm.app.r.flow.output;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.rm.app.RMAppConst;

public class TextWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	public static List<String> info = new ArrayList<String>();

	private JTextPane textPane = new JTextPane();

	SimpleAttributeSet set = new SimpleAttributeSet();

	public TextWindow() {
		super("RM4Es.R.Display");
		URL componentUrl = getClass().getClassLoader().getResource(RMAppConst.LOGO_TITLE_IMG);
		ImageIcon componentIcon = new ImageIcon(componentUrl);
		setIconImage(componentIcon.getImage());
		info = new ArrayList<String>();
		StyleConstants.setForeground(set, Color.BLUE);
		Rectangle re = new Rectangle(500, 250);
		setBounds(re);
		textPane.setEditable(false);
		JScrollPane js = new JScrollPane(textPane);
		js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		js.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(js);
		this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent event) {
				disposeWindow();
			}

		});

	}

	public void disposeWindow() {
		info = new ArrayList<String>();
		// this.dispose();
		setVisible(false);
	}

	public void display() {
		for (int i = 0, k = info.size(); i < k; i++) {
			appendInfo("> " + info.get(i));
			// appendInfo("\n");
		}
		setVisible(true);
	}

	private void appendInfo(String msg) {
		Document doc = textPane.getStyledDocument();
		int length = doc.getLength();
		try {
			if (length > 0) {
				doc.insertString(length, "\n" + msg, set);
			} else {
				doc.insertString(length, msg, set);
			}
			textPane.setCaretPosition(length);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
}
