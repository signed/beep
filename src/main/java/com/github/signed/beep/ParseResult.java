package com.github.signed.beep;


import java.util.Optional;
import java.util.function.Function;


/**
 * The result of attempting to parse a {@link TagExpression}.
 *
 * <p>An instance of this type either contains a successfully parsed
 * {@link TagExpression} or an <em>error message</em> describing the parse
 * error.
 *
 * @see TagExpression#parseFrom(String)
 */
public interface ParseResult {

	/**
	 * Return the parsed {@link TagExpression} or throw an exception with the
	 * contained parse error.
	 *
	 * @param exceptionCreator will be called with the error message in case
	 * this parse result contains a parse error; never {@code null}.
	 */
	default TagExpression tagExpressionOrThrow(Function<String, RuntimeException> exceptionCreator) {
		if (errorMessage().isPresent()) {
			throw exceptionCreator.apply(errorMessage().get());
		}
		return tagExpression().get();
	}

	/**
	 * Return the contained parse error message, if any.
	 */
	default Optional<String> errorMessage() {
		return Optional.empty();
	}

	/**
	 * Return the contained {@link TagExpression}, if any.
	 */
	default Optional<TagExpression> tagExpression() {
		return Optional.empty();
	}

}
