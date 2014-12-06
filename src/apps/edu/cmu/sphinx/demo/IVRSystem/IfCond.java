package edu.cmu.sphinx.demo.IVRSystem;

import java.util.ArrayList;

public class IfCond {
	String cond;
	ArrayList<String> clearList;

	public IfCond(String cond, ArrayList<String> clearList) {
		this.cond = cond;
		this.clearList = clearList;
	}

	public IfCond() {
		this.cond = "";
		this.clearList = new ArrayList<String>();
	}
}
