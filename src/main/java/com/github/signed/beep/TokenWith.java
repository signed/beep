package com.github.signed.beep;

class TokenWith<T> {

	final Token token;
	final T element;

	TokenWith(Token token, T element) {
		this.token = token;
		this.element = element;
	}

}
