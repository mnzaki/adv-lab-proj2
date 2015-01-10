package edu.cmu.sphinx.demo.tictactoe;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;

import edu.cmu.sphinx.demo.tictactoe.Numbers;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class TicTacToe {

	public static void main(String[] args) {
		String[] table = new String[10];
		table[0] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (THREE | FOUR | FIVE | SIX | SEVEN | EIGHT | NINE);\n public <command> = <numbers>;";
		table[3] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE);\n public <command> = <numbers> <numbers>;";
		table[4] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR);\n public <command> = <numbers> <numbers>;";
		table[5] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR | FIVE);\n public <command> = <numbers> <numbers>;";
		table[6] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR | FIVE | SIX);\n public <command> = <numbers> <numbers>;";
		table[7] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR | FIVE | SIX | SEVEN);\n public <command> = <numbers> <numbers>;";
		table[8] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR | FIVE | SIX | SEVEN | EIGHT);\n public <command> = <numbers> <numbers>;";
		table[9] = "#JSGF V1.0;\n grammar TicTacToe;\n <numbers> = (ONE | TWO | THREE | FOUR | FIVE | SIX | SEVEN | EIGHT | NINE);\n public <command> = <numbers> <numbers>;";
		try {
			File myFoo = new File(
					"src/apps/edu/cmu/sphinx/demo/tictactoe/tictactoe.gram");
			FileOutputStream fooStream = new FileOutputStream(myFoo, false);
			byte[] myBytes = table[0].getBytes();
			fooStream.write(myBytes);
			fooStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Scanner sc = new Scanner(System.in);
		ConfigurationManager cm;
		cm = new ConfigurationManager(
				TicTacToe.class.getResource("tictactoe.config.xml"));

		System.out
				.println("enter 0 to use keyboard to enter data or enter 1 to use microphone for entering board size");
		int size = 0;
		if (sc.nextInt() == 0) {
			System.out
					.println("Enter the required size and it should be between 3 & 9");
			size = sc.nextInt();
			sc.nextLine();
		} else {
			Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
			Microphone microphone = (Microphone) cm.lookup("microphone");
			if (!microphone.startRecording()) {
				System.out.println("Cannot start microphone.");
				recognizer.deallocate();
				System.exit(1);
			}
			System.out
					.println("Say the required size and it should be between 3 & 9");
			Result result = recognizer.recognize();
			String resultText = result.getBestFinalResultNoFiller();
			size = Numbers.valueOf(resultText).getValue();
		}
		TicTacToeGame game = new TicTacToeGame(size);
		try {
			File myFoo = new File(
					"src/apps/edu/cmu/sphinx/demo/tictactoe/tictactoe.gram");
			FileOutputStream fooStream = new FileOutputStream(myFoo, false);
			byte[] myBytes = table[size].getBytes();
			fooStream.write(myBytes);
			fooStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			System.out
					.println("enter 0 to use keyboard to enter data or enter 1 to use microphone for entering board size");
			String[] numbers;
			if (sc.nextInt() == 0) {
				sc.nextLine();
				System.out.println("write the cell in form of Number Number");
				String cell = sc.nextLine();
				System.out.println(cell);
				numbers = cell.split(" ");
			} else {
				Recognizer recognizer = (Recognizer) cm.lookup("recognizer");
				Microphone microphone = (Microphone) cm.lookup("microphone");
				if (!microphone.startRecording()) {
					System.out.println("Cannot start microphone.");
					recognizer.deallocate();
					System.exit(1);
				}
				System.out.println("say the cell number in form of i & j");
				Result result = recognizer.recognize();
				String resultText = result.getBestFinalResultNoFiller();
				numbers = resultText.split(" ");
			}
			int i = Numbers.valueOf(numbers[0]).getValue() - 1;
			int j = Numbers.valueOf(numbers[1]).getValue() - 1;
			game.setCell(i, j);
			game.displayBoard();
			if (game.isGameOver()) {
				System.out.println("game is done :/");
				break;
			}
		}
	}
}
