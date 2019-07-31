package com.github.signed.beep;

interface Stack<T> {

	void push(T t);

	T peek();

	T pop();

	boolean isEmpty();

	int size();

}
