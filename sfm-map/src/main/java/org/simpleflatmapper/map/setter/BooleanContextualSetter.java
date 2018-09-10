package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.Context;

public interface BooleanContextualSetter<T> {
	void setBoolean(T target, boolean value, Context context) throws Exception;
}
