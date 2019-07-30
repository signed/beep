package com.github.signed.beep;

interface ExpressionCreator {
	ParseStatus createExpressionAndAddTo(Stack<Position<Expression>> expressions, int position);
}
