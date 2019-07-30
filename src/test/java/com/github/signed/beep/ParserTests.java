package com.github.signed.beep;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ParserTests {

	private final Parser parser = new Parser();

	@Test
	void notHasHigherPrecedenceThanAnd() {
		assertThat(expressionParsedFrom("not foo and bar")).hasToString("(not(foo) and bar)");
	}

	@Test
	void andHasHigherPrecedenceThanOr() {
		assertThat(expressionParsedFrom("foo or bar and baz")).hasToString("(foo or (bar and baz))");
	}

	@Test
	void notIsRightAssociative() {
		assertThat(expressionParsedFrom("not not foo")).hasToString("not(not(foo))");
	}

	@Test
	void andIsLeftAssociative() {
		assertThat(expressionParsedFrom("foo and bar and baz")).hasToString("((foo and bar) and baz)");
	}

	@Test
	void orIsLeftAssociative() {
		assertThat(expressionParsedFrom("foo or bar or baz")).hasToString("((foo or bar) or baz)");
	}

	@ParameterizedTest
	@MethodSource("data")
	void acceptanceTests(String tagExpression, String expression) {
		assertThat(expressionParsedFrom(tagExpression)).hasToString(expression);
	}

	private static Stream<Arguments> data() {
		// @formatter:off
		return Stream.of(
				Arguments.of("foo", "foo"),
				Arguments.of("not foo", "not(foo)"),
				Arguments.of("foo and bar", "(foo and bar)"),
				Arguments.of("foo or bar", "(foo or bar)"),
				Arguments.of("( not foo and bar or baz)", "((not(foo) and bar) or baz)")
		);
		// @formatter:on
	}

	private Expression expressionParsedFrom(String tagExpression) {
		return parser.parse(tagExpression).orElseThrow(
			() -> new RuntimeException("[" + tagExpression + "] should be parsable"));
	}
}
