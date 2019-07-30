package com.github.signed.beep;


import java.util.Collection;

import com.github.signed.external.TestTag;

/**
 * An expression can be evaluated against a collection of {@link TestTag test tags} to decide if they match the expression.
 *
 * @since 1.1
 */
public interface Expression {
	boolean evaluate(Collection<TestTag> tags);
}
