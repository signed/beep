package com.github.signed.beep;

class Position<T> {
	final int position;
	final Token token;
	final T element;

	Position(Token token, T element) {
		this.position = token.position;
		this.token = token;
		this.element = element;
	}
}
