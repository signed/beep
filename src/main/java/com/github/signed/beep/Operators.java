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

	private static final Operator Not = Operator.unaryOperator("!", 3, Right, (expressions, position) -> {
		Position<Expression> rhs = expressions.pop();
		if (position < rhs.position) {
			Expression not = not(rhs.element);
			expressions.push(new Position<>(position, not));
			return ParseStatus.success();
		}
		return ParseStatus.Create(position, "!", "missing rhs operand");
	});

	private static final Operator And = Operator.binaryOperator("&", 2, Left, (expressions, position) -> {
		Position<Expression> rhs = expressions.pop();
		Position<Expression> lhs = expressions.pop();
		if (lhs.position < position && position < rhs.position) {
			expressions.push(new Position<>(position, and(lhs.element, rhs.element)));
			return ParseStatus.success();
		}

		if (position > rhs.position) {
			return ParseStatus.Create(position, "&", "missing rhs operand");
		}
		if (position < lhs.position) {
			return ParseStatus.missingOperatorBetween(lhs.position, lhs.element.toString(), rhs.position,
				rhs.element.toString());
		}
		return ParseStatus.problemParsing(position, "&");
	});

	private static final Operator Or = Operator.binaryOperator("|", 1, Left, (expressions, position) -> {
		Position<Expression> rhs = expressions.pop();
		Position<Expression> lhs = expressions.pop();
		if (lhs.position < position && position < rhs.position) {
			expressions.push(new Position<>(position, or(lhs.element, rhs.element)));
			return ParseStatus.success();
		}
		if (position > rhs.position) {
			return ParseStatus.Create(position, "|", "missing rhs operand");
		}
		if (position < lhs.position) {
			return ParseStatus.missingOperatorBetween(lhs.position, lhs.element.toString(), rhs.position,
				rhs.element.toString());
		}
		return ParseStatus.problemParsing(position, "|");
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
