package edu.cmu.sphinx.demo.IVRSystem;

import java.util.ArrayList;
import java.util.Hashtable;

import org.xml.sax.Attributes;

public class Node {
	public ArrayList<Node> children;
	public String text;
	public String type;
	public Hashtable<String, String> atts;
	
	public Node(String type, Attributes atts) {
		this.type = type;
		this.text = "";
		this.atts = new Hashtable<String, String>();

		for (int i = 0; i < atts.getLength(); i++) {
			this.atts.put(atts.getQName(i), atts.getValue(i));
		}

		children = new ArrayList<Node>();
	}
	
	public Node(String type) {
		this.type = type;
		this.text = "";
		children = new ArrayList<Node>();
	}
	
	public String toString(int level) {
		String str;
		String indent = "";
		for (int i = 0; i < level; i++) {
			indent += "   ";
		}
		str = indent + "<" + this.type + "> " + this.text + "\n";

		for (Node c: children) {
			str += c.toString(level+1);
		}
		
		str += "\n" + indent + "</" + this.type + ">\n";
		return str;
	}
	
	public String getAttr(String name) {
		return atts.get(name);
	}

	@Override
	public String toString() {
		return toString(0);	
	}
}
