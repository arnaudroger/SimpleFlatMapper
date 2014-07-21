package org.sfm.reflect;

public interface Setter<T, P> {
	void set(T target, P value) throws Exception;
	Class<? extends P> getPropertyType();
}
