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
	private final Stack<Position<Expression>> expressions = new DequeStack<>();
	private final Stack<Position<Operator>> operators = new DequeStack<>();

	private final List<Tokenizer.Token> tokens;

	ShuntingYard(List<Tokenizer.Token> tokens) {
		this.tokens = tokens;
		pushOperatorAt(-1, Sentinel);
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
		Tokenizer.Token token = tokens.get(position);
		if (LeftParenthesis.represents(token.string)) {
			pushOperatorAt(position, LeftParenthesis);
			return success();
		}
		if (RightParenthesis.represents(token.string)) {
			return findMatchingLeftParenthesis(position);
		}
		if (validOperators.isOperator(token.string)) {
			Operator operator = validOperators.operatorFor(token.string);
			return findOperands(position, operator);
		}
		pushExpressionAt(position, tag(token.string));
		return success();
	}

	private ParseStatus findMatchingLeftParenthesis(int position) {
		while (!operators.isEmpty()) {
			Position<Operator> positionWithOperator = operators.pop();
			Operator operator = positionWithOperator.element;
			if (LeftParenthesis.equals(operator)) {
				return success();
			}
			ParseStatus parseStatus = operator.createAndAddExpressionTo(expressions, positionWithOperator.position);
			if (parseStatus.isError()) {
				return parseStatus;
			}
		}
		return missingOpeningParenthesis(position, RightParenthesis.representation());
	}

	private ParseStatus findOperands(int position, Operator currentOperator) {
		while (currentOperator.hasLowerPrecedenceThan(previousOperator())
				|| currentOperator.hasSamePrecedenceAs(previousOperator()) && currentOperator.isLeftAssociative()) {
			Position<Operator> positionWithOperator = operators.pop();
			ParseStatus parseStatus = positionWithOperator.element.createAndAddExpressionTo(expressions,
				positionWithOperator.position);
			if (parseStatus.isError()) {
				return parseStatus;
			}
		}
		pushOperatorAt(position, currentOperator);
		return success();
	}

	private Operator previousOperator() {
		return operators.peek().element;
	}

	private void pushExpressionAt(int position, Expression expression) {
		expressions.push(new Position<>(position, expression));
	}

	private void pushOperatorAt(int position, Operator operator) {
		operators.push(new Position<>(position, operator));
	}

	private ParseStatus consumeRemainingOperators() {
		while (!operators.isEmpty()) {
			Position<Operator> positionWithOperator = operators.pop();
			Operator operator = positionWithOperator.element;
			if (LeftParenthesis.equals(operator)) {
				return missingClosingParenthesis(positionWithOperator.position, operator.representation());
			}
			ParseStatus parseStatus = operator.createAndAddExpressionTo(expressions, positionWithOperator.position);
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
