package org.simpleflatmapper.map.setter;


import org.simpleflatmapper.converter.Context;

public interface ContextualIndexedSetter<T, P> {

	void set(T target, P value, int index, Context context) throws Exception;
}
