package com.rm.app.util;

public class LinkedList {

	public LinkedListNode headNode = new LinkedListNode("head", null, null);

	public LinkedList() {
		headNode.nextNode = headNode.perNode = headNode;
	}

	public LinkedListNode addFirst(LinkedListNode node) {
		if (node != null) {
			headNode.nextNode.perNode = node;
			node.nextNode = headNode.nextNode;
			headNode.nextNode = node;
			node.perNode = headNode;
		}
		return node;
	}

	public LinkedListNode addFirst(Object value) {
		LinkedListNode node = new LinkedListNode(value, headNode,
				headNode.nextNode);
		node.perNode.nextNode = node;
		node.nextNode.perNode = node;
		return node;
	}

	public LinkedListNode addLast(LinkedListNode node) {
		headNode.perNode.nextNode = node;
		node.perNode = headNode.perNode;
		node.nextNode = headNode;
		headNode.perNode = node;
		return node;
	}

	public LinkedListNode addLast(Object value) {
		LinkedListNode node = new LinkedListNode(value, headNode.perNode,
				headNode);
		node.perNode.nextNode = node;
		node.nextNode.perNode = node;
		return node;
	}

	public LinkedListNode getLast() {
		LinkedListNode node = headNode.perNode;
		if (node == headNode)
			return null;
		return node;
	}

	public LinkedListNode getFirst() {
		LinkedListNode node = headNode.nextNode;
		if (node == headNode)
			return null;
		return node;
	}

	public void clear() {
		LinkedListNode node = getLast();
		while (node != null) {
			node.removeFormList();
			node = getLast();
		}
		headNode.nextNode = headNode.perNode = headNode;
	}

	public String toString() {
		LinkedListNode node = headNode.nextNode;
		StringBuffer sb = new StringBuffer();
		while (node != headNode) {
			sb.append(node.value == null ? "" : node.value).append(";");
			node = node.nextNode;
		}
		return sb.toString();
	}
}
