package com.github.signed.beep;

import static java.lang.Integer.MIN_VALUE;
import static com.github.signed.beep.Expressions.tag;
import static com.github.signed.beep.Operator.nullaryOperator;
import static com.github.signed.beep.ParseError.missingClosingParenthesis;

import java.util.List;
import java.util.Optional;

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

    private final List<String> tokens;

    public ShuntingYard(List<String> tokens) {
        this.tokens = tokens;
        pushPositionAt(-1, Sentinel);
    }

    public ParseResult execute() {
        Optional<ParseError> maybeParseError1 = processTokens();
        if (maybeParseError1.isPresent()) {
            return ParseResult.error(maybeParseError1.get());
        }
        Optional<ParseError> maybeParseError2 = consumeRemainingOperators();
        if (maybeParseError2.isPresent()) {
            return ParseResult.error(maybeParseError2.get());
        }

        Optional<ParseError> maybeParseError3 = ensureOnlySingleExpressionRemains();
        if (maybeParseError3.isPresent()) {
            return ParseResult.error(maybeParseError3.get());
        }

        return ParseResult.success(expressions.pop().element);
    }

    public Optional<ParseError> ensureOnlySingleExpressionRemains() {
        Optional<ParseError> maybeParseError3 = Optional.empty();
        if (expressions.size() != 1) {
            if (expressions.isEmpty()) {
                ParseError parseError = ParseError.emptyTagExpression();
                maybeParseError3 = Optional.of(parseError);
            } else {
                ParseError parseError = ParseError.missingOperator();
                maybeParseError3 = Optional.of(parseError);
            }
        }
        return maybeParseError3;
    }

    public Optional<ParseError> consumeRemainingOperators() {
        Optional<ParseError> maybeParseError4 = Optional.empty();
        while (!maybeParseError4.isPresent() && !operators.isEmpty()) {
            Position<Operator> pop = operators.pop();
            Operator operator = pop.element;
            if (LeftParenthesis.equals(operator)) {
                maybeParseError4 = Optional.of(missingClosingParenthesis(pop.position, pop.element.representation()));
            } else {
                Optional<ParseError> maybeParseError2 = operator.createAndAddExpressionTo(expressions, pop.position);
                if (maybeParseError2.isPresent()) {
                    maybeParseError4 = maybeParseError2;
                }
            }
        }
        return maybeParseError4;
    }

    private Optional<ParseError> processTokens() {
        Optional<ParseError> maybeParseError = Optional.empty();
        for (int position = 0; !maybeParseError.isPresent() && position < tokens.size(); ++position) {
            String token = tokens.get(position);
            if (LeftParenthesis.represents(token)) {
                pushPositionAt(position, LeftParenthesis);
            } else if (RightParenthesis.represents(token)) {
                maybeParseError = findMatchingLeftParenthesis(position);
            } else if (validOperators.isOperator(token)) {
                maybeParseError = findOperands(position, token);
            } else {
                pushPositionAt(position, tag(token));
            }
        }
        return maybeParseError;
    }

    private Optional<ParseError> findOperands(int position, String token) {
        Operator currentOperator = validOperators.operatorFor(token);
        while (currentOperator.hasLowerPrecedenceThan(operators.peek().element)
                || currentOperator.hasSamePrecedenceAs(operators.peek().element) && currentOperator.isLeftAssociative()) {
            Position<Operator> pop = operators.pop();
            Optional<ParseError> maybeParseError = pop.element.createAndAddExpressionTo(expressions, pop.position);
            if (maybeParseError.isPresent()) {
                return maybeParseError;
            }
        }
        pushPositionAt(position, currentOperator);
        return stepSuccessful();
    }

    private Optional<ParseError> findMatchingLeftParenthesis(int position) {
        boolean foundMatchingParenthesis = false;
        while (!foundMatchingParenthesis && !operators.isEmpty()) {
            Position<Operator> pop = operators.pop();
            Operator candidate = pop.element;
            if (LeftParenthesis.equals(candidate)) {
                foundMatchingParenthesis = true;
            } else {
                Optional<ParseError> maybeParseError = candidate.createAndAddExpressionTo(expressions, pop.position);
                if (maybeParseError.isPresent()) {
                    return maybeParseError;
                }
            }
        }
        if (!foundMatchingParenthesis) {
            String representation = RightParenthesis.representation();
            return Optional.of(ParseError.missingOpeningParenthesis(position, representation));
        }
        return stepSuccessful();
    }

    public Optional<ParseError> stepSuccessful() {
        return Optional.empty();
    }

    private void pushPositionAt(int i, Expression expression) {
        expressions.push(new Position<>(i, expression));
    }

    private void pushPositionAt(int i, Operator operator) {
        operators.push(new Position<>(i, operator));
    }
}
