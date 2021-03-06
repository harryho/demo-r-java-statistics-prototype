package com.rm.app.ui.action;

import java.awt.Component;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.rm.app.RMResources;
import com.rm.app.graph.RMGraph;
import com.rm.app.ui.RMAppDiagramPane;

public abstract class RMAppAction extends AbstractAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Bean property name for <code>isSelected</code>
     */
    public final static String PROPERTY_ISSELECTED = new String("isSelected");

    /**
     * Bean property name for <code>isVisible</code>
     */
    public final static String PROPERTY_ISVISIBLE = new String("isVisible");

    /**
     * Holds the toggleable state.
     */
    protected boolean isToggleAction;

    public void setEnabled(boolean newValue) {
	super.setEnabled(newValue);
    }

    /**
     * Constructs a new action for the specified name
     * 
     * @param name
     *            The name of the new action.
     */
    public RMAppAction(String name) {
	this(name, false);
    }

    /**
     * Constructs a new action for the specified <code>name</code> and
     * <code>isToggleAction</code> state.
     * 
     * @param name
     *            The name of the new action.
     * @param isToggleAction
     *            Whether the action is a toggle action.
     */
    public RMAppAction(String name, boolean isToggleAction) {
	super(name);
	setToggleAction(isToggleAction);
    }

    /**
     * Returns the name.
     */
    public String getActionObjectName() {
    	return String.valueOf(getValue(Action.NAME));
    }

    /**
     * Returns whether the UI elements should display the selection state.
     * 
     * @return Returns true if the action is toggleable.
     */
    public boolean isToggleAction() {
	return isToggleAction;
    }

    /**
     * Sets whether the UI elements should display the selection state.
     * 
     * @param isToggleAction
     *            The isToggleAction state to set.
     */
    public void setToggleAction(boolean isToggleAction) {
	this.isToggleAction = isToggleAction;
    }

    /**
     * Returns the selection state.
     * 
     * @return Returns true if the action is selected.
     */
    public boolean isSelected() {
	Boolean isSelected = (Boolean) getValue(PROPERTY_ISSELECTED);
	if (isSelected != null)
	    return isSelected.booleanValue();
	return false;
    }

    /**
     * Sets the selection state. Dispatches a change event.
     * 
     * @param selected
     *            The selected state to set.
     * 
     * @see Action#putValue(java.lang.String, java.lang.Object)
     */
    public void setSelected(boolean selected) {
	putValue(PROPERTY_ISSELECTED, new Boolean(selected));
    }

    /**
     * Returns the visible state.
     * 
     * @return Returns true if the action is visible.
     */
    public boolean isVisible() {
	Boolean isVisible = (Boolean) getValue(PROPERTY_ISVISIBLE);
	if (isVisible != null)
	    return isVisible.booleanValue();
	return false;
    }

    /**
     * Sets the visible state. Dispatches a change event.
     * 
     * @param visible
     *            The visible state to set.
     * 
     * @see Action#putValue(java.lang.String, java.lang.Object)
     */
    public void setVisible(boolean visible) {
	putValue(PROPERTY_ISVISIBLE, new Boolean(visible));
    }

    /**
     * Shortcut method to {@link JGraphEditorResources#getString(String)}.
     * 
     * @param key
     *            The key to return the resource string for.
     */
    public static String getString(String key) {
	return RMResources.getString(key);
    }

    /**
     * Returns the frame for <code>event</code> if the even source is a
     * Component or the active frame.
     * 
     * @param event
     *            The event to get the frame from.
     * @return Returns the frame for <code>event</code> or the active frame.
     * 
     * @see #getActiveFrame()
     */
    public static Frame getFrame(ActionEvent event) {
	Window wnd = null;
	if (event != null && event.getSource() instanceof Component)
	    wnd = SwingUtilities.windowForComponent((Component) event.getSource());
	Frame frame = (wnd instanceof Frame) ? (Frame) wnd : getActiveFrame();
	return frame;
    }

    /**
     * Returns the permanent focus owner.
     * 
     * @return Returns the permanent focus owner.
     * 
     * @see KeyboardFocusManager#getPermanentFocusOwner()
     */
    public static Component getPermanentFocusOwner() {
    	return KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
    }

    /**
     * Returns the permanent focus owner or the parent scroll pane of it.
     * 
     * @return Returns the permanent focus owner or its parent scroll pane.
     * 
     * @see KeyboardFocusManager#getPermanentFocusOwner()
     */
    // public static Component getPermanentFocusOwnerOrParent()
    // {
    // Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager()
    // .getPermanentFocusOwner();
    // Component tmp = JGraphEditorNavigator.getParentScrollPane(comp);
    // if (tmp != null)
    // comp = tmp;
    // return comp;
    // }
    /**
     * Returns the permanent focus owner graph.
     * 
     * @return Returns the permanent focus owner graph.
     * 
     * @see KeyboardFocusManager#getPermanentFocusOwner()
     */
    public static RMGraph getPermanentFocusOwnerGraph() {
	return getParentGraph(KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner());
    }

    // /**
    // * Returns the diagram for the diagram pane that has the focus.
    // *
    // * @return Returns the focused diagram.
    // */
    // public static RMAppDiagram getPermanentFocusOwnerDiagram()
    // {
    // RMAppDiagramPane diagramPane =
    // getPermanentFocusOwnerDiagramPane();
    // if (diagramPane != null)
    // return diagramPane.getDiagram();
    // return null;
    // }
    //
    // /**
    // * Returns the diagram pane that contains the permanent focus owner.
    // */
    // public static RMAppDiagramPane getPermanentFocusOwnerDiagramPane()
    // {
    // return RMAppDiagramPane
    // .getParentDiagramPane(getPermanentFocusOwner());
    // }

    /**
     * Returns the first active frame.
     * 
     * @return Returns the active frame.
     * 
     * @see Window#isActive()
     */
    public static Frame getActiveFrame() {
	Frame[] frames = JFrame.getFrames();
	for (int i = 0; i < frames.length; i++)
	    if (frames[i].isActive())
		return frames[i];

	for (int i = 0; i < frames.length; i++)
	    if (frames[i].isVisible())
		return frames[i];

	return null;
    }

//    public static RMAppDiagram getPermanentFocusOwnerDiagram() {
//	RMAppDiagramPane diagramPane = getPermanentFocusOwnerDiagramPane();
//	if (diagramPane != null)
//	    return diagramPane.getDiagram();
//	return null;
//    }

    /**
     * Returns the diagram pane that contains the permanent focus owner.
     */
    public static RMAppDiagramPane getPermanentFocusOwnerDiagramPane() {
	return RMAppDiagramPane.getParentDiagramPane(getPermanentFocusOwner());
    }

    /**
     * Returns the JGraphpadPane inside the active frame.
     * 
     * @return Returns the JGraphpad pane for the given frame.
     */
    // public static JGraphpadPane getJGraphpadPane() {
    // Frame frame = getActiveFrame();
    // if (frame instanceof JFrame) {
    // return getJGraphpadPane((JFrame) frame);
    // }
    //
    // return null;
    // }
    /**
     * Returns the JGraphpadPane inside the given frame.
     * 
     * @return Returns the JGraphpad pane for the given frame.
     */
    // public static JGraphpadPane getJGraphpadPane(JFrame frame)
    // {
    // int childCount = frame.getContentPane().getComponentCount();
    //
    // for (int i = 0; i < childCount; i++)
    // {
    // try
    // {
    // Component comp = frame.getContentPane().getComponent(i);
    //				
    // if (comp instanceof JGraphpadPane)
    // {
    // return (JGraphpadPane) comp;
    // }
    // }
    // catch (Exception e)
    // {
    // e.printStackTrace();
    // }
    // }
    //
    // return null;
    // }
    /**
     * Returns the parent diagram pane of the specified component, or the
     * component itself if it is a editor diagram pane.
     * 
     * @return Returns the parent editor diagram pane of <code>component</code>.
     */
    public static RMGraph getParentGraph(Component component) {
	while (component != null) {
	    if (component instanceof RMGraph)
		return (RMGraph) component;
	    component = component.getParent();
	}
	return null;
    }

    /**
     * An interface to manage a set of actions as a single entity.
     * 
     * @see RMEditorKit#addBundle(RMAppAction.Bundle)
     */
    public interface Bundle {

	/**
	 * Returns all actions contained in the bundle.
	 * 
	 * @return Returns all actions.
	 */
	public RMAppAction[] getActions();

	/**
	 * Updates all action states.
	 */
	public void update();

    }

}
