package com.github.signed.beep;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static com.github.signed.beep.Associativity.Left;
import static com.github.signed.beep.Associativity.Right;
import static com.github.signed.beep.Expressions.and;
import static com.github.signed.beep.Expressions.not;
import static com.github.signed.beep.Expressions.or;

import java.util.Map;
import java.util.stream.Stream;

class Operators {

    private static final Operator Not = Operator.unaryOperator("!", 3, Right, (expressionStack, position) -> {
                Position<Expression> rhs = expressionStack.pop();
                if (position < rhs.index) {
                    Expression not = not(rhs.element);
                    expressionStack.push(new Position<>(position, not));
                    return true;
                }
                return false;
            });

    private static final Operator And = Operator.binaryOperator("&", 2, Left, (expressionStack, position) -> {
        Position<Expression> rhs = expressionStack.pop();
        Position<Expression> lhs = expressionStack.pop();
        if (lhs.index < position && position < rhs.index) {
            expressionStack.push(new Position<>(position, and(lhs.element, rhs.element)));
            return true;
        }
        return false;
    });

    private static final Operator Or = Operator.binaryOperator("|", 1, Left, (expressionStack, position) -> {
        Position<Expression> rhs = expressionStack.pop();
        Position<Expression> lhs = expressionStack.pop();
        if (lhs.index < position && position < rhs.index) {
            expressionStack.push(new Position<>(position, or(lhs.element, rhs.element)));
            return true;
        }
        return false;
    });

    private final Map<String, Operator> representationToOperator = Stream.of(Not, And, Or).collect(
            toMap(Operator::representation, identity()));

    boolean isOperator(String token) {
        return representationToOperator.containsKey(token);
    }

    Operator operatorFor(String token) {
        return representationToOperator.get(token);
    }

}
