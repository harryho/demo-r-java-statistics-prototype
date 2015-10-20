package com.rm.app.reg;

public class RegMain {

	public static void main(String[] args) {

		boolean verok = RDetecter.checkdRVersion();
		if (verok) {
			RDetecter.setR2SystemPath();
		}

	}

}
