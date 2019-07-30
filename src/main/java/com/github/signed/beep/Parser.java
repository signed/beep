package com.github.signed.beep;

import java.util.List;

import static java.lang.Integer.MIN_VALUE;
import static com.github.signed.beep.Expressions.tag;
import static com.github.signed.beep.Operator.nullaryOperator;

/**
 * The parser is based on a modified version of the <a href="https://en.wikipedia.org/wiki/Shunting-yard_algorithm">Shunting-yard algorithm</a>
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
                            if (!candidate.createAndAddExpressionTo(expressions, pop.index)) {
                                return ParseResult.error("hmm");
                            }
                        }
                    }
                    if (!foundMatchingBracket) {
                        return ParseResult.error("missing opening parenthesis");
                    }
                } else if (validOperators.isOperator(token)) {
                    Operator operator = validOperators.operatorFor(token);
                    while (operator.hasLowerPrecedenceThan(operators.peek().element)
                            || operator.hasSamePrecedenceAs(operators.peek().element) && operator.isLeftAssociative()) {
                        Position<Operator> pop = operators.pop();
                        if (!pop.element.createAndAddExpressionTo(expressions, pop.index)) {
                            return ParseResult.error("hoo");
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
                    return ParseResult.error("missing closing parenthesis");
                }

                if (!operator.createAndAddExpressionTo(expressions,pop.index)) {
                    return ParseResult.error("hpp");
                }
            }

            if (expressions.size() != 1) {
                if (expressions.isEmpty()) {
                    return ParseResult.error("empty tag expression");
                }
                return ParseResult.error("missing operator");
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
