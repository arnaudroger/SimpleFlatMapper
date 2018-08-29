package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.Context;

public interface FloatContextualSetter<T> {
	void setFloat(T target, float value, Context context) throws Exception;
}
