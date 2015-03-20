package org.sfm.utils;

/**
 * Functional interface use to provide call back when the mapper is getting new value.<p>
 * It is equivalent to a {@link java.util.function.Consumer} apart that it allows for checked Exception.
 *
 * @param <T> the type of the call back argument
 */
public interface RowHandler<T> {
	void handle(T t) throws Exception;
}
