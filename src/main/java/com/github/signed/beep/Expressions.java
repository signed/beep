package com.github.signed.beep;

import java.util.Collection;

import com.github.signed.external.TestTag;

class Expressions {

	static Expression tag(String tag) {
		TestTag testTag = TestTag.create(tag);
		return new Expression() {
			@Override
			public boolean evaluate(Collection<TestTag> tags) {
				return tags.contains(testTag);
			}

			@Override
			public String toString() {
				return testTag.getName();
			}
		};
	}

	static Expression not(Expression toNegate) {
		return new Expression() {
			@Override
			public boolean evaluate(Collection<TestTag> tags) {
				return !toNegate.evaluate(tags);
			}

			@Override
			public String toString() {
				return "!(" + toNegate + ")";
			}
		};
	}

	static Expression and(Expression lhs, Expression rhs) {
		return new Expression() {
			@Override
			public boolean evaluate(Collection<TestTag> tags) {
				return lhs.evaluate(tags) && rhs.evaluate(tags);
			}

			@Override
			public String toString() {
				return "(" + lhs + " & " + rhs + ")";
			}
		};
	}

	static Expression or(Expression lhs, Expression rhs) {
		return new Expression() {
			@Override
			public boolean evaluate(Collection<TestTag> tags) {
				return lhs.evaluate(tags) || rhs.evaluate(tags);
			}

			@Override
			public String toString() {
				return "(" + lhs + " | " + rhs + ")";
			}
		};
	}
}
