package org.sfm.reflect;

import java.lang.reflect.Type;

public interface Setter<T, P> {
	void set(T target, P value) throws Exception;
	Type getPropertyType();
}
