package com.rm.app.util;


public class LinkedListNode {
	
	public LinkedListNode perNode;

	public LinkedListNode nextNode;

	public Object value;

	//
	public long timestamp;

	public LinkedListNode(Object value, LinkedListNode perNode,
			LinkedListNode nextNode) {
		this.value = value;
		this.perNode = perNode;
		this.nextNode = nextNode;
	}

	public void removeFormList() {
		this.perNode.nextNode = this.nextNode;
		this.nextNode.perNode = this.perNode;
	}

}
