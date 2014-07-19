package org.flatmap.reflect.primitive;


public interface IntGetter<T> {
	int getInt(T target)  throws Exception;
}
