package org.simpleflatmapper.util;

/**
 * use {@link CheckedConsumer} instead.
 */
@Deprecated
public interface RowHandler<T> extends CheckedConsumer<T> {
	// bridge method only available for java8
	//IFJAVA8_START
	default void handle(T t) throws Exception {
		accept(t);
	}
	//IFJAVA8_END
}
