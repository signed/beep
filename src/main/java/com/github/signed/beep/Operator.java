package com.github.signed.beep;

import static com.github.signed.beep.Associativity.Left;
import static com.github.signed.beep.ExpressionCreator.report;

class Operator {

	static Operator nullaryOperator(String representation, int precedence) {
		return new Operator(representation, precedence, 0, null, (expressions, position) -> ExpressionCreator.success);
	}

	static Operator unaryOperator(String representation, int precedence, Associativity associativity,
			ExpressionCreator expressionCreator) {
		return new Operator(representation, precedence, 1, associativity, expressionCreator);
	}

	static Operator binaryOperator(String representation, int precedence, Associativity associativity,
			ExpressionCreator expressionCreator) {
		return new Operator(representation, precedence, 2, associativity, expressionCreator);
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

	String representation() {
		return representation;
	}

	boolean hasLowerPrecedenceThan(Operator operator) {
		return this.precedence < operator.precedence;
	}

	boolean represents(String token) {
		return representation.equals(token);
	}

	ParseStatus createAndAddExpressionTo(Stack<Position<Expression>> expressions, int position) {
		if (expressions.size() < arity) {
			String message = createMissingOperandMessage(position, expressions);
			return report(ParseStatus.Create(position, representation, message));
		}
		return expressionCreator.createExpressionAndAddTo(expressions, position);
	}

	boolean hasSamePrecedenceAs(Operator operator) {
		return this.precedence == operator.precedence;
	}

	boolean isLeftAssociative() {
		return Left == associativity;
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
