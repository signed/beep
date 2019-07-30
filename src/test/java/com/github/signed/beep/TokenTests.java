package com.github.signed.beep;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class TokenTests {

	@Test
	void startIndexOfTokenString() {
		assertThat(Token.singlePosition(0, 0, "!").trimmedTokenStartIndex()).isEqualTo(0);
		assertThat(Token.singlePosition(0, 0, "  !").trimmedTokenStartIndex()).isEqualTo(2);
		assertThat(Token.singlePosition(7, 0, "!").trimmedTokenStartIndex()).isEqualTo(7);
	}

	@Test
	void endIndex() {
		assertThat(Token.singlePosition(0, 0, "!").endIndex()).isEqualTo(1);
		assertThat(Token.singlePosition(0, 0, "  !").endIndex()).isEqualTo(3);
		assertThat(Token.singlePosition(7, 0, "!").endIndex()).isEqualTo(8);
	}

	@Test
	void concatenateTwoTokens() {
		List<Token> tokens = new Tokenizer().tokenize(" ! foo");
		Token one = tokens.get(0);
		Token two = tokens.get(1);
		Token joined = one.concatenate(two);
		assertThat(joined.rawString).isEqualTo(" ! foo");
		assertThat(joined.startIndex).isEqualTo(0);
		assertThat(joined.leftMostPosition()).isEqualTo(0);
		assertThat(joined.rightMostPosition()).isEqualTo(1);
	}

}
