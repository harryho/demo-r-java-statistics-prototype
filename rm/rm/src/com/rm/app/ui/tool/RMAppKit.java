package com.rm.app.ui.tool;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.rm.app.ui.action.RMAppAction;

public class RMAppKit {

    /**
     * Holds the (name, action) and (name, tool) pairs respectively.
     */
    protected Map actions = new Hashtable(), tools = new Hashtable();

    /**
     * Holds all action bundles.
     */
    protected List bundles = new ArrayList(2);

    /**
     * Adds the specified action to the kit.
     * 
     * @param action
     *            The action to add.
     * @return Returns the previous action for the same name.
     * 
     * @see JGraphEditorAction#getActionObjectName()
     */
    public Object addAction(RMAppAction action) {
	if (action != null)
	    return actions.put(action.getActionObjectName(), action);
	return null;
    }

    /**
     * Adds all actions in the specified bundle and stores a reference to the
     * bundle for later update of the actions.
     * 
     * @param bundle
     *            The bundle to add the actions from.
     */
    public void addBundle(RMAppAction.Bundle bundle) {
	RMAppAction[] a = bundle.getActions();
	for (int i = 0; i < a.length; i++)
	    addAction(a[i]);
	bundles.add(bundle);
    }

    /**
     * Returns the action for the specified name or <code>null</code> if no such
     * action exists.
     * 
     * @param name
     *            The name that identifies the action.
     * @return Returns the action with the name <code>name</code> or
     *         <code>null</code>.
     */
    public RMAppAction getAction(String name) {
	if (name != null)
	    return (RMAppAction) actions.get(name);
	return null;
    }

    /**
     * Adds the specified tool to the kit.
     * 
     * @param tool
     *            The tool to add.
     * @return Returns the previous tool for the same name.
     * 
     * @see JGraphEditorTool#getName()
     */
    // public Object addTool(JGraphEditorTool tool) {
    // if (tool != null) {
    // return tools.put(tool.getName(), tool);
    // }
    // return null;
    // }
    /**
     * Returns the tool for the specified name or <code>null</code> if no such
     * tool exists.
     * 
     * @param name
     *            The name that identifies the tool.
     * @return Returns the tool with the name <code>name</code> or
     *         <code>null</code>.
     */
    // public JGraphEditorTool getTool(String name) {
    // if (name != null)
    // return (JGraphEditorTool) tools.get(name);
    // return null;
    // }
    /**
     * This is messaged from the application when the kit should update the
     * state of its actions and tools. This implementation updates all
     * registered bundles.
     * 
     * @see JGraphEditorAction.Bundle#update()
     */
    public void update() {
	Iterator it = bundles.iterator();
	while (it.hasNext())
	    ((RMAppAction.Bundle) it.next()).update();
    }

    /**
     * @return Returns the actions.
     */
    public Map getActions() {
	return actions;
    }

    /**
     * @param actions
     *            The actions to set.
     */
    public void setActions(Map actions) {
	this.actions = actions;
    }

    /**
     * @return Returns the bundles.
     */
    public List getBundles() {
	return bundles;
    }

    /**
     * @param bundles
     *            The bundles to set.
     */
    public void setBundles(List bundles) {
	this.bundles = bundles;
    }

    /**
     * @return Returns the tools.
     */
    public Map getTools() {
	return tools;
    }

    /**
     * @param tools
     *            The tools to set.
     */
    public void setTools(Map tools) {
	this.tools = tools;
    }
}
