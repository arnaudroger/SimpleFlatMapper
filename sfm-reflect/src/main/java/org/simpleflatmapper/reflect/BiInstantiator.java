package org.simpleflatmapper.reflect;

public interface BiInstantiator<S1, S2, T> {
	T newInstance(S1 s1, S2 s2) throws Exception;
}
