package com.github.signed.beep;

import java.util.List;
import java.util.Optional;

import static java.lang.Integer.MIN_VALUE;
import static com.github.signed.beep.Expressions.tag;
import static com.github.signed.beep.Operator.nullaryOperator;

/**
 * The parser is based on a modified version of the <a href="https://en.wikipedia.org/wiki/Shunting-yard_algor">Shunting-yard algorithm</a>
 */
public class Parser {
    private static Operator RightParenthesis = nullaryOperator(")", -1);
    private static Operator LeftParenthesis = nullaryOperator("(", -2);
    private static Operator Sentinel = nullaryOperator("sentinel", MIN_VALUE);

    public static ParseResult parseExpressionFrom(String infixTagExpression) {
        return new Parser().parse(infixTagExpression);
    }

    private final Tokenizer tokenizer = new Tokenizer();

    ParseResult parse(String infixTagExpression) {
        return constructExpressionFrom(tokensDerivedFrom(infixTagExpression));
    }

    private List<String> tokensDerivedFrom(String infixTagExpression) {
        return tokenizer.tokenize(infixTagExpression);
    }

    private ParseResult constructExpressionFrom(List<String> tokens) {
        return new Flup(tokens).invoke();
    }

    private static class Flup {
        private final Operators validOperators = new Operators();
        private final Stack<Position<Expression>> expressions = new DequeStack<>();
        private final Stack<Position<Operator>> operators = new DequeStack<>();

        private List<String> tokens;

        public Flup(List<String> tokens) {
            this.tokens = tokens;
            pushPositionAt(-1, Sentinel);
        }

        public ParseResult invoke() {
            for (int i = 0; i < tokens.size(); ++i) {
                String token = tokens.get(i);
                if (LeftParenthesis.represents(token)) {
                    pushPositionAt(i, LeftParenthesis);
                } else if (RightParenthesis.represents(token)) {
                    boolean foundMatchingBracket = false;
                    while (!foundMatchingBracket && !operators.isEmpty()) {
                        Position<Operator> pop = operators.pop();
                        Operator candidate = pop.element;
                        if (LeftParenthesis.equals(candidate)) {
                            foundMatchingBracket = true;
                        } else {
                            Optional<ParseError> maybeParseError = candidate.createAndAddExpressionTo(expressions, pop.index);
                            if (maybeParseError.isPresent()) {
                                return ParseResult.error(maybeParseError.get());
                            }
                        }
                    }
                    if (!foundMatchingBracket) {
                        return ParseResult.error(ParseError.Create("missing opening parenthesis"));
                    }
                } else if (validOperators.isOperator(token)) {
                    Operator operator = validOperators.operatorFor(token);
                    while (operator.hasLowerPrecedenceThan(operators.peek().element)
                            || operator.hasSamePrecedenceAs(operators.peek().element) && operator.isLeftAssociative()) {
                        Position<Operator> pop = operators.pop();
                        Optional<ParseError> maybeParseError = pop.element.createAndAddExpressionTo(expressions, pop.index);
                        if (maybeParseError.isPresent()) {
                            return ParseResult.error(maybeParseError.get());
                        }
                    }
                    pushPositionAt(i, operator);
                } else {
                    pushPositionAt(i, tag(token));
                }
            }

            while (!operators.isEmpty()) {
                Position<Operator> pop = operators.pop();
                Operator operator = pop.element;
                if (LeftParenthesis.equals(operator)) {
                    return ParseResult.error(ParseError.Create("missing closing parenthesis"));
                }

                Optional<ParseError> maybeParseError = operator.createAndAddExpressionTo(expressions, pop.index);
                if (maybeParseError.isPresent()) {
                    return ParseResult.error(maybeParseError.get());
                }
            }

            if (expressions.size() != 1) {
                if (expressions.isEmpty()) {
                    return ParseResult.error(ParseError.Create("empty tag expression"));
                }
                return ParseResult.error(ParseError.Create("missing operator"));
            }
            return ParseResult.success(expressions.pop().element);
        }

        private void pushPositionAt(int i, Expression expression) {
            expressions.push(new Position<>(i, expression));
        }

        private void pushPositionAt(int i, Operator operator) {
            operators.push(new Position<>(i, operator));
        }
    }
}
