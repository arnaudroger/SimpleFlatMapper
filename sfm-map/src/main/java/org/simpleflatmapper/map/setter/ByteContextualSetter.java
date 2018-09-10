package org.simpleflatmapper.map.setter;

import org.simpleflatmapper.converter.Context;

public interface ByteContextualSetter<T> {
	void setByte(T target, byte value, Context context) throws Exception;
}
