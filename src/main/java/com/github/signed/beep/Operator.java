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
		ParseStatus createExpressionAndAddTo(Stack<Position<Expression>> expressions, int position);
	}

	static Operator nullaryOperator(String representation, int precedence) {
		return new Operator(representation, precedence, 0, null, (expressions, position) -> success());
	}

	static Operator unaryOperator(String representation, int precedence, Associativity associativity,
			Function<Expression, Expression> unaryExpression) {
		return new Operator(representation, precedence, 1, associativity, (expressions, position) -> {
			Position<Expression> rhs = expressions.pop();
			if (position < rhs.position) {
				expressions.push(new Position<>(position, unaryExpression.apply(rhs.element)));
				return success();
			}
			return missingRhsOperand(position, representation);
		});
	}

	static Operator binaryOperator(String representation, int precedence, Associativity associativity,
			BiFunction<Expression, Expression, Expression> binaryExpression) {
		return new Operator(representation, precedence, 2, associativity, (expressions, position) -> {
			Position<Expression> rhs = expressions.pop();
			Position<Expression> lhs = expressions.pop();
			if (lhs.position < position && position < rhs.position) {
				expressions.push(new Position<>(position, binaryExpression.apply(lhs.element, rhs.element)));
				return success();
			}
			if (position > rhs.position) {
				return missingRhsOperand(position, representation);
			}
			if (position < lhs.position) {
				return missingOperatorBetween(lhs, rhs);
			}
			return problemParsing(position, representation);
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

	ParseStatus createAndAddExpressionTo(Stack<Position<Expression>> expressions, int position) {
		if (expressions.size() < arity) {
			String message = createMissingOperandMessage(position, expressions);
			return ParseStatus.errorAt(position, representation, message);
		}
		return expressionCreator.createExpressionAndAddTo(expressions, position);
	}

	private String createMissingOperandMessage(int position, Stack<Position<Expression>> expressions) {
		if (1 == arity) {
			return missingOneOperand(associativity == Left ? "lhs" : "rhs");
		}

		if (2 == arity) {
			int mismatch = arity - expressions.size();
			if (2 == mismatch) {
				return "missing lhs and rhs operand";
			}
			return missingOneOperand(position < expressions.peek().position ? "lhs" : "rhs");
		}
		return "missing operand";
	}

	private String missingOneOperand(String side) {
		return "missing " + side + " operand";
	}

}
