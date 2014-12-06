package edu.cmu.sphinx.demo.IVRSystem;

import java.util.ArrayList;

public class Field {
	String name;
	String prompt;
	ArrayList<Choice> choices;
	String cond;

	public Field(String name, String prompt, ArrayList<Choice> choices,
			String cond) {
		this.name = name;
		this.prompt = prompt;
		this.choices = choices;
		this.cond = cond;
	}
}
