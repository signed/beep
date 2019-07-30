package com.github.signed.beep;


import java.util.Collection;

import com.github.signed.external.TestTag;

/**
 * A tag expression can be evaluated against a collection of {@link TestTag test tags} to decide if they match the expression.
 *
 * @since 1.1
 */
public interface TagExpression {

	/**
	 * Factory method to parse a {@link TagExpression TagExpression}
	 * from a <em>tag expression string</em>.
	 *
	 * @since 1.1
	 */
	static ParseResult parseFrom(String infixTagExpression) {
		return new Parser().parse(infixTagExpression);
	}

	boolean evaluate(Collection<TestTag> tags);
}
