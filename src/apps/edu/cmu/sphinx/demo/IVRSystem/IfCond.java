package edu.cmu.sphinx.demo.IVRSystem;

import java.util.ArrayList;

public class IfCond {
	String cond;
	ArrayList<String> clearList;
	boolean isCond;
	String text;

	public IfCond(String cond, ArrayList<String> clearList) {
		this.cond = cond;
		this.clearList = clearList;
		isCond = true;
	}

	public IfCond(String text) {
		this.text = text;
		isCond = false;
	}
}
