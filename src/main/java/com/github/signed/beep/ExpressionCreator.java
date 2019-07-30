package com.github.signed.beep;

import java.util.Optional;

interface ExpressionCreator {
	Optional<ParseError> success = Optional.empty();

	static Optional<ParseError> report(ParseError error) {
		return Optional.of(error);
	}

	Optional<ParseError> createExpressionAndAddTo(Stack<Position<Expression>> expressions, int position);
}
