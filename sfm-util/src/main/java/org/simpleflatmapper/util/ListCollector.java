package org.simpleflatmapper.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link Consumer} that collect all the value into a list.<p>
 * Equivalent to a {@link java.util.stream.Collectors#toList()}.
 * @param <T> the type of the callback argument
 */
public final class ListCollector<T> implements Consumer<T> {

	private final List<T> list = new ArrayList<T>();
	
	@Override
	public void accept(final T t) {
		list.add(t);
	}

    /**
     *
     * @return the collected objects
     */
	public List<T> getList() {
		return list;
	}
}
