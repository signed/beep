package com.github.signed.beep;

import java.util.function.Supplier;

class ParseStatus {

	static ParseStatus success() {
		return error(null);
	}

	static ParseStatus problemParsing(int position, String representation) {
		return errorAt(position, representation, "problem parsing");
	}

	static ParseStatus missingOpeningParenthesis(int position, String representation) {
		return errorAt(position, representation, "missing opening parenthesis");
	}

	static ParseStatus missingClosingParenthesis(int position, String representation) {
		return errorAt(position, representation, "missing closing parenthesis");
	}

	static ParseStatus missingRhsOperand(int position, String representation) {
		return errorAt(position, representation, "missing rhs operand");
	}

	static ParseStatus errorAt(int position, String operatorRepresentation, String message) {
		return error(operatorRepresentation + " at " + format(position) + " " + message);
	}

	static ParseStatus missingOperatorBetween(Position<Expression> lhs, Position<Expression> rhs) {
		return error("missing operator between " + format(lhs) + " and " + format(rhs));
	}

	static ParseStatus missingOperator() {
		return error("missing operator");
	}

	static ParseStatus emptyTagExpression() {
		return error("empty tag expression");
	}

	private static String format(Position<Expression> position) {
		return position.element.toString() + " " + format(position.position);
	}

	private static String format(int position) {
		return "<" + position + ">";
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

	public boolean isSuccess() {
		return null == errorMessage;
	}
}
