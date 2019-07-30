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
	private static final Operator RightParenthesis = nullaryOperator(")", -1);
	private static final Operator LeftParenthesis = nullaryOperator("(", -2);
	private static final Operator Sentinel = nullaryOperator("sentinel", MIN_VALUE);

	private final Operators validOperators = new Operators();
	private final Stack<Position<Expression>> expressions = new DequeStack<>();
	private final Stack<Position<Operator>> operators = new DequeStack<>();

	private final List<String> tokens;

	ShuntingYard(List<String> tokens) {
		this.tokens = tokens;
		pushPositionAt(-1, Sentinel);
	}

	public ParseResult execute() {
        // @formatter:off
		ParseStatus parseStatus = processTokens()
                .process(this::consumeRemainingOperators)
                .process(this::ensureOnlySingleExpressionRemains);
        // @formatter:on
		if (parseStatus.isError()) {
			return ParseResult.error(parseStatus.message);
		}
		return ParseResult.success(expressions.pop().element);
	}

	private ParseStatus processTokens() {
		ParseStatus parseStatus = ParseStatus.success();
		for (int position = 0; parseStatus.noError() && position < tokens.size(); ++position) {
			String token = tokens.get(position);
			if (LeftParenthesis.represents(token)) {
				pushPositionAt(position, LeftParenthesis);
			}
			else if (RightParenthesis.represents(token)) {
				parseStatus = findMatchingLeftParenthesis(position);
			}
			else if (validOperators.isOperator(token)) {
				Operator operator = validOperators.operatorFor(token);
				parseStatus = findOperands(position, operator);
			}
			else {
				pushPositionAt(position, tag(token));
			}
		}
		return parseStatus;
	}

	private ParseStatus findMatchingLeftParenthesis(int position) {
		while (!operators.isEmpty()) {
			Position<Operator> pop = operators.pop();
			Operator candidate = pop.element;
			if (LeftParenthesis.equals(candidate)) {
				return ParseStatus.success();
			}
			ParseStatus parseStatus = candidate.createAndAddExpressionTo(expressions, pop.position);
			if (parseStatus.isError()) {
				return parseStatus;
			}
		}
		return ParseStatus.missingOpeningParenthesis(position, RightParenthesis.representation());
	}

	private ParseStatus findOperands(int position, Operator currentOperator) {
		while (currentOperator.hasLowerPrecedenceThan(previousOperator())
				|| currentOperator.hasSamePrecedenceAs(previousOperator()) && currentOperator.isLeftAssociative()) {
			Position<Operator> pop = operators.pop();
			ParseStatus parseStatus = pop.element.createAndAddExpressionTo(expressions, pop.position);
			if (parseStatus.isError()) {
				return parseStatus;
			}
		}
		pushPositionAt(position, currentOperator);
		return ParseStatus.success();
	}

	private Operator previousOperator() {
		return operators.peek().element;
	}

	private void pushPositionAt(int position, Expression expression) {
		expressions.push(new Position<>(position, expression));
	}

	private void pushPositionAt(int position, Operator operator) {
		operators.push(new Position<>(position, operator));
	}

	private ParseStatus consumeRemainingOperators() {
		while (!operators.isEmpty()) {
			Position<Operator> pop = operators.pop();
			Operator operator = pop.element;
			if (LeftParenthesis.equals(operator)) {
				return missingClosingParenthesis(pop.position, pop.element.representation());
			}
			ParseStatus maybeParseStatus2 = operator.createAndAddExpressionTo(expressions, pop.position);
			if (maybeParseStatus2.isError()) {
				return maybeParseStatus2;
			}
		}
		return ParseStatus.success();
	}

	private ParseStatus ensureOnlySingleExpressionRemains() {
		if (expressions.size() == 1) {
			return ParseStatus.success();
		}
		if (expressions.isEmpty()) {
			return ParseStatus.emptyTagExpression();
		}
		return ParseStatus.missingOperator();
	}

}
