package com.github.signed.beep;

import static com.github.signed.beep.Associativity.Left;
import static com.github.signed.beep.ExpressionCreator.report;

import java.util.Optional;

class Operator {

    static Operator nullaryOperator(String representation, int precedence) {
        return new Operator(representation, precedence, 0, null, (expressions, position) -> ExpressionCreator.success);
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

    Optional<ParseError> createAndAddExpressionTo(Stack<Position<Expression>> expressions, int position) {
        if (expressions.size() < arity) {
            int mismatch = arity - expressions.size();
            String message = "missing operand";

            if (1 == arity) {
                String side = associativity == Left ? "lhs" : "rhs";
                message = "missing " + side + " operand";
            }

            if (2 == arity) {
                if (1 == mismatch) {
                    String side = position < expressions.peek().position ? "lhs" : "rhs";
                    message = "missing " + side + " operand";
                } else {
                    message = "missing lhs and rhs operand";
                }
            }

            return report(ParseError.Create(position, representation, message));
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
