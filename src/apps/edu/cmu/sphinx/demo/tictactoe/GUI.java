package edu.cmu.sphinx.demo.tictactoe;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JLabel;

public class GUI extends JFrame {

	private JPanel contentPane;
	private JTextField size_text_field;
	static GUI frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUI() {
		writeGrammar(0);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnEnterText = new JButton("Enter Text");
		btnEnterText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String size_text = size_text_field.getText();
				int size = 0;
				try {
					size = Integer.parseInt(size_text);
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null,
							"Please enter a number like 3");
					return;
				}
				showGame(size);
			}
		});
		btnEnterText.setBounds(276, 30, 151, 25);
		contentPane.add(btnEnterText);

		JButton btnEnterSpeech = new JButton("Enter Speech");
		btnEnterSpeech.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigurationManager cm = new ConfigurationManager(
						TicTacToe.class.getResource("tictactoe.config.xml"));
				Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
				recognizer.allocate();
				Microphone microphone = (Microphone) cm.lookup("microphone");
				if (!microphone.startRecording()) {
					System.out.println("Cannot start microphone.");
					recognizer.deallocate();
					System.exit(1);
				}
				JSGFGrammar grammar = (JSGFGrammar) cm.lookup("jsgfGrammar");
				try {
					grammar.loadJSGF("tictactoe");
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("couldnt load");
				}
				Result result = recognizer.recognize();
				String resultText = result.getBestFinalResultNoFiller();
				recognizer.deallocate();
				System.out.println(resultText);
				try {
					int size = Numbers.valueOf(resultText.toUpperCase())
							.getValue();
					showGame(size);
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(null,
							"Could not recognize the number");
				}

			}
		});
		btnEnterSpeech.setBounds(276, 67, 151, 25);
		contentPane.add(btnEnterSpeech);

		size_text_field = new JTextField();
		size_text_field.setBounds(41, 35, 114, 19);
		contentPane.add(size_text_field);
		size_text_field.setColumns(10);

		JLabel lblPleaseEnterThe = new JLabel(
				"Please Enter the size of the board");
		lblPleaseEnterThe.setBounds(100, 0, 261, 15);
		contentPane.add(lblPleaseEnterThe);
	}

	public void showGame(int size) {
		if (size < 3 || size > 9) {
			JOptionPane.showMessageDialog(null,
					"Please enter a number >= 3 and <= 9");
			return;
		}
		System.out.println(size);
		writeGrammar(size);
		frame.setVisible(false);
		new GUIGame(size).setVisible(true);
		System.out.println("showing the game");
	}

	public void writeGrammar(int index) {
		try {
			File myFoo = new File(
					"bld/edu/cmu/sphinx/demo/tictactoe/tictactoe.gram");
			FileOutputStream fooStream = new FileOutputStream(myFoo, false);
			String grammar = generateGrammar(index);
			byte[] myBytes = grammar.getBytes();
			fooStream.write(myBytes);
			fooStream.close();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public String generateGrammar(int maxValue) {
		String grammar = "#JSGF V1.0;\ngrammar TicTacToe;\n";
		int start = 1;
		int end = maxValue;
		String command = "public <command> = (<numbers> <numbers>);";
		if (maxValue == 0) {
			start = 3;
			end = 9;
			command = "public <command> = <numbers>;";
		}
		String rule = "<numbers> = (";
		for (int i = start; i <= end; i++) {
			rule = rule + Numbers.values()[i - 1] + " |";
		}
		rule = rule.substring(0, rule.length() - 2) + ");\n";
		grammar += rule;
		grammar += command;
		return grammar;
	}
}