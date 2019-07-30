/*
 * Copyright 2015-2017 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package com.github.signed.external;

import java.util.function.Supplier;

/**
 * Collection of utilities for asserting preconditions for method and
 * constructor arguments.
 *
 * <p>Each method in this class throws a {@link PreconditionViolationException}
 * if the precondition is violated.
 *
 * <h3>DISCLAIMER</h3>
 *
 * <p>These utilities are intended solely for usage within the JUnit framework
 * itself. <strong>Any usage by external parties is not supported.</strong>
 * Use at your own risk!
 *
 * @since 1.0
 */
final class Preconditions {

	///CLOVER:OFF
	private Preconditions() {
		/* no-op */
	}
	///CLOVER:ON

	/**
	 * Assert that the supplied {@code predicate} is {@code true}.
	 *
	 * @param predicate the predicate to check
	 * @param messageSupplier precondition violation message supplier
	 * @throws PreconditionViolationException if the predicate is {@code false}
	 */
	static void condition(boolean predicate, Supplier<String> messageSupplier)
			throws PreconditionViolationException {

		if (!predicate) {
			throw new PreconditionViolationException(messageSupplier.get());
		}
	}

}
