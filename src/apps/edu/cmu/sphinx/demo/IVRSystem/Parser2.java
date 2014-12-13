package edu.cmu.sphinx.demo.IVRSystem;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

public class Parser2 extends DefaultHandler {
	private ArrayList<Node> nodes;
	private Node root;
	String path;

	public Parser2(String path) {
		this.path = path;
	}

	public Node parse() {
		root = new Node("root");
		nodes = new ArrayList<Node>();
		nodes.add(root);
		
		try {
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);
			FileReader r = new FileReader(path);
			xr.parse(new InputSource(r));
		} catch (Exception e) {
			// e.setStackTrace(null);
			e.printStackTrace();
			// System.out.println(e.getMessage());
		}
//		for (int j = 0; j < fields.size(); j++) {
//			// System.out.println(fields.size());
//			current_field = fields.get(j);
//			System.out.println("field name: " + current_field.name);
//			System.out.println("field prompt: " + current_field.prompt);
//			for (int i = 0; i < current_field.choices.size(); i++) {
//				System.out.println("field choice: " + i + " name: "
//						+ current_field.choices.get(i).name + " tag: "
//						+ current_field.choices.get(i).tag);
//			}
//			System.out.println("field cond: " + current_field.cond);
//			System.out.println("field onFilledCond cond: "
//					+ current_field.onFilledCond.cond);
//			for (int i = 0; i < current_field.onFilledCond.clearList.size(); i++) {
//				System.out.println("field onfilledcond clearList: " + i + " "
//						+ current_field.onFilledCond.clearList.get(i));
//			}
//			System.out.println("field onFIlledIsCond: "
//					+ current_field.onFilledIsCond);
//			System.out.println("field filledText: "
//					+ current_field.onFilledText);
//			System.out.println();
//		}
		return root;
	}

	public void startDocument() {
		System.out.println("Start document");
	}

	public void endDocument() {
		System.out.println("End document");
	}

	public void startElement(String uri, String name, String qName,
			Attributes atts) {
		Node n = new Node(name, atts);
		nodes.add(n);
		
		System.out.println("Start element:   {" + uri + "}" + name + " >>> "
		+ qName);
	}
	
	public void characters(char ch[], int start, int length) {
		String s = new String(ch, start, length);
		System.out.print("Characters: " + s);
		Node n = nodes.get(nodes.size()-1);
		n.text += s;
	}
	
	public void endElement(String uri, String name, String qName) {
		System.out.println("End element:   {" + uri + "}" + name + " >>> "
		+ qName);
		// FIXME special case <value /> tags
		Node n = nodes.remove(nodes.size()-1);
		Node p = nodes.get(nodes.size()-1);
		p.children.add(n);
	}
}
