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

public class Parser extends DefaultHandler {
	String path;
	private static ArrayList<Field> fields = new ArrayList<Field>();

	private static Field current_field = new Field();
	private static Choice current_choice = new Choice("", "");
	private static IfCond current_ifCond = new IfCond();

	boolean contains_if = false;
	boolean item = false;
	boolean field = false;
	boolean prompt = false;;
	boolean tag = false;
	boolean isFilled = false;

	private static String[] pc_text;
	private static String[] user_text;

	public Parser(String path) {
		this.path = path;
	}

	public ArrayList<Field> parse() {
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
		for (int j = 0; j < fields.size(); j++) {
			// System.out.println(fields.size());
			current_field = fields.get(j);
			System.out.println("field name: " + current_field.name);
			System.out.println("field prompt: " + current_field.prompt);
			for (int i = 0; i < current_field.choices.size(); i++) {
				System.out.println("field choice: " + i + " name: "
						+ current_field.choices.get(i).name + " tag: "
						+ current_field.choices.get(i).tag);
			}
			System.out.println("field cond: " + current_field.cond);
			System.out.println("field onFilledCond cond: "
					+ current_field.onFilledCond.cond);
			for (int i = 0; i < current_field.onFilledCond.clearList.size(); i++) {
				System.out.println("field onfilledcond clearList: " + i + " "
						+ current_field.onFilledCond.clearList.get(i));
			}
			System.out.println("field onFIlledIsCond: "
					+ current_field.onFilledIsCond);
			System.out.println("field filledText: "
					+ current_field.onFilledText);
			System.out.println();
		}
		return fields;
	}

	public void startDocument() {
		// System.out.println("Start document");
	}

	public void endDocument() {
		// System.out.println("End document");
	}

	public void startElement(String uri, String name, String qName,
			Attributes atts) {
		// System.out.println("Start element:   {" + uri + "}" + name + " >>> "
		// + qName);
		if ("field" == name || "block" == name) {
			if ("block" == name) {
				prompt = true;
			}
			field = true;
			current_field = new Field();
			current_field.name = atts.getValue("name");
			current_field.cond = atts.getValue("cond");
		} else if ("prompt" == name) {
			prompt = true;
		} else if ("value" == name) {
			current_field.prompt = current_field.prompt + "$$"
					+ atts.getValue(0) + "$$";
		} else if ("item" == name) {
			item = true;
		} else if ("tag" == name) {
			tag = true;
		} else if ("filled" == name) {
			isFilled = true;
		} else if ("if" == name) {
			contains_if = true;
			current_ifCond.cond = atts.getValue(0);
		} else if ("clear" == name) {
			current_ifCond.clearList = new ArrayList<String>(Arrays.asList(atts
					.getValue(0).split(" ")));
		} else {
		}
	}

	public void endElement(String uri, String name, String qName) {
		// System.out.println("End element:   {" + uri + "}" + name + " >>> "
		// + qName);
		if ("filled" == name && contains_if) {
			current_field.onFilledCond = current_ifCond;
			current_ifCond = new IfCond();
			contains_if = false;
			current_field.onFilledIsCond = true;
			isFilled = false;
		} else if ("filled" == name && !contains_if) {
			isFilled = false;
		} else if ("tag" == name) {
			tag = false;
		} else if ("item" == name) {
			current_field.choices.add(current_choice);
			current_choice = new Choice();
			item = false;
		} else if ("prompt" == name) {
			prompt = false;
		} else if ("field" == name || "block" == name) {
			if ("block" == name) {
				prompt = false;
			}
			fields.add(current_field);
		} else {
		}
	}

	public void characters(char ch[], int start, int length) {
		// System.out.print("Characters:    \"");
		for (int i = start; i < start + length; i++) {
			if (tag) {
				current_choice.tag += ch[i];
			} else if (item) {
				current_choice.name += ch[i];
			} else if (prompt) {
				current_field.prompt += ch[i];
			} else if (isFilled && !contains_if) {
				current_field.onFilledText += ch[i];
			}
		}
	}
}
