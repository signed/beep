package com.github.signed.beep;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class TokenTests {

	@Test
	void startIndexOfTokenString() {
		assertThat(new Token(0, "!").trimmedTokenStartIndex()).isEqualTo(0);
		assertThat(new Token(0, "  !").trimmedTokenStartIndex()).isEqualTo(2);
		assertThat(new Token(7, "!").trimmedTokenStartIndex()).isEqualTo(7);
	}

	@Test
	void endIndex() {
		assertThat(new Token(0, "!").endIndex()).isEqualTo(1);
		assertThat(new Token(0, "  !").endIndex()).isEqualTo(3);
		assertThat(new Token(7, "!").endIndex()).isEqualTo(8);
	}

	@Test
	void concatenateTwoTokens() {
		List<Token> tokens = new Tokenizer().tokenize(" ! foo");
		Token one = tokens.get(0);
		Token two = tokens.get(1);
		Token joined = one.concatenate(two);
		assertThat(joined.rawString).isEqualTo(" ! foo");
		assertThat(joined.startIndex).isEqualTo(0);
	}

}
