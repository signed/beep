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
		assertThat(parseErrorFromParsing(null)).isNotEmpty();
	}

	@Test
	void emptyExpression() {
		assertThat(parseErrorFromParsing("")).isNotEmpty();
	}

	@Test
	void missingClosingParenthesis() {
		assertThat(parseErrorFromParsing("(")).contains("missing closing parenthesis");
		assertThat(parseErrorFromParsing("( foo & bar")).contains("missing closing parenthesis");
	}

	@Test
	void missingOpeningParenthesis() {
		assertThat(parseErrorFromParsing(")")).contains("missing opening parenthesis");
		assertThat(parseErrorFromParsing(" foo | bar)")).contains("missing opening parenthesis");
	}

	@Test
	void partialUnaryOperator() {
		assertThat(parseErrorFromParsing("!")).isNotEmpty();
	}

	@Test
	void partialBinaryOperator() {
		assertThat(parseErrorFromParsing("& foo")).isNotEmpty();
		assertThat(parseErrorFromParsing("foo |")).isNotEmpty();
	}

    @ParameterizedTest
    @MethodSource("data")
    void acceptanceTests(String tagExpression, String parseError) {
        assertThat(parseErrorFromParsing(tagExpression)).contains(parseError);
    }

    private static Stream<Arguments> data() {
        // @formatter:off
        return Stream.of(
                Arguments.of("foo bar |", "hpp"),
                Arguments.of("foo bar &", "hpp"),
                Arguments.of("foo & (bar !)", "hmm"),
                Arguments.of("foo & (bar baz) |", "hoo"),
                Arguments.of("foo & (bar baz) &", "hoo"),
                Arguments.of("foo & |", "hoo"),
                Arguments.of("| |", "hoo"),
                Arguments.of("foo bar", "hqq"),
                Arguments.of("( foo & bar ) )", "missing opening parenthesis"),
                Arguments.of("( ( foo & bar )", "missing closing parenthesis"),
                Arguments.of("foo !& bar", "hoo"),
                Arguments.of("foo !| bar", "hoo")
        );
        // @formatter:on
    }

    private Optional<String> parseErrorFromParsing(String tagExpression) {
		return parser.parse(tagExpression).parseError();
	}
}
