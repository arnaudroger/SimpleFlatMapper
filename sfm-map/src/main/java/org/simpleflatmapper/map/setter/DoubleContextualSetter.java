package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.Context;

public interface DoubleContextualSetter<T> {
	void setDouble(T target, double value, Context context) throws Exception;
}
