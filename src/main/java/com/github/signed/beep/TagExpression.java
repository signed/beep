package com.github.signed.beep;


import java.util.Collection;

import com.github.signed.external.TestTag;

/**
 * A tag expression can be evaluated against a collection of
 * {@linkplain TestTag tags} to determine if they match the expression.
 */
public interface TagExpression {

	/**
	 * Attempt to parse a {@link TagExpression} from the supplied <em>tag
	 * expression string</em>.
	 *
	 * @param infixTagExpression the tag expression string to parse; never {@code null}.
	 * @see ParseResult
	 */
	static ParseResult parseFrom(String infixTagExpression) {
		return new Parser().parse(infixTagExpression);
	}

	/**
	 * Evaluate this tag expression against the supplied collection of
	 * {@linkplain TestTag tags}.
	 *
	 * @param tags the tags this tag expression is to be evaluated against
	 * @return {@code true}, if the tags match this tag expression; {@code false}, otherwise
	 */
	boolean evaluate(Collection<TestTag> tags);

}
