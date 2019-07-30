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

/**
 * Collection of utilities for working with {@link String Strings},
 * {@link CharSequence CharSequences}, etc.
 *
 * <h3>DISCLAIMER</h3>
 *
 * <p>These utilities are intended solely for usage within the JUnit framework
 * itself. <strong>Any usage by external parties is not supported.</strong>
 * Use at your own risk!
 *
 * @since 1.0
 */
final class StringUtils {

    ///CLOVER:OFF
    private StringUtils() {
        /* no-op */
    }
    ///CLOVER:ON

    /**
     * Determine if the supplied {@link String} contains any whitespace characters.
     *
     * @param str the string to check; may be {@code null}
     * @return {@code true} if the string contains whitespace
     * @see #containsIsoControlCharacter(String)
     * @see Character#isWhitespace(int)
     */
    private static boolean containsWhitespace(String str) {
        return str != null && str.codePoints().anyMatch(Character::isWhitespace);
    }

    /**
     * Determine if the supplied {@link String} does not contain any whitespace
     * characters.
     *
     * @param str the string to check; may be {@code null}
     * @return {@code true} if the string does not contain whitespace
     * @see #containsWhitespace(String)
     * @see #containsIsoControlCharacter(String)
     * @see Character#isWhitespace(int)
     */
    static boolean doesNotContainWhitespace(String str) {
        return !containsWhitespace(str);
    }

    /**
     * Determine if the supplied {@link String} contains any ISO control characters.
     *
     * @param str the string to check; may be {@code null}
     * @return {@code true} if the string contains an ISO control character
     * @see #containsWhitespace(String)
     * @see Character#isISOControl(int)
     */
    private static boolean containsIsoControlCharacter(String str) {
        return str != null && str.codePoints().anyMatch(Character::isISOControl);
    }

    /**
     * Determine if the supplied {@link String} does not contain any ISO control
     * characters.
     *
     * @param str the string to check; may be {@code null}
     * @return {@code true} if the string does not contain an ISO control character
     * @see #containsIsoControlCharacter(String)
     * @see #containsWhitespace(String)
     * @see Character#isISOControl(int)
     */
    static boolean doesNotContainIsoControlCharacter(String str) {
        return !containsIsoControlCharacter(str);
    }

}
