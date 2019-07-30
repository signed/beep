package com.github.signed.beep;

import java.util.function.Supplier;

class ParseStatus {

	static ParseStatus success() {
		return error(null);
	}

	static ParseStatus problemParsing(Token token, String representation) {
		return errorAt(token, representation, "problem parsing");
	}

	static ParseStatus missingOpeningParenthesis(Token token, String representation) {
		return errorAt(token, representation, "missing opening parenthesis");
	}

	static ParseStatus missingClosingParenthesis(Token token, String representation) {
		return errorAt(token, representation, "missing closing parenthesis");
	}

	static ParseStatus missingRhsOperand(Token token, String representation) {
		return errorAt(token, representation, "missing rhs operand");
	}

	static ParseStatus errorAt(Token token, String operatorRepresentation, String message) {
		return error(operatorRepresentation + " at " + format(token.trimmedTokenStartIndex()) + " " + message);
	}

	static ParseStatus missingOperatorBetween(TokenWith<Expression> lhs, TokenWith<Expression> rhs) {
		String lhsString = lhs.element.toString() + " " + format(lhs.token.lastCharacterIndex());
		String rhsString = rhs.element.toString() + " " + format(rhs.token.trimmedTokenStartIndex());
		return error("missing operator between " + lhsString + " and " + rhsString);
	}

	static ParseStatus emptyTagExpression() {
		return error("empty tag expression");
	}

	private static String format(int indexInTagExpression) {
		return "<" + indexInTagExpression + ">";
	}

	private static ParseStatus error(String errorMessage) {
		return new ParseStatus(errorMessage);
	}

	final String errorMessage;

	private ParseStatus(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ParseStatus process(Supplier<ParseStatus> step) {
		if (isSuccess()) {
			return step.get();
		}
		return this;
	}

	public boolean isError() {
		return !isSuccess();
	}

	private boolean isSuccess() {
		return null == errorMessage;
	}
}
