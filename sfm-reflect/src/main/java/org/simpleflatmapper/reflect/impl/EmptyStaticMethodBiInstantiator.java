package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.util.ErrorHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class EmptyStaticMethodBiInstantiator<S1, S2, T> implements BiInstantiator<S1, S2, T> {

	private final Method method;
	private final Class<?> declaringClass;

	public EmptyStaticMethodBiInstantiator(final Method method) {
		this.method = method;
		this.declaringClass = method.getDeclaringClass();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T newInstance(S1 s1, S2 s2) throws Exception {
		try {
			return (T) method.invoke(declaringClass);
		} catch(InvocationTargetException e) {
			return ErrorHelper.rethrow(e.getCause());
		}
	}

    @Override
    public String toString() {
        return "EmptyStaticMethodBiInstantiator{}";
    }
}
