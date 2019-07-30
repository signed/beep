package com.github.signed.beep;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class ParserErrorTests {

	private final Parser parser = new Parser();

	@Test
	void cantParseExpressionFromNull() {
		assertThat(expressionParsedFrom(null)).isEmpty();
	}

	@Test
	void emptyExpression() {
		assertThat(expressionParsedFrom("")).isEmpty();
	}

	@Test
	void missingClosingParenthesis() {
		assertThat(expressionParsedFrom("(")).isEmpty();
		assertThat(expressionParsedFrom("( foo and bar")).isEmpty();
	}

	@Test
	void missingOpeningParenthesis() {
		assertThat(expressionParsedFrom(")")).isEmpty();
		assertThat(expressionParsedFrom(" foo or bar)")).isEmpty();
	}

	@Test
	void partialUnaryOperator() {
		assertThat(expressionParsedFrom("not")).isEmpty();
	}

	@Test
	void partialBinaryOperator() {
		assertThat(expressionParsedFrom("and foo")).isEmpty();
		assertThat(expressionParsedFrom("foo or")).isEmpty();
	}

	private Optional<Expression> expressionParsedFrom(String tagExpression) {
		return parser.parse(tagExpression);
	}
}
