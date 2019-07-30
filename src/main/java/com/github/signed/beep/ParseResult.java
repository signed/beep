package com.github.signed.beep;


import java.util.Optional;
import java.util.function.Function;


/**
 * Either contains a successfully parsed {@link Expression} or an <em>error message</em> describing the parse error.
 *
 * @since 1.1
 */
public interface ParseResult {

	default Expression expressionOrThrow(Function<String, RuntimeException> error) {
		if (errorMessage().isPresent()) {
			throw error.apply(errorMessage().get());
		}
		return expression().get();
	}

	default Optional<String> errorMessage() {
		return Optional.empty();
	}

	default Optional<Expression> expression() {
		return Optional.empty();
	}

}
