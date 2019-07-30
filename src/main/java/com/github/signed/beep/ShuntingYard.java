package com.github.signed.beep;

import java.util.List;
import java.util.Optional;

import static java.lang.Integer.MIN_VALUE;
import static com.github.signed.beep.Expressions.tag;
import static com.github.signed.beep.Operator.nullaryOperator;

/**
 * This is based on a modified version of the <a href="https://en.wikipedia.org/wiki/Shunting-yard_algor">Shunting-yard algorithm</a>
 */
class ShuntingYard {
    private static Operator RightParenthesis = nullaryOperator(")", -1);
    private static Operator LeftParenthesis = nullaryOperator("(", -2);
    private static Operator Sentinel = nullaryOperator("sentinel", MIN_VALUE);

    private final Operators validOperators = new Operators();
    private final Stack<Position<Expression>> expressions = new DequeStack<>();
    private final Stack<Position<Operator>> operators = new DequeStack<>();
    private Optional<ParseError> maybeParseError = Optional.empty();

    private List<String> tokens;

    public ShuntingYard(List<String> tokens) {
        this.tokens = tokens;
        pushPositionAt(-1, Sentinel);
    }

    public ParseResult execute() {
        for (int i = 0; !maybeParseError.isPresent() && i < tokens.size(); ++i) {
            String token = tokens.get(i);
            if (LeftParenthesis.represents(token)) {
                pushPositionAt(i, LeftParenthesis);
            } else if (RightParenthesis.represents(token)) {
                findMatchingLeftParenthesis(i);
            } else if (validOperators.isOperator(token)) {
                findOperands(i, token);
            } else {
                pushPositionAt(i, tag(token));
            }
        }

        if (maybeParseError.isPresent()) {
            return ParseResult.error(maybeParseError.get());
        }

        while (!operators.isEmpty()) {
            Position<Operator> pop = operators.pop();
            Operator operator = pop.element;
            if (LeftParenthesis.equals(operator)) {
                return ParseResult.error(
                        ParseError.Create(pop.position, pop.element.representation(), "missing closing parenthesis"));
            }

            Optional<ParseError> maybeParseError = operator.createAndAddExpressionTo(expressions, pop.position);
            if (maybeParseError.isPresent()) {
                return ParseResult.error(maybeParseError.get());
            }
        }

        if (expressions.size() != 1) {
            if (expressions.isEmpty()) {
                return ParseResult.error(ParseError.emptyTagExpression());
            }
            return ParseResult.error(ParseError.missingOperator());
        }
        return ParseResult.success(expressions.pop().element);
    }

    public void findOperands(int i, String token) {
        Operator operator = validOperators.operatorFor(token);
        while (operator.hasLowerPrecedenceThan(operators.peek().element)
                || operator.hasSamePrecedenceAs(operators.peek().element) && operator.isLeftAssociative()) {
            Position<Operator> pop = operators.pop();
            maybeParseError = pop.element.createAndAddExpressionTo(expressions, pop.position);
        }
        pushPositionAt(i, operator);
    }

    public void findMatchingLeftParenthesis(int i) {
        boolean foundMatchingParenthesis = false;
        while (!foundMatchingParenthesis && !operators.isEmpty()) {
            Position<Operator> pop = operators.pop();
            Operator candidate = pop.element;
            if (LeftParenthesis.equals(candidate)) {
                foundMatchingParenthesis = true;
            } else {
                maybeParseError = candidate.createAndAddExpressionTo(expressions, pop.position);
            }
        }
        if (!foundMatchingParenthesis) {
            maybeParseError = Optional.of(ParseError.Create(i, RightParenthesis.representation(), "missing opening parenthesis"));
        }
    }

    private void pushPositionAt(int i, Expression expression) {
        expressions.push(new Position<>(i, expression));
    }

    private void pushPositionAt(int i, Operator operator) {
        operators.push(new Position<>(i, operator));
    }
}
