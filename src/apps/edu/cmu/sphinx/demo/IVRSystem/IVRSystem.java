package edu.cmu.sphinx.demo.IVRSystem;

public class IVRSystem {

	private static String path = "/home/hossam/Desktop/University/Advanced Media Lab/Mini Project 1/sphinx4-1.0beta5-src/sphinx4-1.0beta5/src/apps/edu/cmu/sphinx/demo/IVRSystem/dialog.vxml";

	public IVRSystem() {
		super();
	}

	public static void main(String args[]) {
		Parser parser = new Parser(path);
		parser.parse();
	}

}