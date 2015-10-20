package com.rm.app.graph;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jgraph.graph.DefaultGraphCell;

public class RMGraphCell extends DefaultGraphCell implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
	 * 
	 */
    // private static final long serialVersionUID = 1L;
    private String classifierKey;

    private String key;

    private String label;

    private String id;

    private String name;

    private Map<String, Attribute> dataMap = new HashMap<String, Attribute>();

    private Double posX;

    private Double posY;

    private String viewLabel;

    private String fromDataCache;

    public String getFromDataCache() {
	return fromDataCache;
    }

    public void setFromDataCache(String fromDataCache) {
	this.fromDataCache = fromDataCache;
    }

    public String getViewLabel() {
	return viewLabel;
    }

    public void setViewLabel(String viewLabel) {
	this.viewLabel = viewLabel;
    }

    public Double getPosX() {
	return posX;
    }

    public void setPosX(Double posX) {
	this.posX = posX;
    }

    public Double getPosY() {
	return posY;
    }

    public void setPosY(Double posY) {
	this.posY = posY;
    }

    public RMGraphCell(String object) {
	super(object);
    }

    public RMGraphCell() {
	super();
    }

    public Attribute newAttribute() {
	return new Attribute();
    }

    public String getLabel() {
	return label;
    }

    public void setLabel(String label) {
	this.label = label;
    }

    public String getId() {
	return id;
    }

    public void setId(String name) {
	this.id = name;
    }

    public Map<String, Attribute> getDataMap() {
	return dataMap;
    }

    public void setDataMap(Map<String, Attribute> dataMap) {
	this.dataMap = dataMap;
    }

    public String getParaStringValue(String key) {
	Attribute att = dataMap.get(key);
	if (att == null) {
	    return null;
	} else {
	    return (String) att.getValue();
	}
    }

    public String getClassifierKey() {
	return classifierKey;
    }

    public void setClassifierKey(String classifierKey) {
	this.classifierKey = classifierKey;
    }

    public String getKey() {
	return key;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Object clone() {
	Object object = super.clone();
	Map map = new HashMap();
	RMGraphCell cell = (RMGraphCell) object;
	Map oldMap = cell.getDataMap();

	Set keySet = oldMap.keySet();
	Iterator keyIterator = keySet.iterator();
	int rows = keySet.size();
	for (int k = 0; k < rows; k++) {
	    String key = (String) keyIterator.next();
	    Attribute att = (Attribute) oldMap.get(key);
	    Attribute att2 = (Attribute) att.clone();
	    map.put(att2.getId(), att2);
	}
	cell.setDataMap(map);
	return cell;
    }
}
