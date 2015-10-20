package com.rm.app.graph;

import java.io.Serializable;

public class Attribute implements Serializable, Cloneable, Comparable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String name;

	private boolean isVisible;

	private boolean isEditable;

	private boolean isValid;

	private Object value;

	private String discription;

	private int displayOrder = 0;

	public int getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(int displayOrder) {
		this.displayOrder = displayOrder;
	}

	private String type;

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDiscription() {
		return discription;
	}

	public void setDiscription(String discription) {
		this.discription = discription;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}

	}

	public int compareTo(Object arg0) {
		Attribute a2 = (Attribute) arg0;
		return this.getDisplayOrder() - a2.getDisplayOrder();
	}

}
