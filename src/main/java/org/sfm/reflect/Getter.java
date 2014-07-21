package org.sfm.reflect;

public interface Getter<T, P> {
	P get(T target) throws Exception;
}
