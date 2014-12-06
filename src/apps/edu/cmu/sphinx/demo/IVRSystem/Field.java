package edu.cmu.sphinx.demo.IVRSystem;

import java.util.ArrayList;

public class Field {
	String name;
	String prompt;
	ArrayList<Choice> choices;
	String cond;
	IfCond onFilledCond;
	boolean onFilledIsCond;
	String onFilledText;

	public Field(String name, String prompt, ArrayList<Choice> choices,
			String cond, IfCond onFilledCond) {
		this.name = name;
		this.prompt = prompt;
		this.choices = choices;
		this.cond = cond;
		this.onFilledCond = onFilledCond;
		onFilledIsCond = true;
	}

	public Field(String name, String prompt, ArrayList<Choice> choices,
			String cond, String onFilledText) {
		this.name = name;
		this.prompt = prompt;
		this.choices = choices;
		this.cond = cond;
		this.onFilledText = onFilledText;
		onFilledIsCond = false;
	}
}
