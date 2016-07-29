package org.sfm.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Use {@link ListCollectorHandler} instead.
 * Implementation of {@link org.sfm.utils.RowHandler} that collect all the value into a list.<p>
 * Equivalent to a {@link java.util.stream.Collectors#toList()}.
 * @param <T> the type of the callback argument
 */
@Deprecated
public final class ListHandler<T> implements RowHandler<T> {

	private final List<T> list = new ArrayList<T>();
	
	@Override
	public void handle(final T t) {
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
