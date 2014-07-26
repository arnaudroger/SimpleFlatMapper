package org.sfm.reflect;

public interface Instantiator<T> {
	T newInstance() throws Exception;
}
