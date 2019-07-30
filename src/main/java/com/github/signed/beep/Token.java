package com.github.signed.beep;

class Token {

	final int startIndex;
	final String rawString;

	Token(int startIndex, String rawString) {
		this.startIndex = startIndex;
		this.rawString = rawString;
	}

	String string() {
		return rawString.trim();
	}

	public int trimmedTokenStartIndex() {
		return startIndex + rawString.indexOf(string());
	}

	public int endIndex() {
		return startIndex + rawString.length() -1;
	}

	public Token concatenate(Token rightOfThis) {
		String concatenatedRawString = this.rawString + rightOfThis.rawString;
		return new Token(startIndex, concatenatedRawString);
	}

	public boolean isLeftOf(Token token) {
		return endIndex() < token.startIndex;
	}

}
