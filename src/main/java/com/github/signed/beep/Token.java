package com.github.signed.beep;

class Token {

	final int startIndex;
	final int position;
	final String rawString;

	Token(int startIndex, int position, String rawString) {
		this.startIndex = startIndex;
		this.position = position;
		this.rawString = rawString;
	}

	String string() {
		return rawString.trim();
	}

	public int trimmedTokenStartIndex() {
		return startIndex + rawString.indexOf(string());
	}
}
