package com.github.signed.beep;

public class ParseError {

    static ParseError Create(int position, String operatorRepresentation, String message) {
        return new ParseError(operatorRepresentation + " at <" + position + "> "+ message);
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
