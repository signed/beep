package com.github.signed.beep;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class Tokenizer {

	List<String> tokenize(String infixTagExpression) {
		if (null == infixTagExpression) {
			return Collections.emptyList();
		}
		String[] parts = infixTagExpression.replaceAll("\\(", " ( ").replaceAll("\\)", " ) ").split("\\s");
		return Arrays.stream(parts).filter(part -> !part.isEmpty()).collect(Collectors.toList());
	}

}
