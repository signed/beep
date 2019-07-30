package com.github.signed.beep;

class Token {

	static Token singlePosition(int startIndex, int position, String rawString) {
		return new Token(startIndex, position, position, rawString);
	}

	private final int rightMostPosition;
	private final int leftMostPosition;

	final int startIndex;
	final String rawString;

	private Token(int startIndex, int leftMostPosition, int rightMostPosition, String rawString) {
		this.startIndex = startIndex;
		this.leftMostPosition = leftMostPosition;
		this.rightMostPosition = rightMostPosition;
		this.rawString = rawString;
	}

	String string() {
		return rawString.trim();
	}

	public int trimmedTokenStartIndex() {
		return startIndex + rawString.indexOf(string());
	}

	public int endIndex() {
		return startIndex + rawString.length();
	}

	public int leftMostPosition() {
		return leftMostPosition;
	}

	public int rightMostPosition() {
		return rightMostPosition;
	}

	public Token concatenate(Token rightOfThis) {
		String concatenatedRawString = this.rawString + rightOfThis.rawString;
		return new Token(startIndex, leftMostPosition, rightOfThis.rightMostPosition, concatenatedRawString);
	}

}
