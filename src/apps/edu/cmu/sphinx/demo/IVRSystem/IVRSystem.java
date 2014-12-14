package edu.cmu.sphinx.demo.IVRSystem;

import java.util.ArrayList;

public class IVRSystem {

	private static String path = "/home/hossam/Desktop/University/Advanced Media Lab/Mini Project 1/sphinx4-1.0beta5-src/sphinx4-1.0beta5/src/apps/edu/cmu/sphinx/demo/IVRSystem/dialog.vxml";

	public IVRSystem() {
		super();
	}

	public static void testDialog() {
		ArrayList<Field> fields = new ArrayList<Field>();
		ArrayList<Choice> choices;
		ArrayList<String> clearList;
		
		fields.add(new Field("", "Ya alf welcome!", null, "", ""));
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
		System.out.println(d.interact("regular"));
		System.out.println(d.interact("yes"));	
	}
	
	public static void testParser() {
		String testFile = "../../mini_project3/tests/dialog.vxml";
		System.out.println("Parser test on " + testFile);
		Parser2 p = new Parser2(testFile);
		Node root = p.getRootNode();
		
		System.out.println("Parsed VoiceXML:");
		System.out.println(root);
	}
	
	public static void testGrammar() throws Exception {
		String testFile = "../../mini_project3/tests/grammar_test.vxml";
		System.out.println("Grammar test on " + testFile);
		
		Parser2 p = new Parser2(testFile);
		Node g1 = p.getNodeById("g1");
		Grammar gram = new Grammar(g1);
		System.out.println("Grammar g1:\n" + gram);

		System.out.println("g1 tests:\n");
		String tests[] = {"big", "regular", "big pizza"};
		for (String test: tests) {
			ArrayList<String> tags = gram.match(test);
			System.out.println(test + ":");
			if (tags == null) {
				System.out.println("    NO MATCH!");
			} else {
				for (String tag: tags) {
					System.out.println("    " + tag);
				}
			}
		}
		
	}
	
	public static void main(String args[]) {
		testParser();
		
		try {
			testGrammar();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}