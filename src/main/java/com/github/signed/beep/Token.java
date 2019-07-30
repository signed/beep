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

	public boolean isLeftOf(Token token) {
		return lastCharacterIndex() < token.startIndex;
	}

	public int lastCharacterIndex() {
		return endIndexExclusive() - 1;
	}

	public int endIndexExclusive() {
		return startIndex + rawString.length();
	}

	public Token concatenate(Token rightOfThis) {
		String concatenatedRawString = this.rawString + rightOfThis.rawString;
		return new Token(startIndex, concatenatedRawString);
	}

}
