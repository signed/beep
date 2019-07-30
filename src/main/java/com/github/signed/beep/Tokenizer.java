package com.github.signed.beep;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Tokenizer {

	List<Token> tokenize(String infixTagExpression) {
		if (null == infixTagExpression) {
			return emptyList();
		}

		return deriveTokensFrom(infixTagExpression, trimmedTokenStringsFrom(infixTagExpression));
	}

	private List<String> trimmedTokenStringsFrom(String infixTagExpression) {
		String[] parts = infixTagExpression.replaceAll("([()!|&])", " $1 ").split("\\s");
		return stream(parts).filter(part -> !part.isEmpty()).collect(Collectors.toList());
	}

	private List<Token> deriveTokensFrom(String infixTagExpression, List<String> trimmedTokens) {
		int startIndex = 0;
		List<Token> tokens = new ArrayList<>(trimmedTokens.size());
		for (String trimmedToken : trimmedTokens) {
			Token token = extractTokenStartingAt(infixTagExpression, startIndex, trimmedToken);
			startIndex = token.endIndexExclusive();
			tokens.add(token);
		}
		return tokens;
	}

	private Token extractTokenStartingAt(String infixTagExpression, int startIndex, String trimmedToken) {
		int foundAt = infixTagExpression.indexOf(trimmedToken, startIndex);
		int endIndex = foundAt + trimmedToken.length();
		String rawToken = infixTagExpression.substring(startIndex, endIndex);
		return new Token(startIndex, rawToken);
	}

}
