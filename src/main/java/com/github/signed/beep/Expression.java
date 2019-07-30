package com.github.signed.beep;

import java.util.Collection;

import com.github.signed.external.TestTag;

public interface Expression {
	boolean evaluate(Collection<TestTag> tags);
}
