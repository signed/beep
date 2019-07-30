package com.github.signed.beep;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static com.github.signed.external.TestTag.create;
import static com.github.signed.beep.Expressions.and;
import static com.github.signed.beep.Expressions.not;
import static com.github.signed.beep.Expressions.or;
import static com.github.signed.beep.Expressions.tag;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.PreconditionViolationException;
import com.github.signed.external.TestTag;

class ExpressionsTests {

	private static final Expression True = tags -> true;
	private static final Expression False = tags -> false;

	@Test
	void tagIsJustATestTag() {
		assertThat(tag("foo")).hasToString("foo");
	}

	@Test
	void tagEvaluation() {
		Expression tagExpression = tag("foo");

		assertThat(tagExpression.evaluate(singleton(create("foo")))).isTrue();
		assertThat(tagExpression.evaluate(singleton(create("not_foo")))).isFalse();
	}

	@Test
	void justConcatenateNot() {
		assertThat(not(tag("foo"))).hasToString("!foo");
		assertThat(not(and(tag("foo"), tag("bar")))).hasToString("!(foo & bar)");
		assertThat(not(or(tag("foo"), tag("bar")))).hasToString("!(foo | bar)");
	}

	@Test
	void notEvaluation() {
		assertThat(not(True).evaluate(anyTestTags())).isFalse();
		assertThat(not(False).evaluate(anyTestTags())).isTrue();
	}

	@Test
	void encloseAndWithParenthesis() {
		assertThat(and(tag("foo"), tag("bar"))).hasToString("(foo & bar)");
	}

	@Test
	void andEvaluation() {
		assertThat(and(True, True).evaluate(anyTestTags())).isTrue();
		assertThat(and(True, False).evaluate(anyTestTags())).isFalse();
		assertThat(and(False, onEvaluateThrow("should not be evaluated")).evaluate(anyTestTags())).isFalse();
	}

	@Test
	void encloseOrWithParenthesis() {
		assertThat(or(tag("foo"), tag("bar"))).hasToString("(foo | bar)");
	}

	@Test
	void orEvaluation() {
		assertThat(or(False, False).evaluate(anyTestTags())).isFalse();
		assertThat(or(True, onEvaluateThrow("should not be evaluated")).evaluate(anyTestTags())).isTrue();
		assertThat(or(False, True).evaluate(anyTestTags())).isTrue();
	}

	private Expression onEvaluateThrow(String message) {
		return tags -> {
			throw new RuntimeException(message);
		};
	}

	private static Set<TestTag> anyTestTags() {
		return Collections.emptySet();
	}
}
