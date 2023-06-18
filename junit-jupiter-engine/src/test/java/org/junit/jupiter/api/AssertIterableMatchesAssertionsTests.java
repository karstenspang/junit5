/*
 * Copyright 2015-2023 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.jupiter.api;

import static org.junit.jupiter.api.AssertionTestUtils.assertMessageEquals;
import static org.junit.jupiter.api.Assertions.assertIterableMatches;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.junit.platform.commons.PreconditionViolationException;
import org.opentest4j.AssertionFailedError;

/**
 * Unit tests for JUnit Jupiter {@link Assertions}.
 *
 * @since 5.0
 */
class AssertIterableMatchesAssertionsTests {

	@Test
	void assertIterableMatchesNullPredicate() {
		List<String> expected = Arrays.asList("x");
		Set<String> actual = new TreeSet<>(Arrays.asList("y"));
		PreconditionViolationException ex = assertThrows(PreconditionViolationException.class,
			() -> assertIterableMatches(null, expected, actual));
		assertMessageEquals(ex, "predicate must not be null");
	}

	@Test
	void assertIterableMatchesNullExpectedAndActual() {
		assertIterableMatches(Objects::equals, null, null);
		assertIterableMatches(Objects::equals, null, null, "message");
		assertIterableMatches(Objects::equals, null, null, () -> "message");
	}

	@Test
	void assertIterableMatchesNullActual() {
		List<String> expected = Arrays.asList("x");
		AssertionFailedError ex = assertThrows(AssertionFailedError.class,
			() -> assertIterableMatches(Objects::equals, expected, null));
		assertMessageEquals(ex, "actual iterable was <null>");
	}

	@Test
	void assertIterableMatchesNullExpected() {
		Set<String> actual = new TreeSet<>(Arrays.asList("y"));
		AssertionFailedError ex = assertThrows(AssertionFailedError.class,
			() -> assertIterableMatches(Objects::equals, null, actual));
		assertMessageEquals(ex, "expected iterable was <null>");
	}

	@Test
	void assertIterableMatchesMatching() {
		List<String> expected = Arrays.asList("x");
		Set<String> actual = new TreeSet<>(Arrays.asList("x"));
		assertIterableMatches(Objects::equals, expected, actual);
	}

	@Test
	void assertIterableMatchesNotMatching() {
		List<String> expected = Arrays.asList("x");
		Set<String> actual = new TreeSet<>(Arrays.asList("y"));
		AssertionFailedError ex = assertThrows(AssertionFailedError.class,
			() -> assertIterableMatches(Objects::equals, expected, actual));
		assertMessageEquals(ex, "iterable contents do not match at index 0, expected: <x> but was: <y>");
	}

	@Test
	void assertIterableMatchesActualShort() {
		List<String> expected = Arrays.asList("x");
		Set<String> actual = new TreeSet<>(Arrays.asList());
		AssertionFailedError ex = assertThrows(AssertionFailedError.class,
			() -> assertIterableMatches(Objects::equals, expected, actual));
		assertMessageEquals(ex, "iterable lengths differ, expected: <1> but was: <0>");
	}

	@Test
	void assertIterableMatchesExpectedShort() {
		List<String> expected = Arrays.asList();
		Set<String> actual = new TreeSet<>(Arrays.asList("y"));
		AssertionFailedError ex = assertThrows(AssertionFailedError.class,
			() -> assertIterableMatches(Objects::equals, expected, actual));
		assertMessageEquals(ex, "iterable lengths differ, expected: <0> but was: <1>");
	}

	@Test
	void assertIterableMatchesUserSuppliedPredicate() {
		List<Integer> i0 = Arrays.asList(1, 2, 3);
		List<Integer> i1 = Arrays.asList(2, 3, 4);
		assertIterableMatches((e, a) -> a.intValue() > e.intValue(), i0, i1);
	}

}
