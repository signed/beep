package com.github.signed.beep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ParserTests {

	private final Parser parser = new Parser();

	@Test
	void notHasHigherPrecedenceThanAnd() {
		assertThat(tagExpressionParsedFrom("! foo & bar")).hasToString("(!foo & bar)");
	}

	@Test
	void andHasHigherPrecedenceThanOr() {
		assertThat(tagExpressionParsedFrom("foo | bar & baz")).hasToString("(foo | (bar & baz))");
	}

	@Test
	void notIsRightAssociative() {
		assertThat(tagExpressionParsedFrom("foo &! bar")).hasToString("(foo & !bar)");
	}

	@Test
	void andIsLeftAssociative() {
		assertThat(tagExpressionParsedFrom("foo & bar & baz")).hasToString("((foo & bar) & baz)");
	}

	@Test
	void orIsLeftAssociative() {
		assertThat(tagExpressionParsedFrom("foo | bar | baz")).hasToString("((foo | bar) | baz)");
	}

	@ParameterizedTest
	@MethodSource("data")
	void acceptanceTests(String tagExpression, String expression) {
		assertThat(tagExpressionParsedFrom(tagExpression)).hasToString(expression);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> data() {
		// @formatter:off
		return Stream.of(
				arguments("foo", "foo"),
				arguments("! foo", "!foo"),
				arguments("foo & bar", "(foo & bar)"),
				arguments("foo | bar", "(foo | bar)"),
				arguments("( ! foo & bar | baz)", "((!foo & bar) | baz)"),
				arguments("(foo & bar ) | baz & quux", "((foo & bar) | (baz & quux))"),
				arguments("! foo | bar & ! baz | ! quux | quuz & corge", "(((!foo | (bar & !baz)) | !quux) | (quuz & corge))"),
				arguments("(foo & bar ) | baz & quux", "((foo & bar) | (baz & quux))"),
				arguments("foo | bar & baz|quux", "((foo | (bar & baz)) | quux)")
		);
		// @formatter:on
	}

	private TagExpression tagExpressionParsedFrom(String tagExpression) {
		return parser.parse(tagExpression).tagExpressionOrThrow(
			(error) -> new RuntimeException("[" + tagExpression + "] should be parsable"));
	}

}
