package edu.cmu.sphinx.demo.IVRSystem;

import org.mozilla.javascript.*;

import java.util.ArrayList;

import edu.cmu.sphinx.demo.IVRSystem.*;

public class Dialog {
	private ArrayList<Field> fields;
	private int curFieldIndex;
	private Context ctx;
	private Scriptable scope;
	
	public Dialog(ArrayList<Field> fields) {
		this.fields = fields;
		ctx = Context.enter();
		ctx.setLanguageVersion(Context.VERSION_1_2);
		scope = ctx.initStandardObjects();
	}

	private boolean evalBool(String cond) throws JavaScriptException {
		cond = "(" + cond + ") ? true : false;";
		return (Boolean) evalJS(cond, "ifcond");
	}
	
	private boolean isJSUndefined(String src) throws JavaScriptException {
		String cond = "(typeof(" + src + ")) == 'undefined'";
		return evalBool(cond);
	}
	private Object evalJS(String source, String name) throws JavaScriptException {
		return ctx.evaluateString(scope, source, name, 1, null);

	}
	
	// replace all "$$js$$" with the value returned by evaluating js
	private String replaceStringExpr(String inp) {
		try {
			String retArr[] = inp.split("\\$\\$", -1);
			if (retArr.length % 2 != 1) {
				// if the length is not odd, that means some '$$' was not closed
				// FIXME don't fail silently
			} else {
				inp = "";
				for (int i = 0; i < retArr.length; i++) {
					if (i % 2 == 0) {
						inp += retArr[i];
					} else {
						inp += evalJS(retArr[i], "retArr[" + i + "]");
					}
				}
			}
		} catch (JavaScriptException e) {
			e.printStackTrace();
		}
		
		return inp;
	}
	// current field prompt has been spoken
	// current field has choices (waiting for input)
	// the user-input is for the current field
	private String updateCurrentField(String userInput) {
		String ret = "";

		// normalize userInput
		userInput = userInput.trim().toLowerCase();
		
		Field field = fields.get(curFieldIndex);

		// is the userInput one of the valid choices?
		int i; Choice c = null;
		for (i = 0; i < field.choices.size(); i++) {
			c = field.choices.get(i);
			if (userInput.equals(c.name.trim().toLowerCase())) break;
		}
		
		// if the userInput is NOT a valid choice, bail out
		if (i == field.choices.size()) {
			return ret;
		}
		
		try {
			// Evaluate the tag, which will (hopefully) assign to the '$' var
			evalJS(c.tag, c.name + "_tag");
			// get the value of the '$' var and assign it to a variable named after the field
			evalJS(field.name + " = $;", "assign_field_var");
			
			// we only support one kind of if-condition, that containing
			// a name clear list
			if (field.onFilledIsCond) {
				// evaluate the field condition
				boolean condSatisfied = evalBool(field.onFilledCond.cond);
				if (condSatisfied) {
					String src = "";
					// create the javascript that will clear the variables
					for (String s: field.onFilledCond.clearList) {
						src += "delete " + s + ";\n";
					}
					// clear the variables
					evalJS(src, "clearlist");
				}
			} else {
				// it's just a string to be said.
				ret += replaceStringExpr(field.onFilledText);
			}
		} catch (JavaScriptException e) {
			e.printStackTrace();
		}

		// continue on the form at a field that has its variable set
		// so that if a field clears variables, then processing goes back
		// to the respective fields
		int lastFilledField = 0;
		try {
			for (; lastFilledField < fields.size(); lastFilledField++) {
				Field f = fields.get(lastFilledField);
                                if (f.name != null && !f.name.equals("") && isJSUndefined(f.name)) {
					lastFilledField--;
					break;
				}
			}
			curFieldIndex = lastFilledField + 1;
                        if(curFieldIndex>fields.size()) curFieldIndex = fields.size()-1;
		} catch (JavaScriptException e) {
			e.printStackTrace();
		}

		return ret;
	}

	// skip till a field that requires input
	// return all prompts including current
	private String skipTillInput() {
		String ret = "";
		
		while (!isOver()) {
			Field f = fields.get(curFieldIndex);
			ret += replaceStringExpr(f.prompt + "\n");

			if (f.choices != null && f.choices.size() != 0) {
				break;
			}

			curFieldIndex++;
		} 

		return ret;
	}
	
	public String begin() {
		curFieldIndex = 0;

		return skipTillInput();
	}
	
	public boolean isOver() {
		return curFieldIndex >= fields.size();
	}

	public String interact(String userInput) {
		if (isOver()) {
			return null;
		}

		String ret = updateCurrentField(userInput);

		if (!isOver()) {
			ret += "\n" + skipTillInput();
		}

		return ret;
	}
	
	public void loadScript(String src) {
		try {
			ctx.evaluateString(scope, src, "user_script", 1, null);
		} catch (JavaScriptException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ArrayList<Field> fields = new ArrayList<Field>();
		ArrayList<Choice> choices;
		ArrayList<String> clearList;
		
		fields.add(new Field("block", "Ya alf welcome!", null, "", ""));
		choices = new ArrayList<Choice>();
		choices.add(new Choice("big", " $='big'; "));
		choices.add(new Choice("regular", " $='regular'; "));
		fields.add(new Field("size", "What size!?", choices, "", "OK!"));
		
		choices = new ArrayList<Choice>();
		choices.add(new Choice("yes", " $=true; "));
		choices.add(new Choice("no", " $=false; "));
		clearList = new ArrayList<String>();
		clearList.add("size");
		clearList.add("confirm_size");
		fields.add(new Field("confirm_size", "Sure about $$size$$?", choices, "", new IfCond("!confirm_size", clearList)));
		
		Dialog d = new Dialog(fields);
		System.out.println(d.begin());
		System.out.println(d.interact("big"));
		System.out.println(d.interact("no"));
	}
}
