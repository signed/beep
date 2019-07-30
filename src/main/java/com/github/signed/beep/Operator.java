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
		ParseStatus createExpressionAndAddTo(Stack<TokenWith<Expression>> expressions, Token operatorToken);
	}

	static Operator nullaryOperator(String representation, int precedence) {
		return new Operator(representation, precedence, 0, null, (expressions, operatorToken) -> success());
	}

	static Operator unaryOperator(String representation, int precedence, Associativity associativity,
			Function<Expression, Expression> unaryExpression) {
		return new Operator(representation, precedence, 1, associativity, (expressions, operatorToken) -> {
			TokenWith<Expression> rhs = expressions.pop();
			if (operatorToken.rightMostPosition() < rhs.token.leftMostPosition()) {
				expressions.push(
					new TokenWith<>(operatorToken.concatenate(rhs.token), unaryExpression.apply(rhs.element)));
				return success();
			}
			return missingRhsOperand(operatorToken, representation);
		});
	}

	static Operator binaryOperator(String representation, int precedence, Associativity associativity,
			BiFunction<Expression, Expression, Expression> binaryExpression) {
		return new Operator(representation, precedence, 2, associativity, (expressions, operatorToken) -> {
			TokenWith<Expression> rhs = expressions.pop();
			TokenWith<Expression> lhs = expressions.pop();
			if (lhs.token.rightMostPosition() < operatorToken.leftMostPosition()
					&& operatorToken.rightMostPosition() < rhs.token.leftMostPosition()) {
				Token combinedToken = lhs.token.concatenate(operatorToken).concatenate(rhs.token);
				expressions.push(new TokenWith<>(combinedToken, binaryExpression.apply(lhs.element, rhs.element)));
				return success();
			}
			if (rhs.token.rightMostPosition() < operatorToken.leftMostPosition()) {
				return missingRhsOperand(operatorToken, representation);
			}
			if (operatorToken.leftMostPosition() < lhs.token.rightMostPosition()) {
				return missingOperatorBetween(lhs, rhs);
			}
			return problemParsing(operatorToken, representation);
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

	ParseStatus createAndAddExpressionTo(Stack<TokenWith<Expression>> expressions, Token operatorToken) {
		if (expressions.size() < arity) {
			String message = createMissingOperandMessage(expressions, operatorToken);
			return ParseStatus.errorAt(operatorToken, representation, message);
		}
		return expressionCreator.createExpressionAndAddTo(expressions, operatorToken);
	}

	private String createMissingOperandMessage(Stack<TokenWith<Expression>> expressions, Token operatorToken) {
		if (1 == arity) {
			return missingOneOperand(associativity == Left ? "lhs" : "rhs");
		}

		if (2 == arity) {
			int mismatch = arity - expressions.size();
			if (2 == mismatch) {
				return "missing lhs and rhs operand";
			}
			return missingOneOperand(
				operatorToken.rightMostPosition() < expressions.peek().token.leftMostPosition() ? "lhs" : "rhs");
		}
		return "missing operand";
	}

	private String missingOneOperand(String side) {
		return "missing " + side + " operand";
	}

}
