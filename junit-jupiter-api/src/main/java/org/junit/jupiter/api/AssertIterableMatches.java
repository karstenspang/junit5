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

import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;
import static org.junit.platform.commons.util.Preconditions.notNull;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * {@code AssertMatches} is a collection of utility methods that support asserting
 * Iterable match in tests.
 *
 * @since 5.10
 */
class AssertIterableMatches {

	private AssertIterableMatches() {
		/* no-op */
	}

	static <E, A> void assertIterableMatches(BiPredicate<? super E, ? super A> predicate, Iterable<E> expected,
			Iterable<A> actual) {
		assertIterableMatchesPrivate(predicate, expected, actual, null);
	}

	static <E, A> void assertIterableMatches(BiPredicate<? super E, ? super A> predicate, Iterable<E> expected,
			Iterable<A> actual, String message) {
		assertIterableMatchesPrivate(predicate, expected, actual, message);
	}

	static <E, A> void assertIterableMatches(BiPredicate<? super E, ? super A> predicate, Iterable<E> expected,
			Iterable<A> actual, Supplier<String> messageSupplier) {
		assertIterableMatchesPrivate(predicate, expected, actual, messageSupplier);
	}

	private static <E, A> void assertIterableMatchesPrivate(BiPredicate<? super E, ? super A> predicate,
			Iterable<E> expected, Iterable<A> actual, Object messageOrSupplier) {

		notNull(predicate, "predicate must not be null");

		if (expected == null && actual == null)
			return;
		assertIterablesNotNull(expected, actual, messageOrSupplier);

		Iterator<E> expectedIterator = expected.iterator();
		Iterator<A> actualIterator = actual.iterator();

		int processed = 0;
		while (expectedIterator.hasNext() && actualIterator.hasNext()) {
			E expectedElement = expectedIterator.next();
			A actualElement = actualIterator.next();

			if (!predicate.test(expectedElement, actualElement)) {
				failIterablesNotMatching(expectedElement, actualElement, processed, messageOrSupplier);
			}

			processed++;
		}

		assertIteratorsAreEmpty(expectedIterator, actualIterator, processed, messageOrSupplier);
	}

	private static void assertIterablesNotNull(Object expected, Object actual, Object messageOrSupplier) {

		if (expected == null) {
			failExpectedIterableIsNull(messageOrSupplier);
		}
		if (actual == null) {
			failActualIterableIsNull(messageOrSupplier);
		}
	}

	private static void failExpectedIterableIsNull(Object messageOrSupplier) {
		assertionFailure() //
				.message(messageOrSupplier) //
				.reason("expected iterable was <null>") //
				.buildAndThrow();
	}

	private static void failActualIterableIsNull(Object messageOrSupplier) {
		assertionFailure() //
				.message(messageOrSupplier) //
				.reason("actual iterable was <null>") //
				.buildAndThrow();
	}

	private static void assertIteratorsAreEmpty(Iterator<?> expected, Iterator<?> actual, int processed,
			Object messageOrSupplier) {

		if (expected.hasNext() || actual.hasNext()) {
			AtomicInteger expectedCount = new AtomicInteger(processed);
			expected.forEachRemaining(e -> expectedCount.incrementAndGet());

			AtomicInteger actualCount = new AtomicInteger(processed);
			actual.forEachRemaining(e -> actualCount.incrementAndGet());

			assertionFailure() //
					.message(messageOrSupplier) //
					.reason("iterable lengths differ") //
					.expected(expectedCount.get()) //
					.actual(actualCount.get()) //
					.buildAndThrow();
		}
	}

	private static void failIterablesNotMatching(Object expected, Object actual, int index, Object messageOrSupplier) {

		assertionFailure() //
				.message(messageOrSupplier) //
				.reason("iterable contents do not match at index " + index) //
				.expected(expected) //
				.actual(actual) //
				.buildAndThrow();
	}

}
