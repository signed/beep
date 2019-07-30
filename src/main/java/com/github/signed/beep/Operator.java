package com.github.signed.beep;

import static com.github.signed.beep.Associativity.Left;

import java.util.function.Consumer;

class Operator {

	static Operator nullaryOperator(String representation, int precedence) {
		return new Operator(representation, precedence, 0, null, expressionStack -> {
		});
	}

	static Operator unaryOperator(String representation, int precedence, Associativity associativity,
			Consumer<Stack<Expression>> expressionCreator) {
		return new Operator(representation, precedence, 1, associativity, expressionCreator);
	}

	static Operator binaryOperator(String representation, int precedence, Associativity associativity,
			Consumer<Stack<Expression>> expressionCreator) {
		return new Operator(representation, precedence, 2, associativity, expressionCreator);
	}

	private final String representation;
	private final int precedence;
	private final int arity;
	private final Associativity associativity;
	private final Consumer<Stack<Expression>> expressionCreator;

	private Operator(String representation, int precedence, int arity, Associativity associativity,
			Consumer<Stack<Expression>> expressionCreator) {
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

	boolean createAndAddExpressionTo(Stack<Expression> expressions) {
		if (expressions.size() < arity) {
			return false;
		}
		expressionCreator.accept(expressions);
		return true;
	}

	boolean hasSamePrecedenceAs(Operator operator) {
		return this.precedence == operator.precedence;
	}

	boolean isLeftAssociative() {
		return Left == associativity;
	}
}
