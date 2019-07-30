package com.github.signed.beep;

import java.util.Optional;

interface ExpressionCreator {
    Optional<ParseError> Success = Optional.empty();

    static Optional<ParseError> ParseError(ParseError error) {
        return Optional.of(error);
    }

    Optional<ParseError> accept(Stack<Position<Expression>> expressions, int position);
}
