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
		assertThat(parseErrorFromParsing(null)).contains("empty tag expression");
	}

	@Test
	void emptyExpression() {
		assertThat(parseErrorFromParsing("")).contains("empty tag expression");
	}

	@Test
	void missingClosingParenthesis() {
		assertThat(parseErrorFromParsing("(")).contains("( at <0> missing closing parenthesis");
		assertThat(parseErrorFromParsing("( foo & bar")).contains("( at <0> missing closing parenthesis");
	}

	@Test
	void missingOpeningParenthesis() {
		assertThat(parseErrorFromParsing(")")).contains(") at <0> missing opening parenthesis");
		assertThat(parseErrorFromParsing(" foo | bar)")).contains(") at <3> missing opening parenthesis");
	}

	@Test
	void partialUnaryOperator() {
		assertThat(parseErrorFromParsing("!")).contains("! at <0> missing operand");
	}

	@Test
	void partialBinaryOperator() {
		assertThat(parseErrorFromParsing("& foo")).contains("& at <0> missing operand");
		assertThat(parseErrorFromParsing("foo |")).contains("| at <1> missing operand");
	}

    @ParameterizedTest
    @MethodSource("data")
    void acceptanceTests(String tagExpression, String parseError) {
        assertThat(parseErrorFromParsing(tagExpression)).contains(parseError);
    }

    private static Stream<Arguments> data() {
        // @formatter:off
        return Stream.of(
                Arguments.of("foo bar", "missing operator"),
                Arguments.of("foo bar |", "| at <2> missing rhs operand"),
                Arguments.of("foo bar &", "& at <2> missing rhs operand"),
                Arguments.of("foo & (bar !)", "! at <4> missing rhs operand"),
                Arguments.of("( foo & bar ) )", ") at <5> missing opening parenthesis"),
                Arguments.of("( ( foo & bar )", "( at <0> missing closing parenthesis"),
                Arguments.of("foo & (bar baz) |", "missing operator between bar <3> and baz <4>"),
                Arguments.of("foo & (bar baz) &", "missing operator between bar <3> and baz <4>"),
                Arguments.of("foo & |", "& at <1> missing operand"),
                Arguments.of("| |", "| at <0> missing operand"),
                Arguments.of("foo !& bar", "! at <1> missing rhs operand"),
                Arguments.of("foo !| bar", "! at <1> missing rhs operand")
        );
        // @formatter:on
    }

    private Optional<String> parseErrorFromParsing(String tagExpression) {
		return parser.parse(tagExpression).parseError();
	}
}
