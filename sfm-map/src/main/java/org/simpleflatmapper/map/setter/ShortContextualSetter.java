package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.Context;

public interface ShortContextualSetter<T> {
	void setShort(T target, short value, Context context) throws Exception;
}
