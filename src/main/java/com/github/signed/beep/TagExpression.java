package com.github.signed.beep;

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
