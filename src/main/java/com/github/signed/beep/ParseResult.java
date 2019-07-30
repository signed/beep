package com.github.signed.beep;

import java.util.function.Function;

public class ParseResult {

	static ParseResult success(Expression expression) {
		return new ParseResult(expression, null);
	}

	static ParseResult error(String errorMessage) {
		return new ParseResult(null, errorMessage);
	}

	private final String errorMessage;
	private final Expression expression;

	private ParseResult(Expression expression, String errorMessage) {
		this.errorMessage = errorMessage;
		this.expression = expression;
	}

	public Expression expressionOrThrow(Function<String, RuntimeException> error) {
		if (null != errorMessage) {
			throw error.apply(errorMessage);
		}
		return expression;
	}
}
