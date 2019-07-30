package com.github.signed.beep;



/**
 * Factory method to parse an {@link Expression Expression}
 * from a <em>tag expression string</em>.
 *
 * @since 1.1
 */
public class TagExpression {

	///CLOVER:OFF
	private TagExpression() {
		/* no-op */
	}
	///CLOVER:ON

	public static ParseResult parseFrom(String infixTagExpression) {
		return new Parser().parse(infixTagExpression);
	}
}
