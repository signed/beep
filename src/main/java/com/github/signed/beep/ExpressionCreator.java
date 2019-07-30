package com.github.signed.beep;

import java.util.Optional;

interface ExpressionCreator {
    Optional<ParseError> Success = Optional.empty();

    static Optional<ParseError> ParseError(String message) {
        return Optional.of(ParseError.Create(message));
    }

    Optional<ParseError> accept(Stack<Position<Expression>> expressions, int position);
}
