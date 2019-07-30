package com.github.signed.beep;


import java.util.Optional;
import java.util.function.Function;


/**
 * Either contains a successfully parsed {@link TagExpression} or an <em>error message</em> describing the parse error.
 *
 * @since 1.1
 */
public interface ParseResult {

	default TagExpression tagExpressionOrThrow(Function<String, RuntimeException> error) {
		if (errorMessage().isPresent()) {
			throw error.apply(errorMessage().get());
		}
		return tagExpression().get();
	}

	default Optional<String> errorMessage() {
		return Optional.empty();
	}

	default Optional<TagExpression> tagExpression() {
		return Optional.empty();
	}

}
