package com.github.signed.beep;

import java.util.Optional;

class ParseResults {
	static ParseResult success(TagExpression tagExpression) {
		return new ParseResult() {
			@Override
			public Optional<TagExpression> tagExpression() {
				return Optional.of(tagExpression);
			}
		};
	}

	static ParseResult error(String errorMessage) {
		return new ParseResult() {
			@Override
			public Optional<String> errorMessage() {
				return Optional.of(errorMessage);
			}
		};
	}
}
