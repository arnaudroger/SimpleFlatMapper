package org.simpleflatmapper.core.reflect;

public interface Instantiator<S, T> {
	T newInstance(S s) throws Exception;
}
