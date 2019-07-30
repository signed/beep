package com.github.signed.beep;

import java.util.Optional;

class ParseResults {
	static ParseResult success(Expression expression) {
		return new ParseResult() {
			@Override
			public Optional<Expression> expression() {
				return Optional.of(expression);
			}
		};
	}

	static ParseResult error(String errorMessage) {
		return new ParseResult() {
			@Override
			public Optional<String> errorMessage() {
				return Optional.of(errorMessage);
			}
		};
	}
}
