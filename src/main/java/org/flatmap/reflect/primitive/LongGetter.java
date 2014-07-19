package org.flatmap.reflect.primitive;


public interface LongGetter<T> {
	long getLong(T target)  throws Exception;
}
