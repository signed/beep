package com.github.signed.beep;

import java.util.Optional;
import java.util.function.Function;

public class ParseResult {

    public static ParseResult error(ParseError parseError) {
        return new ParseResult(parseError, null);
    }

    public static ParseResult success(Expression expression) {
        return new ParseResult(null, expression);
    }

    private final ParseError parseError;
    private final Expression expression;

    private ParseResult(ParseError parseError, Expression expression) {
        this.parseError = parseError;
        this.expression = expression;
    }

    public Optional<String> parseError(){
        return Optional.ofNullable(parseError).map(e -> e.message);
    }

    public Expression expressionOrThrow(Function<ParseError, RuntimeException > error) {
        if (null != parseError) {
            throw error.apply(parseError);
        }
        return expression;
    }
}
