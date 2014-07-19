package org.flatmap.reflect;

public interface Getter<T, P> {
	P get(T target) throws Exception;
}
