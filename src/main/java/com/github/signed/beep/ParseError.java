package com.github.signed.beep;

public class ParseError {

	static ParseError missingOperatorBetween(int lhsPosition, String lhsRepresentation, int rhsPosition,
                                             String rhsRepresentation) {
		return new ParseError("missing operator between " + lhsRepresentation + " <" + lhsPosition + "> and "
				+ rhsRepresentation + " <" + rhsPosition + ">");
	}

    static ParseError problemParsing(int position, String representation) {
        return Create(position, representation, "problem parsing");
    }

    static ParseError Create(int position, String operatorRepresentation, String message) {
        return new ParseError(operatorRepresentation + " at <" + position + "> " + message);
    }

	static ParseError missingOperator() {
		return new ParseError("missing operator");
	}

	static ParseError emptyTagExpression() {
		return new ParseError("empty tag expression");
	}

	final String message;

	private ParseError(String message) {
		this.message = message;
	}
}
