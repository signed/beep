package com.github.signed.beep;

import java.util.Optional;

interface ExpressionCreator {
    Optional<String> Success = Optional.empty();

    static Optional<String> ParseError(String message) {
        return Optional.of(message);
    }

    Optional<String> accept(Stack<Position<Expression>> expressions, int position);
}
