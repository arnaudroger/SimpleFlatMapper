package org.simpleflatmapper.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link Consumer} that collect all the value into a set.<p>
 * Equivalent to a {@link java.util.stream.Collectors#toSet()}.
 * @param <T> the type of the callback argument
 */
public final class ImmutableSetCollector<T> implements Consumer<T> {

	private final Set<T> set = new HashSet<T>();
	
	@Override
	public void accept(final T t) {
		set.add(t);
	}

    /**
     *
     * @return the collected objects
     */
	public Set<T> getSet() {
		return Collections.unmodifiableSet(set);
	}
}
