package com.github.signed.beep;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

class TokenTests {

	@Test
	void startIndexOfTokenString() {
		assertThat(new Token(0, 0, "!").trimmedTokenStartIndex()).isEqualTo(0);
		assertThat(new Token(0, 0, "  !").trimmedTokenStartIndex()).isEqualTo(2);
		assertThat(new Token(7, 0, "!").trimmedTokenStartIndex()).isEqualTo(7);
	}
}
