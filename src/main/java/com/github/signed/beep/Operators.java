package com.github.signed.beep;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static com.github.signed.beep.Associativity.Left;
import static com.github.signed.beep.Associativity.Right;
import static com.github.signed.beep.Expressions.and;
import static com.github.signed.beep.Expressions.not;
import static com.github.signed.beep.Expressions.or;

import java.util.Map;
import java.util.stream.Stream;

class Operators {

	private static final Operator Not = Operator.unaryOperator("not", 3, Right,
		expressionStack -> expressionStack.push(not(expressionStack.pop())));

	private static final Operator And = Operator.binaryOperator("and", 2, Left, expressionStack -> {
		Expression rhs = expressionStack.pop();
		Expression lhs = expressionStack.pop();
		expressionStack.push(and(lhs, rhs));
	});

	private static final Operator Or = Operator.binaryOperator("or", 1, Left, expressionStack -> {
		Expression rhs = expressionStack.pop();
		Expression lhs = expressionStack.pop();
		expressionStack.push(or(lhs, rhs));
	});

	private final Map<String, Operator> representationToOperator = Stream.of(Not, And, Or).collect(
		toMap(Operator::representation, identity()));

	boolean isOperator(String token) {
		return representationToOperator.containsKey(token);
	}

	Operator operatorFor(String token) {
		return representationToOperator.get(token);
	}

}
