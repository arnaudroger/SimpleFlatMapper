package org.simpleflatmapper.core.reflect.primitive;


public interface BooleanGetter<T> {
	boolean getBoolean(T target)  throws Exception;
}
