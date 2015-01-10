package edu.cmu.sphinx.demo.tictactoe;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JTextField;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUIGame extends JFrame {

	private JPanel contentPane;
	private JTable table;
	JButton speech_button;
	private JTextField data;
	TicTacToeGame game;
	JButton text_button;

	/**
	 * Launch the application.
	 */
	// public static void main(String[] args) {
	// EventQueue.invokeLater(new Runnable() {
	// public void run() {
	// try {
	// GUIGame frame = new GUIGame(0);
	// frame.setVisible(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }

	/**
	 * Create the frame.
	 */
	public GUIGame(int size) {
		String[] grammar_table = new String[10];
		grammar_table[0] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (THREE | FOUR | FIVE | SIX | SEVEN | EIGHT | NINE);\n public <command> = <numbers>;";
		grammar_table[3] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE);\n public <command> = <numbers> <numbers>;";
		grammar_table[4] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR);\n public <command> = <numbers> <numbers>;";
		grammar_table[5] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR | FIVE);\n public <command> = <numbers> <numbers>;";
		grammar_table[6] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR | FIVE | SIX);\n public <command> = <numbers> <numbers>;";
		grammar_table[7] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR | FIVE | SIX | SEVEN);\n public <command> = <numbers> <numbers>;";
		grammar_table[8] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR | FIVE | SIX | SEVEN | EIGHT);\n public <command> = <numbers> <numbers>;";
		grammar_table[9] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR | FIVE | SIX | SEVEN | EIGHT | NINE);\n public <command> = <numbers> <numbers>;";

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		game = new TicTacToeGame(size);
		try {
			File myFoo = new File(
					"src/apps/edu/cmu/sphinx/demo/tictactoe/tictactoe.gram");
			FileOutputStream fooStream = new FileOutputStream(myFoo, false);
			byte[] myBytes = grammar_table[size].getBytes();
			fooStream.write(myBytes);
			fooStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		table = new JTable();
		table.setBounds(12, 78, 426, 182);
		table.setModel(new DefaultTableModel(size, size));
		contentPane.add(table);
		text_button = new JButton("Enter Text");
		text_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] numbers = data.getText().split(" ");
				set(numbers);
				data.setText(null);
			}
		});
		text_button.setBounds(268, 12, 155, 25);
		contentPane.add(text_button);

		speech_button = new JButton("Enter Speech");
		speech_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigurationManager cm = new ConfigurationManager(
						TicTacToe.class.getResource("tictactoe.config.xml"));
				Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
				Microphone microphone = (Microphone) cm.lookup("microphone");
				if (!microphone.startRecording()) {
					System.out.println("Cannot start microphone.");
					recognizer.deallocate();
					System.exit(1);
				}
				Result result = recognizer.recognize();
				String resultText = result.getBestFinalResultNoFiller();
				String[] numbers = resultText.split(" ");
				set(numbers);
			}
		});
		speech_button.setBounds(268, 36, 155, 25);
		contentPane.add(speech_button);

		data = new JTextField();
		data.setBounds(43, 12, 114, 19);
		contentPane.add(data);
		data.setColumns(10);
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				table.getModel().setValueAt("-", i, j);
			}
		}
	}

	public void set(String[] numbers) {
		int i = 0;
		int j = 0;
		try {
			i = Numbers.valueOf(numbers[0]).getValue() - 1;
			j = Numbers.valueOf(numbers[1]).getValue() - 1;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Please enter coordinates in the format Number Number");
			return;
		}
		game.setCell(i, j);
		String[][] board = game.getBoard();
		for (int k = 0; k < board.length; k++) {
			for (int k2 = 0; k2 < board[0].length; k2++) {
				String value = board[k][k2];
				value = value == null ? "-" : value;
				table.getModel().setValueAt(value, k, k2);
			}
		}
		if (game.isGameOver()) {
			JOptionPane.showMessageDialog(null, "Game Over");
			text_button.setEnabled(false);
			speech_button.setEnabled(false);
		}

	}
}
