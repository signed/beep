package com.github.signed.beep;

import java.util.function.Supplier;

class ParseStatus {

	static ParseStatus success() {
		return new ParseStatus(null);
	}

	static ParseStatus missingOperatorBetween(Position<Expression> rhs, Position<Expression> lhs) {
		return new ParseStatus("missing operator between " + format(lhs) + " and " + format(rhs));
	}

	private static String format(Position<Expression> rhs) {
		return rhs.element.toString() + " <" + rhs.position + ">";
	}

	static ParseStatus problemParsing(int position, String representation) {
		return Create(position, representation, "problem parsing");
	}

	static ParseStatus missingOpeningParenthesis(int position, String representation) {
		return Create(position, representation, "missing opening parenthesis");
	}

	static ParseStatus missingClosingParenthesis(int position, String representation) {
		return Create(position, representation, "missing closing parenthesis");
	}

	static ParseStatus Create(int position, String operatorRepresentation, String message) {
		return new ParseStatus(operatorRepresentation + " at <" + position + "> " + message);
	}

	static ParseStatus missingOperator() {
		return new ParseStatus("missing operator");
	}

	static ParseStatus missingRhsOperand(String representation, int position) {
		return Create(position, representation, "missing rhs operand");
	}

	static ParseStatus emptyTagExpression() {
		return new ParseStatus("empty tag expression");
	}

	final String message;

	private ParseStatus(String message) {
		this.message = message;
	}

	public ParseStatus process(Supplier<ParseStatus> step) {
		if (noError()) {
			return step.get();
		}
		return this;
	}

	public boolean noError() {
		return null == message;
	}

	public boolean isError() {
		return !noError();
	}
}
