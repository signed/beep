package com.github.signed.beep;

import static java.lang.Integer.MIN_VALUE;
import static com.github.signed.beep.Expressions.tag;
import static com.github.signed.beep.Operator.nullaryOperator;
import static com.github.signed.beep.ParseStatus.missingClosingParenthesis;

import java.util.List;

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
        ParseStatus parseStatus = processTokens()
                .process(this::consumeRemainingOperators)
                .process(this::ensureOnlySingleExpressionRemains);
        if (parseStatus.isError()) {
            return ParseResult.error(parseStatus);
        }

        return ParseResult.success(expressions.pop().element);
    }

    public ParseStatus ensureOnlySingleExpressionRemains() {
        ParseStatus maybeParseStatus3 = ParseStatus.NoParseError();
        if (expressions.size() != 1) {
            if (expressions.isEmpty()) {
                maybeParseStatus3 = ParseStatus.emptyTagExpression();
            } else {
                maybeParseStatus3 = ParseStatus.missingOperator();
            }
        }
        return maybeParseStatus3;
    }

    public ParseStatus consumeRemainingOperators() {
        ParseStatus maybeParseStatus4 = ParseStatus.NoParseError();
        while (maybeParseStatus4.noParseError() && !operators.isEmpty()) {
            Position<Operator> pop = operators.pop();
            Operator operator = pop.element;
            if (LeftParenthesis.equals(operator)) {
                maybeParseStatus4 = missingClosingParenthesis(pop.position, pop.element.representation());
            } else {
                ParseStatus maybeParseStatus2 = operator.createAndAddExpressionTo(expressions, pop.position);
                if (maybeParseStatus2.isError()) {
                    maybeParseStatus4 = maybeParseStatus2;
                }
            }
        }
        return maybeParseStatus4;
    }

    private ParseStatus processTokens() {
        ParseStatus maybeParseStatus = ParseStatus.NoParseError();
        for (int position = 0; maybeParseStatus.noParseError() && position < tokens.size(); ++position) {
            String token = tokens.get(position);
            if (LeftParenthesis.represents(token)) {
                pushPositionAt(position, LeftParenthesis);
            } else if (RightParenthesis.represents(token)) {
                maybeParseStatus = findMatchingLeftParenthesis(position);
            } else if (validOperators.isOperator(token)) {
                maybeParseStatus = findOperands(position, token);
            } else {
                pushPositionAt(position, tag(token));
            }
        }
        return maybeParseStatus;
    }

    private ParseStatus findOperands(int position, String token) {
        Operator currentOperator = validOperators.operatorFor(token);
        while (currentOperator.hasLowerPrecedenceThan(operators.peek().element)
                || currentOperator.hasSamePrecedenceAs(operators.peek().element) && currentOperator.isLeftAssociative()) {
            Position<Operator> pop = operators.pop();
            ParseStatus maybeParseStatus = pop.element.createAndAddExpressionTo(expressions, pop.position);
            if (maybeParseStatus.isError()) {
                return maybeParseStatus;
            }
        }
        pushPositionAt(position, currentOperator);
        return stepSuccessful();
    }

    private ParseStatus findMatchingLeftParenthesis(int position) {
        boolean foundMatchingParenthesis = false;
        while (!foundMatchingParenthesis && !operators.isEmpty()) {
            Position<Operator> pop = operators.pop();
            Operator candidate = pop.element;
            if (LeftParenthesis.equals(candidate)) {
                foundMatchingParenthesis = true;
            } else {
                ParseStatus maybeParseStatus = candidate.createAndAddExpressionTo(expressions, pop.position);
                if (maybeParseStatus.isError()) {
                    return maybeParseStatus;
                }
            }
        }
        if (!foundMatchingParenthesis) {
            String representation = RightParenthesis.representation();
            return ParseStatus.missingOpeningParenthesis(position, representation);
        }
        return stepSuccessful();
    }

    public ParseStatus stepSuccessful() {
        return ParseStatus.NoParseError();
    }

    private void pushPositionAt(int i, Expression expression) {
        expressions.push(new Position<>(i, expression));
    }

    private void pushPositionAt(int i, Operator operator) {
        operators.push(new Position<>(i, operator));
    }
}
