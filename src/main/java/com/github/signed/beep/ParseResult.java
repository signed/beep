package com.github.signed.beep;

import java.util.Optional;
import java.util.function.Function;

public class ParseResult {

	public static ParseResult error(ParseStatus parseStatus) {
		return new ParseResult(parseStatus, null);
	}

	public static ParseResult success(Expression expression) {
		return new ParseResult(null, expression);
	}

	private final ParseStatus parseStatus;
	private final Expression expression;

	private ParseResult(ParseStatus parseStatus, Expression expression) {
		this.parseStatus = parseStatus;
		this.expression = expression;
	}

	public Optional<String> parseError() {
		return Optional.ofNullable(parseStatus).map(e -> e.message);
	}

	public Expression expressionOrThrow(Function<ParseStatus, RuntimeException> error) {
		if (null != parseStatus) {
			throw error.apply(parseStatus);
		}
		return expression;
	}
}
