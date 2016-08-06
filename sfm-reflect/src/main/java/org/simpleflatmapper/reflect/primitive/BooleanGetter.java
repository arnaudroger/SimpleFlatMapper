package org.simpleflatmapper.reflect.primitive;


public interface BooleanGetter<T> {
	boolean getBoolean(T target)  throws Exception;
}
