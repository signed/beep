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

	static ParseStatus missingOperatorBetween(Position<Expression> rhs, Position<Expression> lhs) {
		return error("missing operator between " + format(lhs) + " and " + format(rhs));
	}

	static ParseStatus errorAt(int position, String operatorRepresentation, String message) {
		return error(operatorRepresentation + " at <" + position + "> " + message);
	}

	static ParseStatus missingOperator() {
		return error("missing operator");
	}

	static ParseStatus emptyTagExpression() {
		return error("empty tag expression");
	}

	private static String format(Position<Expression> position) {
		int position1 = position.position;
		return position.element.toString() + " <" + position1 + ">";
	}

	private static ParseStatus error(String errorMessage) {
		return new ParseStatus(errorMessage);
	}

	final String errorMessage;

	private ParseStatus(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public ParseStatus process(Supplier<ParseStatus> step) {
		if (noError()) {
			return step.get();
		}
		return this;
	}

	public boolean noError() {
		return null == errorMessage;
	}

	public boolean isError() {
		return !noError();
	}
}
