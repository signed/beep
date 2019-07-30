package com.github.signed.beep;

import java.util.List;

class Parser {

	private final Tokenizer tokenizer = new Tokenizer();

	ParseResult parse(String infixTagExpression) {
		return constructExpressionFrom(tokensDerivedFrom(infixTagExpression));
	}

	private List<String> tokensDerivedFrom(String infixTagExpression) {
		return tokenizer.tokenize(infixTagExpression);
	}

	private ParseResult constructExpressionFrom(List<String> tokens) {
		return new ShuntingYard(tokens).execute();
	}

}
