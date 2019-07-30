package com.github.signed.beep;

public class ParseError {

    static ParseError Create(String message) {
        return new ParseError(message);
    }

    final String message;

    private ParseError(String message) {
        this.message = message;
    }

}
