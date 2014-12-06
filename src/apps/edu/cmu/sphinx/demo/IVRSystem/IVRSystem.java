package edu.cmu.sphinx.demo.IVRSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import edu.cmu.sphinx.demo.helloworld.HelloWorld;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

import java.io.File;

public class IVRSystem {

	private static String path = "/home/hossam/Desktop/University/Advanced Media Lab/Mini Project 1/sphinx4-1.0beta5-src/sphinx4-1.0beta5/src/apps/edu/cmu/sphinx/demo/IVRSystem/dialog.vxml";
	private static String[] text;
	private static String[] pc_text;
	private static String[] user_text;

	public static void main(String[] args) throws IOException {
		testing();

		text = readFile();
		parseFile();

		ConfigurationManager cm;
		if (args.length > 0) {
			cm = new ConfigurationManager(args[0]);
		} else {
			cm = new ConfigurationManager(
					HelloWorld.class.getResource("helloworld.config.xml"));
		}
		Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
		Microphone microphone = (Microphone) cm.lookup("microphone");
		if (!microphone.startRecording()) {
			System.out.println("Cannot start microphone.");
			recognizer.deallocate();
			System.exit(1);
		}
		// welcoming customer
		System.out.println(pc_text[0]);
		// starting the conversation
		for (int i = 1; i < pc_text.length - 1; i++) {
			System.out.println(pc_text[i]);
			Result result = recognizer.recognize();
			String resultText = result.getBestFinalResultNoFiller();
			if (resultText == "No") {
				i -= 2;
				// Say Then
				continue;
			}
			System.out.println("You said: " + resultText + '\n');
			user_text[i] = resultText;
		}
		// ending the conversation
		System.out.println(pc_text[pc_text.length - 1]);
	}

	public static void testing() {
		pc_text = new String[7];
		pc_text[0] = "Welcome to your pizza ordering service!";
		pc_text[1] = "What size would you like?";
		pc_text[2] = "So you want a big pizza?";
		pc_text[3] = "What toppings would you like?";
		pc_text[4] = "So you want cheese?";
		pc_text[5] = "Would you like stuffed crust?";
		pc_text[6] = "Fine. Your total is 10 pounds. Your order will be ready shortly.";
	}

	public static void parseFile() {
		try {
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList prompts = doc.getElementsByTagName("prompt");
			for (int i = 0; i < prompts.getLength(); i++) {
				System.out.println(prompts.item(i).getTextContent());
				System.out.println("_________________");
			}
			NodeList blocks = doc.getElementsByTagName("block");
			for (int i = 0; i < blocks.getLength(); i++) {
				System.out.println(blocks.item(i).getTextContent());
				System.out.println("_________________");
			}
			NodeList user_decisions = doc.getElementsByTagName("one-of");
			for (int i = 0; i < user_decisions.getLength(); i++) {
				System.out.println(user_decisions.item(i).getTextContent());
				System.out.println("_________________");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String[] readFile() throws IOException {
		FileReader fr = new FileReader(path);
		BufferedReader br = new BufferedReader(fr);
		ArrayList<String> arr = new ArrayList<String>();
		while (true) {
			String text = br.readLine();
			if (text == null)
				break;
			arr.add(text);
		}
		br.close();
		return arr.toArray(new String[arr.size()]);
	}
}

// System.out.println("Root element :"
// + doc.getDocumentElement().getNodeName());