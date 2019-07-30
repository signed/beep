package com.github.signed.beep;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.stream.Collectors;

class Tokenizer {

	List<String> tokenize(String infixTagExpression) {
		if (null == infixTagExpression) {
			return emptyList();
		}
		String[] parts = infixTagExpression.replaceAll("([()!|&])", " $1 ").split("\\s");
		return stream(parts).filter(part -> !part.isEmpty()).collect(Collectors.toList());
	}

}
