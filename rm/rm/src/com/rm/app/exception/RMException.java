package com.rm.app.exception;

public class RMException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RMException() {
		super();
	}

	public RMException(String mes) {
		super(mes);
	}

	public RMException(Throwable tab) {
		super(tab);
	}
}
