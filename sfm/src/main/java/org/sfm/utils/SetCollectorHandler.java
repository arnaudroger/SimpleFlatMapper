package org.sfm.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link RowHandler} that collect all the value into a set.<p>
 * Equivalent to a {@link java.util.stream.Collectors#toSet()}.
 * @param <T> the type of the callback argument
 */
public final class SetCollectorHandler<T> implements RowHandler<T> {

	private final Set<T> set = new HashSet<T>();
	
	@Override
	public void handle(final T t) {
		set.add(t);
	}

    /**
     *
     * @return the collected objects
     */
	public Set<T> getSet() {
		return set;
	}
}
