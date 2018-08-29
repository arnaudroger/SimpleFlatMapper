package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.Context;

public interface IntContextualSetter<T> {
	void setInt(T target, int value, Context context) throws Exception;
}
