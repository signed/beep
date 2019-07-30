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
		assertThat(expressionParsedFrom("( foo & bar")).isEmpty();
	}

	@Test
	void missingOpeningParenthesis() {
		assertThat(expressionParsedFrom(")")).isEmpty();
		assertThat(expressionParsedFrom(" foo | bar)")).isEmpty();
	}

	@Test
	void partialUnaryOperator() {
		assertThat(expressionParsedFrom("!")).isEmpty();
	}

	@Test
	void partialBinaryOperator() {
		assertThat(expressionParsedFrom("& foo")).isEmpty();
		assertThat(expressionParsedFrom("foo |")).isEmpty();
	}

	private Optional<Expression> expressionParsedFrom(String tagExpression) {
		return parser.parse(tagExpression);
	}
}
