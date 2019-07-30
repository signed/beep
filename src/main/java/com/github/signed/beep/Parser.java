package com.github.signed.beep;

import static java.lang.Integer.MIN_VALUE;
import static com.github.signed.beep.Expressions.tag;
import static com.github.signed.beep.Operator.nullaryOperator;

import java.util.List;
import java.util.Optional;

/**
 * The parser is based on a modified version of the <a href="https://en.wikipedia.org/wiki/Shunting-yard_algorithm">Shunting-yard algorithm</a>
 */
public class Parser {
	private static Operator RightParenthesis = nullaryOperator(")", -1);
	private static Operator LeftParenthesis = nullaryOperator("(", -2);
	private static Operator Sentinel = nullaryOperator("sentinel", MIN_VALUE);

	public static Optional<Expression> parseExpressionFrom(String infixTagExpression) {
		return new Parser().parse(infixTagExpression);
	}

	private final Operators validOperators = new Operators();
	private final Tokenizer tokenizer = new Tokenizer();

	Optional<Expression> parse(String infixTagExpression) {
		Stack<Expression> expressions = new DequeStack<>();
		Stack<Operator> operators = new DequeStack<>();
		operators.push(Sentinel);

		List<String> tokens = tokenizer.tokenize(infixTagExpression);
		for (String token : tokens) {
			if (LeftParenthesis.represents(token)) {
				operators.push(LeftParenthesis);
			}
			else if (RightParenthesis.represents(token)) {
				boolean foundMatchingBracket = false;
				while (!foundMatchingBracket && !operators.isEmpty()) {
					Operator candidate = operators.pop();
					if (LeftParenthesis.equals(candidate)) {
						foundMatchingBracket = true;
					}
					else {
						if (!candidate.createAndAddExpressionTo(expressions)) {
							return Optional.empty();
						}
					}
				}
				if (!foundMatchingBracket) {
					return Optional.empty();
				}
			}
			else if (validOperators.isOperator(token)) {
				Operator operator = validOperators.operatorFor(token);
				while (operator.hasLowerPrecedenceThan(operators.peek())
						|| operator.hasSamePrecedenceAs(operators.peek()) && operator.isLeftAssociative()) {
					if (!operators.pop().createAndAddExpressionTo(expressions)) {
						return Optional.empty();
					}
				}
				operators.push(operator);
			}
			else {
				expressions.push(tag(token));
			}
		}

		while (!operators.isEmpty()) {
			Operator operator = operators.pop();
			if (LeftParenthesis.equals(operator) || !operator.createAndAddExpressionTo(expressions)) {
				return Optional.empty();
			}
		}

		if (expressions.size() != 1) {
			return Optional.empty();
		}
		return Optional.of(expressions.pop());
	}

}
