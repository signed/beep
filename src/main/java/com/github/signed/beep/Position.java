package com.github.signed.beep;

class Position<T> {
	final Token token;
	final T element;

	Position(Token token, T element) {
		this.token = token;
		this.element = element;
	}
}
