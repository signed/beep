package com.github.signed.beep;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class TokenizerTests {

	@Test
	void nullContainsNoTokens() {
		assertThat(tokensExtractedFrom(null)).isEmpty();
	}

	@Test
	void removeLeadingAndTrailingSpaces() {
		assertThat(tokensExtractedFrom(" tag ")).containsExactly("tag");
	}

	@Test
	void notIsAReservedKeyword() {
		assertThat(tokensExtractedFrom("not tag")).containsExactly("not", "tag");
		assertThat(tokensExtractedFrom("nottag")).containsExactly("nottag");
	}

	@Test
	void andIsAReservedKeyword() {
		assertThat(tokensExtractedFrom("one and two")).containsExactly("one", "and", "two");
		assertThat(tokensExtractedFrom("andtag")).containsExactly("andtag");
	}

	@Test
	void orIsAReservedKeyword() {
		assertThat(tokensExtractedFrom("one or two")).containsExactly("one", "or", "two");
		assertThat(tokensExtractedFrom("ortag")).containsExactly("ortag");
	}

	@Test
	void discoverBrackets() {
		assertThat(tokensExtractedFrom("()")).containsExactly("(", ")");
		assertThat(tokensExtractedFrom("(tag)")).containsExactly("(", "tag", ")");
		assertThat(tokensExtractedFrom("( tag )")).containsExactly("(", "tag", ")");
		assertThat(tokensExtractedFrom("( foo and bar) or (baz and qux )")).containsExactly("(", "foo", "and", "bar",
			")", "or", "(", "baz", "and", "qux", ")");
	}

	private List<String> tokensExtractedFrom(String expression) {
		return new Tokenizer().tokenize(expression);
	}
}
