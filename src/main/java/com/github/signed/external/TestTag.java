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

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable value object for a <em>tag</em> that is assigned to a test or
 * container.
 *
 * @since 1.0
 * @see #create(String)
 */
public final class TestTag implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String name;

	public static TestTag create(String name) {
		return new TestTag(name);
	}

	private TestTag(String name) {
		this.name = name.trim();
	}

	/**
	 * Get the name of this tag.
	 *
	 * @return the name of this tag; never {@code null} or blank
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TestTag) {
			TestTag that = (TestTag) obj;
			return Objects.equals(this.name, that.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public String toString() {
		return this.name;
	}

}
