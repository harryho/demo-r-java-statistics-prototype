package com.rm.app.ui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.TransferHandler;
import javax.swing.text.DefaultEditorKit;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphUndoManager;

import com.rm.app.r.component.data.RDataCache;
import com.rm.app.ui.RMAppDiagramPane;

/**
 * Implements all actions of the edit menu. The selectPath and selectTree
 * actions are implemented by plugins.
 */
public class RMAppEditAction extends RMAppAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Holds the last search expression. Note: In a multi application
     * environment you may have to put this into the application instance.
     */
    protected static Pattern lastSearchPattern = null;

    /**
     * Holds the last found cell for a search.
     */
    protected static Object lastFoundCell = null;

    /**
     * Specifies the name for the <code>cut</code> action.
     */
    public static final String NAME_CUT = "cut";

    /**
     * Specifies the name for the <code>copy</code> action.
     */
    public static final String NAME_COPY = "copy";

    /**
     * Specifies the name for the <code>paste</code> action.
     */
    public static final String NAME_PASTE = "paste";

    /**
     * Specifies the name for the <code>delete</code> action.
     */
    public static final String NAME_DELETE = "delete";

    /**
     * Specifies the name for the <code>edit</code> action.
     */
    public static final String NAME_EDIT = "edit";

    /**
     * Specifies the name for the <code>find</code> action.
     */
    public static final String NAME_FIND = "find";

    /**
     * Specifies the name for the <code>findAgain</code> action.
     */
    public static final String NAME_FINDAGAIN = "findAgain";

    /**
     * Specifies the name for the <code>undo</code> action.
     */
    public static final String NAME_UNDO = "undo";

    /**
     * Specifies the name for the <code>redo</code> action.
     */
    public static final String NAME_REDO = "redo";

    /**
     * Specifies the name for the <code>selectAll</code> action.
     */
    public static final String NAME_SELECTALL = "selectAll";

    /**
     * Specifies the name for the <code>clearSelection</code> action.
     */
    public static final String NAME_CLEARSELECTION = "clearSelection";

    /**
     * Specifies the name for the <code>selectVertices</code> action.
     */
    public static final String NAME_SELECTVERTICES = "selectVertices";

    /**
     * Specifies the name for the <code>selectEdges</code> action.
     */
    public static final String NAME_SELECTEDGES = "selectEdges";

    /**
     * Specifies the name for the <code>deselectVertices</code> action.
     */
    public static final String NAME_DESELECTVERTICES = "deselectVertices";

    /**
     * Specifies the name for the <code>deselectEdges</code> action.
     */
    public static final String NAME_DESELECTEDGES = "deselectEdges";

    /**
     * Specifies the name for the <code>invertSelection</code> action.
     */
    public static final String NAME_INVERTSELECTION = "invertSelection";

    /**
     * Fallback action if the focus-owner is not a graph. This is assigned
     * internally for special action names, namely cut, copy, paste and delete
     * for text components.
     */
    protected Action fallbackAction = null;

    /**
     * Constructs a new edit action for the specified name. This constructs the
     * {@link #fallbackAction} for text components in case of supported actions.
     * 
     * @param name
     *            The name of the action to be created.
     */
    public RMAppEditAction(String name) {
	super(name);
	if (getActionObjectName().equals(NAME_COPY))
	    fallbackAction = new DefaultEditorKit.CopyAction();
	else if (getActionObjectName().equals(NAME_PASTE))
	    fallbackAction = new DefaultEditorKit.PasteAction();
	else if (getActionObjectName().equals(NAME_CUT))
	    fallbackAction = new DefaultEditorKit.CutAction();
	else if (getActionObjectName().equals(NAME_DELETE))
	    fallbackAction = new DefaultEditorKit.CutAction();
    }

    /**
     * Executes the action based on the action name.
     * 
     * @param e
     *            The object that describes the event.
     */
    public void actionPerformed(ActionEvent e) {
	Component component = getPermanentFocusOwner();
	

	if (component instanceof JGraph) {
	    JGraph graph = (JGraph) component;

	    // Creates a new event with the graph as the source.
	    // This is required for the transfer handler to work
	    // correctly when called from outside the graph ui.
	    ActionEvent newEvent = new ActionEvent(graph, e.getID(), e.getActionCommand());
	    if (getActionObjectName().equals(NAME_COPY))
		TransferHandler.getCopyAction().actionPerformed(newEvent);
	    else if (getActionObjectName().equals(NAME_PASTE))
		TransferHandler.getPasteAction().actionPerformed(newEvent);
	    else if (getActionObjectName().equals(NAME_CUT))
		TransferHandler.getCutAction().actionPerformed(newEvent);
	    else if (getActionObjectName().equals(NAME_DELETE)){
		RDataCache dataCache = RDataCache.getIntance();
	//	DataCache dataCache = DataCache.getIntance();
	//	dataCache.initial(graph.getSelectionCells());
//		dataCache.initial(null);
		graph.getGraphLayoutCache().remove(graph.getDescendants(graph.getSelectionCells()));
	        
	        
	    }
	    
	   
	}


	// Actions that require a focused diagram pane
	RMAppDiagramPane diagramPane = getPermanentFocusOwnerDiagramPane();
	if (diagramPane != null) {
	    if (getActionObjectName().equals(NAME_UNDO))
		doUndo(diagramPane);
	    else if (getActionObjectName().equals(NAME_REDO))
		doRedo(diagramPane);
	}
    }

    /**
     * Adds or removes the specified cells to/from the selection. If
     * <code>all</code> is true then <code>edges</code> is ignored.
     * 
     * @param graph
     *            The graph to perform the operation in.
     * @param all
     *            Whether all cells should be selected.
     * @param edges
     *            Whether edges or vertices should be selected.
     * @param deselect
     *            Whether to remove the cells from the selection.
     */
    protected void doSelect(JGraph graph, boolean all, boolean edges, boolean deselect) {
	Object[] cells = (all) ? graph.getRoots() : graph.getGraphLayoutCache().getCells(false, !edges, false, edges);
	if (deselect)
	    graph.getSelectionModel().removeSelectionCells(cells);
	else
	    graph.addSelectionCells(cells);
    }

    /**
     * Inverts the selection in the specified graph by selecting all cells for
     * which {@link #isParentSelected(JGraph, Object)} returns false.
     * 
     * @param graph
     *            The graph to perform the operation in.
     */
    public void doInvertSelection(JGraph graph) {
	GraphLayoutCache cache = graph.getGraphLayoutCache();
	CellView[] views = cache.getAllDescendants(cache.getRoots());
	List result = new ArrayList(views.length);
	for (int i = 0; i < views.length; i++)
	    if (views[i].isLeaf() && !graph.getModel().isPort(views[i].getCell())
		    && !isParentSelected(graph, views[i].getCell()))
		result.add(views[i].getCell());
	graph.setSelectionCells(result.toArray());
    }

    /**
     * Helper method that returns true if either the cell or one of its parent
     * is selected in <code>graph</code>.
     * 
     * @param graph
     *            The graph to check if the cell is selected in.
     * @param cell
     *            The cell that is to be tested for selection.
     * @return Returns true if cell or one of its ancestors is selected.
     */
    protected boolean isParentSelected(JGraph graph, Object cell) {
	do {
	    if (graph.isCellSelected(cell))
		return true;
	    cell = graph.getModel().getParent(cell);
	} while (cell != null);
	return false;
    }

    /**
     * Undos the last operation in the specified diagram pane.
     * 
     * @param diagramPane
     *            The diagram pane to perform the operation in.
     */
    protected void doUndo(RMAppDiagramPane diagramPane) {
	GraphUndoManager manager = diagramPane.getGraphUndoManager();
	GraphLayoutCache cache = diagramPane.getGraph().getGraphLayoutCache();
	if (manager.canUndo(cache))
	    manager.undo(cache);
	
    }

    /**
     * Undos the last operation in the specified diagram pane.
     * 
     * @param diagramPane
     *            The diagram pane to perform the operation in.
     */
    protected void doRedo(RMAppDiagramPane diagramPane) {
	GraphUndoManager manager = diagramPane.getGraphUndoManager();
	GraphLayoutCache cache = diagramPane.getGraph().getGraphLayoutCache();
	if (manager.canRedo(cache))
	    manager.redo(cache);
    }

    /**
     * Starts editing the selection cell in the specified graph.
     * 
     * @param graph
     *            The graph to perform the operation in.
     */
    protected void doEdit(JGraph graph) {
	if (!graph.isSelectionEmpty())
	    graph.startEditingAtCell(graph.getSelectionCell());
    }

   
    /**
     * Returns the fallback action.
     * 
     * @return Returns the fallbackAction.
     */
    public Action getFallbackAction() {
	return fallbackAction;
    }

    /**
     * Bundle of all actions in this class.
     */
    public static class AllActions implements Bundle {

	/**
	 * Holds the actions.
	 */
	public RMAppAction actionEdit = new RMAppEditAction(NAME_EDIT), actionSelectAll = new RMAppEditAction(
		NAME_SELECTALL), actionClearSelection = new RMAppEditAction(NAME_CLEARSELECTION),
		actionSelectVertices = new RMAppEditAction(NAME_SELECTVERTICES),
		actionSelectEdges = new RMAppEditAction(NAME_SELECTEDGES),
		actionDeselectVertices = new RMAppEditAction(NAME_DESELECTVERTICES),
		actionDeselectEdges = new RMAppEditAction(NAME_DESELECTEDGES),
		actionInvertSelection = new RMAppEditAction(NAME_INVERTSELECTION), actionFind = new RMAppEditAction(
			NAME_FIND), actionFindAgain = new RMAppEditAction(NAME_FINDAGAIN),
		actionUndo = new RMAppEditAction(NAME_UNDO), actionRedo = new RMAppEditAction(NAME_REDO),
		actionCut = new RMAppEditAction(NAME_CUT), actionCopy = new RMAppEditAction(NAME_COPY),
		actionPaste = new RMAppEditAction(NAME_PASTE), actionDelete = new RMAppEditAction(NAME_DELETE);

	/*
	 * (non-Javadoc)
	 */
	public RMAppAction[] getActions() {
	    return new RMAppAction[] { actionEdit, actionSelectAll, actionClearSelection, actionSelectVertices,
		    actionSelectEdges, actionDeselectVertices, actionDeselectEdges, actionInvertSelection, actionFind,
		    actionUndo, actionRedo, actionFindAgain, actionCut, actionCopy, actionPaste, actionDelete };
	}

	/*
	 * (non-Javadoc)
	 */
	public void update() {
	    JGraph graph = getPermanentFocusOwnerGraph();
	    RMAppDiagramPane diagramPane = getPermanentFocusOwnerDiagramPane();
	    if (diagramPane != null) {
		GraphUndoManager manager = diagramPane.getGraphUndoManager();
		GraphLayoutCache glc = diagramPane.getGraph().getGraphLayoutCache();
		actionUndo.setEnabled(manager.canUndo(glc));
		actionRedo.setEnabled(manager.canRedo(glc));
	    } else {
		actionUndo.setEnabled(false);
		actionRedo.setEnabled(false);
	    }
	    boolean isGraphFocused = graph != null;
	    boolean isGraphEditable = isGraphFocused && graph.isEditable();
	    boolean isSelectionEmpty = !isGraphFocused || graph.isSelectionEmpty();
	    // JGraphpadLibraryPane libraryPane =
	    // RMAppFileAction.getPermanentFocusOwnerLibraryPane();
	    //boolean isEntrySelected; // = libraryPane != null &&
	    // !libraryPane.isSelectionEmpty();

	    actionEdit.setEnabled(isGraphEditable);
	    actionFind.setEnabled(isGraphFocused);
	    actionFindAgain.setEnabled(isGraphFocused && lastSearchPattern != null);
	    actionCut.setEnabled(!isSelectionEmpty); // || isEntrySelected);
	    actionCopy.setEnabled(!isSelectionEmpty); // || isEntrySelected);
	    actionPaste.setEnabled(isGraphFocused);
	    actionDelete.setEnabled(!isSelectionEmpty); // || isEntrySelected);
	}

    }

};
