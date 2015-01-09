package edu.cmu.sphinx.demo.tictactoe;

public class TicTacToeGame {
	private String board[][];
	private int size;
	private int current_player;

	public TicTacToeGame(int size) {
		this.size = size;
		board = new String[this.size][this.size];
		current_player = 0;
	}

	public void setCell(int i, int j) {
		if (board[i][j] != null) {
			return;
		}
		board[i][j] = current_player % 2 == 0 ? "X" : "O";
		current_player++;
	}

	public void displayBoard() {
		for (int i = 0; i < this.size; i++) {
			for (int j = 0; j < this.size; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println();
		}
	}

	public boolean isGameOver() {
		// checking horizontal winning condition
		for (int i = 0; i < size; i++) {
			boolean horizontal_value = true;
			for (int j = 1; j < size; j++) {
				if (board[i][j] == null || board[i][j - 1] == null
						|| board[i][j].compareTo(board[i][j - 1]) != 0) {
					horizontal_value = false;
					break;
				}
			}
			if (horizontal_value)
				return true;
		}
		// checking vertical winning condition
		for (int i = 0; i < size; i++) {
			boolean vertical_value = true;
			for (int j = 1; j < size; j++) {
				if (board[j][i] == null || board[j - 1][i] == null
						|| board[j][i].compareTo(board[j - 1][i]) != 0) {
					vertical_value = false;
					break;
				}
			}
			if (vertical_value)
				return true;
		}
		// checking main diagonal winning condition
		boolean diagonal_value = true;
		for (int i = 1, j = 1; i < size; i++, j++) {
			if (board[i][j] == null || board[i - 1][j - 1] == null
					|| board[i][j].compareTo(board[i - 1][j - 1]) != 0) {
				diagonal_value = false;
				break;
			}
		}
		if (diagonal_value)
			return true;
		diagonal_value = true;
		for (int i = 1, j = size - 2; i < size; i++, j--) {
			if (board[i][j] == null || board[i - 1][j + 1] == null
					|| board[i][j].compareTo(board[i - 1][j + 1]) != 0) {
				diagonal_value = false;
				break;
			}
		}
		if (diagonal_value)
			return true;
		return false;
	}
}
