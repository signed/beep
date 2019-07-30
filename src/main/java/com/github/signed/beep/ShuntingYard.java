package com.github.signed.beep;

import static java.lang.Integer.MIN_VALUE;
import static com.github.signed.beep.Expressions.tag;
import static com.github.signed.beep.Operator.nullaryOperator;
import static com.github.signed.beep.ParseStatus.emptyTagExpression;
import static com.github.signed.beep.ParseStatus.missingClosingParenthesis;
import static com.github.signed.beep.ParseStatus.missingOpeningParenthesis;
import static com.github.signed.beep.ParseStatus.missingOperator;
import static com.github.signed.beep.ParseStatus.success;

import java.util.List;

/**
 * This is based on a modified version of the
 * <a href="https://en.wikipedia.org/wiki/Shunting-yard_algorithm">
 *     Shunting-yard algorithm</a>
 */
class ShuntingYard {
	private static final Operator RightParenthesis = nullaryOperator(")", -1);
	private static final Operator LeftParenthesis = nullaryOperator("(", -2);
	private static final Operator Sentinel = nullaryOperator("sentinel", MIN_VALUE);

	private final Operators validOperators = new Operators();
	private final Stack<TokenWith<Expression>> expressions = new DequeStack<>();
	private final Stack<TokenWith<Operator>> operators = new DequeStack<>();
	private final List<Token> tokens;

	ShuntingYard(List<Token> tokens) {
		this.tokens = tokens;
		pushOperatorAt(Token.singlePosition(-1, -1, ""), Sentinel);
	}

	public ParseResult execute() {
		// @formatter:off
		ParseStatus parseStatus = processTokens()
				.process(this::consumeRemainingOperators)
				.process(this::ensureOnlySingleExpressionRemains);
		// @formatter:on
		if (parseStatus.isError()) {
			return ParseResults.error(parseStatus.errorMessage);
		}
		return ParseResults.success(expressions.pop().element);
	}

	private ParseStatus processTokens() {
		ParseStatus parseStatus = success();
		for (int position = 0; parseStatus.isSuccess() && position < tokens.size(); ++position) {
			parseStatus = processTokenAt(position);
		}
		return parseStatus;
	}

	private ParseStatus processTokenAt(int position) {
		Token token = tokens.get(position);
		if (LeftParenthesis.represents(token.string())) {
			pushOperatorAt(token, LeftParenthesis);
			return success();
		}
		if (RightParenthesis.represents(token.string())) {
			return findMatchingLeftParenthesis(token);
		}
		if (validOperators.isOperator(token.string())) {
			Operator operator = validOperators.operatorFor(token.string());
			return findOperands(token, operator);
		}
		pushExpressionAt(token, tag(token.string()));
		return success();
	}

	private ParseStatus findMatchingLeftParenthesis(Token token) {
		while (!operators.isEmpty()) {
			TokenWith<Operator> tokenWithWithOperator = operators.pop();
			Operator operator = tokenWithWithOperator.element;
			if (LeftParenthesis.equals(operator)) {
				return success();
			}
			ParseStatus parseStatus = operator.createAndAddExpressionTo(expressions, tokenWithWithOperator.token);
			if (parseStatus.isError()) {
				return parseStatus;
			}
		}
		return missingOpeningParenthesis(token, RightParenthesis.representation());
	}

	private ParseStatus findOperands(Token token, Operator currentOperator) {
		while (currentOperator.hasLowerPrecedenceThan(previousOperator())
				|| currentOperator.hasSamePrecedenceAs(previousOperator()) && currentOperator.isLeftAssociative()) {
			TokenWith<Operator> tokenWithWithOperator = operators.pop();
			ParseStatus parseStatus = tokenWithWithOperator.element.createAndAddExpressionTo(expressions,
				tokenWithWithOperator.token);
			if (parseStatus.isError()) {
				return parseStatus;
			}
		}
		pushOperatorAt(token, currentOperator);
		return success();
	}

	private Operator previousOperator() {
		return operators.peek().element;
	}

	private void pushExpressionAt(Token token, Expression expression) {
		expressions.push(new TokenWith<>(token, expression));
	}

	private void pushOperatorAt(Token token, Operator operator) {
		operators.push(new TokenWith<>(token, operator));
	}

	private ParseStatus consumeRemainingOperators() {
		while (!operators.isEmpty()) {
			TokenWith<Operator> tokenWithWithOperator = operators.pop();
			Operator operator = tokenWithWithOperator.element;
			if (LeftParenthesis.equals(operator)) {
				return missingClosingParenthesis(tokenWithWithOperator.token, operator.representation());
			}
			ParseStatus parseStatus = operator.createAndAddExpressionTo(expressions, tokenWithWithOperator.token);
			if (parseStatus.isError()) {
				return parseStatus;
			}
		}
		return success();
	}

	private ParseStatus ensureOnlySingleExpressionRemains() {
		if (expressions.size() == 1) {
			return success();
		}
		if (expressions.isEmpty()) {
			return emptyTagExpression();
		}
		return missingOperator();
	}

}
