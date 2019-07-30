package com.github.signed.beep;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static com.github.signed.beep.Operator.Associativity.Left;
import static com.github.signed.beep.Operator.Associativity.Right;

import java.util.Map;
import java.util.stream.Stream;

class Operators {

	private static final Operator Not = Operator.unaryOperator("!", 3, Right, TagExpressions::not);
	private static final Operator And = Operator.binaryOperator("&", 2, Left, TagExpressions::and);
	private static final Operator Or = Operator.binaryOperator("|", 1, Left, TagExpressions::or);

	private final Map<String, Operator> representationToOperator = Stream.of(Not, And, Or).collect(
		toMap(Operator::representation, identity()));

	boolean isOperator(String token) {
		return representationToOperator.containsKey(token);
	}

	Operator operatorFor(String token) {
		return representationToOperator.get(token);
	}

}
