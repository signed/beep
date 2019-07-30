package com.github.signed.beep;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    @ParameterizedTest
    @MethodSource("data")
    void acceptanceTests(String tagExpression) {
        assertThat(expressionParsedFrom(tagExpression)).isEmpty();
    }

    private static Stream<Arguments> data() {
        // @formatter:off
        return Stream.of(
                Arguments.of("foo bar |"),
                Arguments.of("foo bar &"),
                Arguments.of("foo & (bar !)"),
                Arguments.of("foo & (bar baz) |"),
                Arguments.of("foo & (bar baz) &"),
                Arguments.of("foo & |"),
                Arguments.of("| |"),
                Arguments.of("foo bar"),
                Arguments.of("( foo & bar ) )"),
                Arguments.of("( ( foo & bar )"),
                Arguments.of("foo !& bar"),
                Arguments.of("foo !| bar")
        );
        // @formatter:on
    }

    private Optional<Expression> expressionParsedFrom(String tagExpression) {
		return parser.parse(tagExpression);
	}
}
