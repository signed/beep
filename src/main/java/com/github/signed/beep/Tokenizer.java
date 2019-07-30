package com.github.signed.beep;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class Tokenizer {

	static class Token{

		final int position;
		final String string;
		final String rawString;
		final int start;

		Token(int position, String string, String rawToken, int startIndex) {
			this.position = position;
			this.string = string;
			rawString = rawToken;
			start = startIndex;
		}
	}

	List<Token> tokenize(String infixTagExpression) {
		if (null == infixTagExpression) {
			return emptyList();
		}

		return deriveTokensFrom(infixTagExpression, trimmedTokenStringsFrom(infixTagExpression));
	}

	private List<Token> deriveTokensFrom(String infixTagExpression, List<String> actualTokens) {
		int startIndex = 0;
		List<Token> tokens = new ArrayList<>(actualTokens.size());
		for(int position = 0; position < actualTokens.size(); ++position) {
			String token = actualTokens.get(position);
			int foundAt = infixTagExpression.indexOf(token, startIndex);
			int endIndex = foundAt + token.length();
			String rawToken = infixTagExpression.substring(startIndex, endIndex);
			tokens.add(new Token(position, token, rawToken, startIndex));
			startIndex = endIndex;
		}
		return tokens;
	}

	private List<String> trimmedTokenStringsFrom(String infixTagExpression) {
		String[] parts = infixTagExpression.replaceAll("([()!|&])", " $1 ").split("\\s");
		return stream(parts).filter(part -> !part.isEmpty()).collect(Collectors.toList());
	}

}
