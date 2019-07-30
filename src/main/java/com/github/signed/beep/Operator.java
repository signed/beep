package com.github.signed.beep;

import java.util.Optional;

import static com.github.signed.beep.Associativity.Left;
import static com.github.signed.beep.ExpressionCreator.ParseError;

class Operator {

    static Operator nullaryOperator(String representation, int precedence) {
        return new Operator(representation, precedence, 0, null, (expressions, position) -> ExpressionCreator.Success);
    }

    static Operator unaryOperator(String representation, int precedence, Associativity associativity,
                                  ExpressionCreator expressionCreator) {
        return new Operator(representation, precedence, 1, associativity, expressionCreator);
    }

    static Operator binaryOperator(String representation, int precedence, Associativity associativity,
                                   ExpressionCreator expressionCreator) {
        return new Operator(representation, precedence, 2, associativity, expressionCreator);
    }

    private final String representation;
    private final int precedence;
    private final int arity;
    private final Associativity associativity;
    private final ExpressionCreator expressionCreator;

    private Operator(String representation, int precedence, int arity, Associativity associativity,
                     ExpressionCreator expressionCreator) {
        this.representation = representation;
        this.precedence = precedence;
        this.arity = arity;
        this.associativity = associativity;
        this.expressionCreator = expressionCreator;
    }

    String representation() {
        return representation;
    }

    boolean hasLowerPrecedenceThan(Operator operator) {
        return this.precedence < operator.precedence;
    }

    boolean represents(String token) {
        return representation.equals(token);
    }

    Optional<String> createAndAddExpressionTo(Stack<Position<Expression>> expressions, int position) {
        if (expressions.size() < arity) {
            return ParseError("missing operand");
        }
        return expressionCreator.accept(expressions, position);
    }

    boolean hasSamePrecedenceAs(Operator operator) {
        return this.precedence == operator.precedence;
    }

    boolean isLeftAssociative() {
        return Left == associativity;
    }
}
