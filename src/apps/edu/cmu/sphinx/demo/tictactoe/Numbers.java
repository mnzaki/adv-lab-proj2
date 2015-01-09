package edu.cmu.sphinx.demo.tictactoe;

public enum Numbers {
	ONE(1),
	TWO(2),
	THREE(3),
	FOUR(4),
	FIVE(5),
	SIX(6),
	SEVEN(7),
	EIGHT(8),
	NINE(9);
	
	int value;
	
	private Numbers(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}
