package com.github.signed.beep;

interface ExpressionCreator {
    boolean accept(Stack<Position<Expression>> expressions, int position);
}
