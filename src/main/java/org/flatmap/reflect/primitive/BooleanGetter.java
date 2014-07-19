package org.flatmap.reflect.primitive;


public interface BooleanGetter<T> {
	boolean getBoolean(T target)  throws Exception;
}
