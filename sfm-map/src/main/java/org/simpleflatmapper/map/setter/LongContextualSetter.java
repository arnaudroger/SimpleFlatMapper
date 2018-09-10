package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.Context;

public interface LongContextualSetter<T> {
	void setLong(T target, long value, Context context) throws Exception;
}
