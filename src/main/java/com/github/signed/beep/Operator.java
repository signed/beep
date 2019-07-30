package com.github.signed.beep;

import static com.github.signed.beep.Operator.Associativity.Left;
import static com.github.signed.beep.ParseStatus.missingOperatorBetween;
import static com.github.signed.beep.ParseStatus.missingRhsOperand;
import static com.github.signed.beep.ParseStatus.problemParsing;
import static com.github.signed.beep.ParseStatus.success;

import java.util.function.BiFunction;
import java.util.function.Function;

class Operator {

	enum Associativity {
		Left, Right
	}

	interface ExpressionCreator {
		ParseStatus createExpressionAndAddTo(Stack<Position<Expression>> expressions, Token token);
	}

	static Operator nullaryOperator(String representation, int precedence) {
		return new Operator(representation, precedence, 0, null, (expressions, position) -> success());
	}

	static Operator unaryOperator(String representation, int precedence, Associativity associativity,
			Function<Expression, Expression> unaryExpression) {
		return new Operator(representation, precedence, 1, associativity, (expressions, token) -> {
			Position<Expression> rhs = expressions.pop();
			if (token.position < rhs.position) {
				expressions.push(new Position<>(token, unaryExpression.apply(rhs.element)));
				return success();
			}
			return missingRhsOperand(token, representation);
		});
	}

	static Operator binaryOperator(String representation, int precedence, Associativity associativity,
			BiFunction<Expression, Expression, Expression> binaryExpression) {
		return new Operator(representation, precedence, 2, associativity, (expressions, token) -> {
			Position<Expression> rhs = expressions.pop();
			Position<Expression> lhs = expressions.pop();
			if (lhs.position < token.position && token.position < rhs.position) {
				expressions.push(new Position<>(token, binaryExpression.apply(lhs.element, rhs.element)));
				return success();
			}
			if (token.position > rhs.position) {
				return missingRhsOperand(token, representation);
			}
			if (token.position < lhs.position) {
				return missingOperatorBetween(lhs, rhs);
			}
			return problemParsing(token, representation);
		});
	}

	private final String representation;
	private final int precedence;
	private final int arity;
	private final Associativity associativity;
	private final ExpressionCreator expressionCreator;

	private Operator(String representation, int precedence, int arity, Associativity associativity,
			ExpressionCreator expressionCreator) {
		this.representation = representation;
		this.precedence = precedence;
		this.arity = arity;
		this.associativity = associativity;
		this.expressionCreator = expressionCreator;
	}

	boolean represents(String token) {
		return representation.equals(token);
	}

	String representation() {
		return representation;
	}

	boolean hasLowerPrecedenceThan(Operator operator) {
		return this.precedence < operator.precedence;
	}

	boolean hasSamePrecedenceAs(Operator operator) {
		return this.precedence == operator.precedence;
	}

	boolean isLeftAssociative() {
		return Left == associativity;
	}

	ParseStatus createAndAddExpressionTo(Stack<Position<Expression>> expressions, Token token) {
		if (expressions.size() < arity) {
			String message = createMissingOperandMessage(token, expressions);
			return ParseStatus.errorAt(token, representation, message);
		}
		return expressionCreator.createExpressionAndAddTo(expressions, token);
	}

	private String createMissingOperandMessage(Token token, Stack<Position<Expression>> expressions) {
		if (1 == arity) {
			return missingOneOperand(associativity == Left ? "lhs" : "rhs");
		}

		if (2 == arity) {
			int mismatch = arity - expressions.size();
			if (2 == mismatch) {
				return "missing lhs and rhs operand";
			}
			return missingOneOperand(token.position < expressions.peek().position ? "lhs" : "rhs");
		}
		return "missing operand";
	}

	private String missingOneOperand(String side) {
		return "missing " + side + " operand";
	}

}
