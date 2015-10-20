package com.rm.app.ui.action;

import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import org.jgraph.graph.GraphModel;

import com.rm.app.RMAppConst;
import com.rm.app.RMAppContext;
import com.rm.app.graph.RMGraph;
import com.rm.app.log.RMLogger;
import com.rm.app.ui.RMAppDiagramPane;

/**
 * Implements all actions of the file menu. The openRecent menu is implemented
 * using the {@link JGraphpadOpenRecentMenu} class, and the import/export
 * actions are added to the file menu by plugins, so look for their
 * implementations there.
 */
public class RMAppFileAction extends RMAppAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Defines the text/plain mime-type.
	 */
	public static final String MIME_PLAINTEXT = "text/plain",
			MIME_HTML = "text/html";

	/**
	 * Defines the (double) newline character as used in mime responses.
	 */
	static String NL = "\r\n", NLNL = NL + NL;

	/**
	 * Specifies the name for the <code>newDocument</code> action.
	 */
	public static final String NAME_NEWDOCUMENT = "newDocument";

	public static final String NAME_NEWFLOW = "newFlow";

	/**
	 * Specifies the name for the <code>newDiagram</code> action.
	 */
	public static final String NAME_NEWDIAGRAM = "newDiagram";

	/**
	 * Specifies the name for the <code>renameDiagram</code> action.
	 */
	public static final String NAME_RENAMEDIAGRAM = "renameDiagram";

	/**
	 * Specifies the name for the <code>removeDiagram</code> action.
	 */
	public static final String NAME_REMOVEDIAGRAM = "removeDiagram";

	/**
	 * Specifies the name for the <code>newLibrary</code> action.
	 */
	public static final String NAME_NEWLIBRARY = "newLibrary";

	/**
	 * Specifies the name for the <code>open</code> action.
	 */
	public static final String NAME_OPEN = "open";

	/**
	 * Specifies the name for the <code>download</code> action.
	 */
	public static final String NAME_DOWNLOAD = "download";

	/**
	 * Specifies the name for the <code>close</code> action.
	 */
	public static final String NAME_CLOSE = "close";

	/**
	 * Specifies the name for the <code>closeAll</code> action.
	 */
	public static final String NAME_CLOSEALL = "closeAll";

	/**
	 * Specifies the name for the <code>save</code> action.
	 */
	public static final String NAME_SAVE = "save";

	/**
	 * Specifies the name for the <code>saveAs</code> action.
	 */
	public static final String NAME_SAVEAS = "saveAs";

	/**
	 * Specifies the name for the <code>uploadAs</code> action.
	 */
	public static final String NAME_UPLOADAS = "uploadAs";

	/**
	 * Specifies the name for the <code>saveAll</code> action.
	 */
	public static final String NAME_SAVEALL = "saveAll";

	/**
	 * Specifies the name for the <code>saveImage</code> action.
	 */
	public static final String NAME_SAVEIMAGE = "saveImage";

	/**
	 * Specifies the name for the <code>print</code> action.
	 */
	public static final String NAME_PRINT = "print";

	/**
	 * Specifies the name for the <code>exit</code> action.
	 */
	public static final String NAME_EXIT = "exit";

	public static final String NAME_OPENFILE = "openFile";

	public static final String NAME_NEWBYGUIDE = "newByGuide";

	/**
	 * References the enclosing editor.
	 */
	// protected JGraphEditor editor;
	/**
	 * Holds the last directory for file operations.
	 */
	protected File lastDirectory = null;

	/**
	 * Constructs a new file action for the specified name and editor.
	 * 
	 * @param name
	 *            The name of the action to be created.
	 * @param editor
	 *            The enclosing editor for the action.
	 */
	public RMAppFileAction(String name) {
		super(name);
	}

	/**
	 * Executes the action based on the action name.
	 * 
	 * @param event
	 *            The object that describes the event.
	 */
	public void actionPerformed(ActionEvent event) {
		RMGraph graph = getPermanentFocusOwnerGraph();
		if (graph == null) {
			graph = RMAppContext.getGraph();
		}
		try {
			if (getActionObjectName().equals(NAME_EXIT)) {
				doExitAction(event);
			} else if (getActionObjectName().equals(NAME_NEWBYGUIDE)) {
				doNewByGuideAction(event);
			} else if (getActionObjectName().equals(NAME_NEWFLOW)) {
				doNewFlowAction(event);
			} else if (getActionObjectName().equals(NAME_SAVE)) {
				doSaveAction(event);
			} else if (getActionObjectName().equals(NAME_SAVEAS)) {
				doSaveAsAction(event);
			} else if (getActionObjectName().equals(NAME_CLOSE)) {
				this.doNewFlowAction(event);
			} else if (getActionObjectName().equals(NAME_OPEN)) {
				doOpenFile(event);
			} else if (getActionObjectName().equals(NAME_OPENFILE)) {
				doOpenFile(event);
			}

			// Actions that require a focused diagram pane
			RMAppDiagramPane diagramPane = getPermanentFocusOwnerDiagramPane();
			if (diagramPane != null) {
				if (getActionObjectName().equals(NAME_SAVEIMAGE)) {
					RMLogger.debug(NAME_SAVEIMAGE);

				} else if (getActionObjectName().equals(NAME_PRINT)) {
					doPrintDiagramPane(diagramPane);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doOpenFile(ActionEvent event) {
		ActionHelper.doFileOpenActionWithChooser(RMAppContext.rmMyFlowPath);
	}

	private void doSaveAsAction(ActionEvent event) {
		ActionHelper.doSaveAsAction();
	}

	/**
	 * Saves the specified byte array to the specified file.
	 * 
	 * @param filename
	 *            The filename of the file to be written.
	 * @param data
	 *            The array of bytes to write to the file.
	 */
	private boolean doSaveAction(ActionEvent event) throws Exception {
		return ActionHelper.doSaveAction();
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	private void doNewFlowAction(ActionEvent event) throws Exception {
		RMGraph graph = getPermanentFocusOwnerGraph();
		if (graph == null) {
			graph = RMAppContext.getGraph();
		}
		if (!graph.isEmpty() && !graph.isSaved()) {
			int returnCode = JOptionPane.showConfirmDialog(RMAppContext
					.getRMApp(),
					"The file is not saved!\n Are you sure to drop?");
			if (0 == returnCode) {// yes

			} else if (1 == returnCode) {// not
				if (!doSaveAction(event)) {
					return;
				}
			} else if (2 == returnCode) {// cancel
				return;
			}
		}
		ActionHelper.cleanCurrentSpaceToFile(graph, null);

	}

	/**
	 * 
	 * @throws IOException
	 *             If there was an error saving unsaved changes.
	 */
	private void doExitAction(ActionEvent event) throws Exception {
		if (ActionHelper
				.confirmSaveCurrentFlow(RMAppConst.MSG_CONFIRM_FOLW_SAVE_EXIT))
			System.exit(-1);
	}

	/**
	 * Displays a system print dialog and prints the specified diagram pane.
	 * 
	 * @param diagramPane
	 *            The diagram pane to be printed.
	 * @throws PrinterException
	 *             If the document can not be printed.
	 */
	protected void doPrintDiagramPane(RMAppDiagramPane diagramPane)
			throws PrinterException {
		// TODO
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(diagramPane);
		if (printJob.printDialog())
			printJob.print();

	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	private void doNewByGuideAction(ActionEvent event) throws Exception {
		//
		// GuidanceFrame gFrame = new GuidanceFrame("Choose ");
		// gFrame.setAlwaysOnTop(true);
		// gFrame.setVisible(true);
		// JOptionPane.showMessageDialog(RMAppContext.getRMApp(), "TODO");
	}

	/**
	 * Displays the system page format dialog and updates the pageformat on the
	 * specified diagram pane.
	 * 
	 * @param diagramPane
	 *            The diagram pane to set the page format.
	 */
	protected void doPageSetup(RMAppDiagramPane diagramPane) {
		PageFormat format = PrinterJob.getPrinterJob().pageDialog(
				diagramPane.getPageFormat());
		if (format != null)
			diagramPane.setPageFormat(format);
	}

	/**
	 * Utility method to return the cell stored under key in the specified map
	 * or create the cell using the specified prototype and model and put it
	 * into the map under key. The cells will be positioned into a matrix with
	 * <code>cols</code> columns and entries of size (w,h).
	 * 
	 * @param model
	 *            The model to use for cloning the prototype.
	 * @param prototype
	 *            The prototype to use for creating new cells.
	 * @param map
	 *            The map to check whether the cell exists for key.
	 * @param key
	 *            The key to return the cell for.
	 * @param cols
	 *            The number of columns for the matrix.
	 * @param w
	 *            The width of the entries.
	 * @param h
	 *            The height of the entries.
	 * @param offset
	 *            The offset from the top left.
	 * @param image
	 *            Whether to insert image or text cells.
	 * @return Returns the cell for the specified key.
	 */
	public static Object getCellForKey(GraphModel model, Object prototype,
			Hashtable map, String key, int cols, int w, int h, int offset,
			boolean image) {
		Object cell = map.get(key);
		// if (cell == null) {
		// cell = DefaultGraphModel.cloneCell(model, prototype);
		// if (image)
		// model.valueForCellChanged(cell, key);
		// else
		// model.valueForCellChanged(cell, new JGraphpadRichTextValue(key));
		//
		// GraphConstants.setResize(model.getAttributes(cell), true);
		//
		// // Set initial Location
		// int col = map.size() / cols;
		// int row = map.size() % cols;
		// Rectangle2D bounds = new Rectangle2D.Double(row * w + offset, col * h
		// + offset, 10, 10);
		// GraphConstants.setBounds(model.getAttributes(cell), bounds);
		// map.put(key, cell);
		// }
		return cell;
	}

	/**
	 * Posts the data to the specified url using <code>path</code> to specify
	 * the filename in the mime response using for type {@link #MIME_PLAINTEXT}.
	 * 
	 * @param url
	 *            The url to post the mime response to.
	 * @param path
	 *            The filename to use in the mime response.
	 * @param data
	 *            The binary data to send with the mime response.
	 * @return Returns true if the data was successfuly posted.
	 * @throws IOException
	 */
	public static boolean postPlain(URL url, String path, OutputStream data)
			throws IOException {
		return post(url, path, MIME_PLAINTEXT, data.toString());
	}

	/**
	 * Posts the data to the specified url using <code>path</code> to specify
	 * the filename in the mime response for the specified mime type.
	 * 
	 * @param url
	 *            The url to post the mime response to.
	 * @param path
	 *            The filename to use in the mime response.
	 * @param mime
	 *            The mime type to use for the response.
	 * @param data
	 *            The binary data to send with the mime response.
	 * @return Returns true if the data was successfuly posted.
	 * @throws IOException
	 */
	public static boolean post(URL url, String path, String mime,
			OutputStream data) throws IOException {
		return post(url, path, mime, convert(data, mime));
	}

	/**
	 * Converts the specified data stream into a string assuming the data stream
	 * is of the specified mime type. This performs a byte to char conversion on
	 * all mime types other than {@link #MIME_PLAINTEXT}.
	 * 
	 * @param data
	 *            The data to be converted.
	 * @param mime
	 *            The mime type to assume for the data.
	 * @return Returns a string representation of the data in the stream.
	 */
	public static String convert(OutputStream data, String mime) {
		String text = null;
		if (data instanceof ByteArrayOutputStream) {
			byte[] aByte = ((ByteArrayOutputStream) data).toByteArray();
			int size = aByte.length;
			char[] aChar = new char[size];
			for (int i = 0; i < size; i++) {
				aChar[i] = (char) aByte[i];
			}
			text = String.valueOf(aChar, 0, aChar.length);
		} else
			text = data.toString();
		return text;
	}

	/**
	 * Posts the data to the specified url using <code>path</code> to specify
	 * the filename in the mime response for the specified mime type.
	 * 
	 * @param url
	 *            The url to post the mime response to.
	 * @param path
	 *            The filename to use in the mime response.
	 * @param mime
	 *            The mime type to use for the response.
	 * @param data
	 *            The binary data to send with the mime response.
	 * @return Returns true if the data was successfuly posted.
	 * @throws IOException
	 */
	public static boolean post(URL url, String path, String mime, String data)
			throws IOException {
		String sep = "89692781418184";
		while (data.indexOf(sep) != -1)
			sep += "x";
		String message = makeMimeForm("", mime, path, data, "", sep);

		// Ask for parameters
		URLConnection connection = url.openConnection();
		connection.setAllowUserInteraction(false);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-type",
				"multipart/form-data; boundary=" + sep);
		connection.setRequestProperty("Content-length", Integer
				.toString(message.length()));

		String replyString = null;
		try {
			DataOutputStream out = new DataOutputStream(connection
					.getOutputStream());
			out.writeBytes(message);
			out.close();
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String reply = null;
				while ((reply = in.readLine()) != null) {
					if (reply.startsWith("ERROR ")) {
						replyString = reply.substring("ERROR ".length());
					}
				}
				in.close();
			} catch (IOException ioe) {
				replyString = ioe.toString();
			}
		} catch (UnknownServiceException use) {
			replyString = use.getMessage();
		}
		if (replyString != null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns a mime form using the specified parameters.
	 */
	public static String makeMimeForm(String fileName, String type,
			String path, String content, String comment, String sep) {
		String binary = "";
		if (type.startsWith("image/") || type.startsWith("application")) {
			binary = "Content-Transfer-Encoding: binary" + NL;
		}
		String mime_sep = NL + "--" + sep + NL;
		return "--" + sep + "\r\n"
				+ "Content-Disposition: form-data; name=\"filename\"" + NLNL
				+ fileName + mime_sep
				+ "Content-Disposition: form-data; name=\"noredirect\"" + NLNL
				+ 1 + mime_sep
				+ "Content-Disposition: form-data; name=\"filepath\"; "
				+ "filename=\"" + path + "\"" + NL + "Content-Type: " + type
				+ NL + binary + NL + content + mime_sep
				+ "Content-Disposition: form-data; name=\"filecomment\"" + NLNL
				+ comment + NL + "--" + sep + "--" + NL;
	}

	/**
	 * Returns the permanent focus owner library pane.
	 */
	// public static JGraphpadLibraryPane getPermanentFocusOwnerLibraryPane() {
	// Component component =
	// KeyboardFocusManager.getCurrentKeyboardFocusManager(
	// ).getPermanentFocusOwner();
	// return JGraphpadLibraryPane.getParentLibraryPane(component);
	// }
	/**
	 * Bundle of all actions in this class.
	 */
	public static class AllActions implements Bundle {

		/**
		 * Holds the actions. All actions require an editor reference and are
		 * therefore created at construction time.
		 */
		public RMAppAction actionNewDocument, actionNewDiagram,
				actionNewLibrary, actionOpen, actionDownload, actionSave,
				actionSaveAs, actionUploadAs, actionSaveAll,
				actionRenameDiagram, actionRemoveDiagram, actionClose,
				actionCloseAll, actionSaveImage, actionPrint, actionExit,
				actionNewFlow, actionOpenFile, actionNewByGuide;

		/**
		 * Constructs the action bundle for the specified editor.
		 * 
		 * @param editor
		 *            The enclosing editor for this bundle.
		 */
		public AllActions() {
			actionNewFlow = new RMAppFileAction(NAME_NEWFLOW);
			actionOpenFile = new RMAppFileAction(NAME_OPENFILE);
			actionNewDocument = new RMAppFileAction(NAME_NEWDOCUMENT);
			actionNewByGuide = new RMAppFileAction(NAME_NEWBYGUIDE);
			actionNewDiagram = new RMAppFileAction(NAME_NEWDIAGRAM);
			actionNewLibrary = new RMAppFileAction(NAME_NEWLIBRARY);
			actionOpen = new RMAppFileAction(NAME_OPEN);
			actionDownload = new RMAppFileAction(NAME_DOWNLOAD);
			actionSave = new RMAppFileAction(NAME_SAVE);
			actionSaveAs = new RMAppFileAction(NAME_SAVEAS);
			actionUploadAs = new RMAppFileAction(NAME_UPLOADAS);
			actionSaveAll = new RMAppFileAction(NAME_SAVEALL);
			actionRemoveDiagram = new RMAppFileAction(NAME_REMOVEDIAGRAM);
			actionRenameDiagram = new RMAppFileAction(NAME_RENAMEDIAGRAM);
			actionPrint = new RMAppFileAction(NAME_PRINT);
			actionClose = new RMAppFileAction(NAME_CLOSE);
			actionCloseAll = new RMAppFileAction(NAME_CLOSEALL);
			actionSaveImage = new RMAppFileAction(NAME_SAVEIMAGE);
			actionExit = new RMAppFileAction(NAME_EXIT);
		}

		/*
		 * (non-Javadoc)
		 */
		public RMAppAction[] getActions() {
			return new RMAppAction[] { actionNewDocument, actionNewDiagram,
					actionNewLibrary, actionOpen, actionDownload, actionSave,
					actionSaveAs, actionUploadAs, actionSaveAll,
					actionRemoveDiagram, actionRenameDiagram, actionPrint,
					actionClose, actionCloseAll, actionSaveImage, actionExit,
					actionNewFlow, actionOpenFile, actionNewByGuide };
		}

		/*
		 * (non-Javadoc)
		 */
		public void update() {
			// JGraphEditorDiagram diagram = getPermanentFocusOwnerDiagram();
			// JGraphEditorFile file = getPermanentFocusOwnerFile();
			boolean isDiagramFocused = true; // = (diagram != null);
			boolean isFileModified = true; // = (file != null &&
			// (file.isModified() ||
			// file
			// .isNew()));
			actionOpen.setEnabled(true);

			actionRemoveDiagram.setEnabled(isDiagramFocused);
			actionRenameDiagram.setEnabled(isDiagramFocused);
			actionNewDiagram.setEnabled(isDiagramFocused);
			actionNewByGuide.setEnabled(true);
			actionNewFlow.setEnabled(isDiagramFocused);
			actionOpenFile.setEnabled(isDiagramFocused);
			actionNewLibrary.setEnabled(isDiagramFocused); // || !JGraphpad.
			// INNER_LIBRARIES);

			actionSaveImage.setEnabled(isDiagramFocused);
			actionClose.setEnabled(true); // file != null);
			actionCloseAll.setEnabled(true); // file != null);
			actionSave.setEnabled(isFileModified);
			actionSaveAs.setEnabled(true); // file != null);
			actionSaveAll.setEnabled(true); // file != null);
			actionUploadAs.setEnabled(true); // file != null);
		}

	}

};
