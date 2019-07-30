package com.github.signed.beep;

import java.util.Collection;

import com.github.signed.external.TestTag;

class TagExpressions {

	static TagExpression tag(String tag) {
		TestTag testTag = TestTag.create(tag);
		return new TagExpression() {
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

	static TagExpression not(TagExpression toNegate) {
		return new TagExpression() {
			@Override
			public boolean evaluate(Collection<TestTag> tags) {
				return !toNegate.evaluate(tags);
			}

			@Override
			public String toString() {
				return "!" + toNegate + "";
			}
		};
	}

	static TagExpression and(TagExpression lhs, TagExpression rhs) {
		return new TagExpression() {
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

	static TagExpression or(TagExpression lhs, TagExpression rhs) {
		return new TagExpression() {
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
