package com.rm.app.graph;

import java.io.Serializable;

import org.jgraph.graph.DefaultEdge;

public class RMGraphEdge extends DefaultEdge implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String sourceId;
    
    private String targetId;
    
    private String sourceType;
    
    private String targetType; 

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
       
}
