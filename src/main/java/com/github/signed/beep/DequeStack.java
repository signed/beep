package com.github.signed.beep;

import java.util.ArrayDeque;
import java.util.Deque;

class DequeStack<T> implements Stack<T> {

	private final Deque<T> deque = new ArrayDeque<>();

	@Override
	public void push(T t) {
		deque.addFirst(t);
	}

	@Override
	public T peek() {
		return deque.peek();
	}

	@Override
	public T pop() {
		return deque.pollFirst();
	}

	@Override
	public boolean isEmpty() {
		return deque.isEmpty();
	}

	@Override
	public int size() {
		return deque.size();
	}
}
