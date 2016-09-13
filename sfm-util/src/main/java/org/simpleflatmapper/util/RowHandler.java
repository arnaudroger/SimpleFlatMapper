package org.simpleflatmapper.util;

/**
 * use {@link CheckedConsumer} instead.
 */
@Deprecated
public interface RowHandler<T> extends CheckedConsumer<T> {
	//IFJAVA8_START
	// bridge method only available for java8
	default void handle(T t) throws Exception {
		accept(t);
	}
	//IFJAVA8_END
}
