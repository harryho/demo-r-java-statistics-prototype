package com.rm.app.graph;

import java.io.Serializable;
import java.util.List;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphModel;

public class RMModel extends DefaultGraphModel implements Serializable {

	private String version = "0.2";

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RMModel() {
		super();
	}

	public RMModel(List roots, AttributeMap attributes) {
		super(roots, attributes);
	}

	public boolean acceptsSource(Object edge, Object port) {
		return (port != null);
	}

	public boolean acceptsTarget(Object edge, Object port) {
		return (port != null);
	}

}
