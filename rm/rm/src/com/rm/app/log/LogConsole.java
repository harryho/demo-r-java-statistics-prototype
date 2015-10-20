package com.rm.app.log;

import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class LogConsole extends JScrollPane implements Logger {

	private static final long serialVersionUID = 1L;

	static LogConsole instance;

	static class Holder {
		public static LogConsole instance = new LogConsole();
	}

	public static LogConsole getInstance() {
		if (instance == null) {
			instance = Holder.instance;
		}
		return instance;
	}

	private final int MAX_SIZE_OF_LOGS = 10000;

	private JTextPane textPane = new JTextPane();

	public LogConsole() {
		super();
		this.setAutoscrolls(true);
		// this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Font monospaced = new Font("Aril", Font.PLAIN, 12);
		textPane.setFont(monospaced);
		setViewportView(textPane);

		textPane.addMouseListener(new ConsoleMouseListner(textPane));

	}

	private void writeToConsole(Object msg, SimpleAttributeSet set) {
		
		checkSizeOfPool();
		Document doc = textPane.getStyledDocument();
		int length = doc.getLength();
		try {
			if (length > 0) {
				doc.insertString(length, "\n" + getTime() + "  " + msg, set);
			} else {
				doc.insertString(length, "" + getTime() + "  " + msg, set);
			}
			textPane.setCaretPosition(length);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void checkSizeOfPool() {
		Document doc = textPane.getStyledDocument();
		int length = doc.getLength();
		if (length > MAX_SIZE_OF_LOGS) {
			try {
				doc.remove(0, length - MAX_SIZE_OF_LOGS);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getTime() {
		Date today = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
		return formatter.format(today);

	}

	public void infor(String msg) {
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, Color.BLUE);
		writeToConsole(msg, set);
	}

	public void error(String msg) {
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, Color.RED);
		writeToConsole(msg, set);
	}

	public void warn(String msg) {
		SimpleAttributeSet set = new SimpleAttributeSet();
		StyleConstants.setForeground(set, Color.RED);
		writeToConsole(msg, set);
	}
	
	public void cleanConsole(){
		Document doc = textPane.getStyledDocument();
		try {
			doc.remove(0, doc.getLength());
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	private class ConsoleMouseListner implements MouseListener {
		JTextPane textPane;

		private JPopupMenu pop = null;

		private JMenuItem copy = null;

		private JMenuItem clear = null;

		public ConsoleMouseListner(JTextPane textPane) {
			this.textPane = textPane;
			pop = new JPopupMenu();
			pop.add(copy = new JMenuItem("Copy"));
			pop.add(clear = new JMenuItem("Clear"));

			copy.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					action(e);
				}
			});

			clear.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					action(e);
				}
			});

		}

		public void action(ActionEvent e) {
			String str = e.getActionCommand();
			if (str.equals(copy.getText())) {
				textPane.copy();
			} else if (str.equals(clear.getText())) {
				cleanConsole();
			}
		}

		public void mouseClicked(MouseEvent e) {

		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}

		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				pop.show(e.getComponent(), e.getX(), e.getY());
			}

		}

		public void mouseReleased(MouseEvent e) {

		}

		public boolean isClipboardString() {
			boolean b = false;
			Clipboard clipboard = textPane.getToolkit().getSystemClipboard();
			Transferable content = clipboard.getContents(this);
			try {
				if (content.getTransferData(DataFlavor.stringFlavor) instanceof String) {
					b = true;
				}
			} catch (Exception e) {
			}
			return b;
		}

	
		public boolean isCanCopy() {
			boolean b = false;
			int start = textPane.getSelectionStart();
			int end = textPane.getSelectionEnd();
			if (start != end)
				b = true;
			return b;
		}

	}
}
