package com.github.signed.beep;

interface ExpressionCreator {
	ParseStatus success = ParseStatus.success();

	static ParseStatus report(ParseStatus error) {
		return error;
	}

	ParseStatus createExpressionAndAddTo(Stack<Position<Expression>> expressions, int position);
}
