package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.MethodGetterPair;
import org.simpleflatmapper.util.ErrorHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class BuilderInstantiator<S, T> implements Instantiator<S, T> {

	private final Instantiator<Void, ?> builderInstantiator;
	private final MethodGetterPair<S>[] chainedArguments;
	private final MethodGetterPair<S>[] unchainedArguments;
	private final Method buildMethod;
	private final boolean ignoreNullValues;

	public BuilderInstantiator(
			Instantiator<Void, ?> builderInstantiator,
			MethodGetterPair<S>[] chainedArguments,
			MethodGetterPair<S>[] unchainedArguments,
			Method buildMethod, boolean ignoreNullValues) {
		this.builderInstantiator = builderInstantiator;
		this.chainedArguments = chainedArguments;
		this.unchainedArguments = unchainedArguments;
		this.buildMethod = buildMethod;
		this.ignoreNullValues = ignoreNullValues;
	}


	@Override
	@SuppressWarnings("unchecked")
	public T newInstance(S s) throws Exception {
		try {
			Object builder = builderInstantiator.newInstance(null);
			for (MethodGetterPair<S> argument : chainedArguments) {
				Object v = argument.getGetter().get(s);
				if (!ignoreNullValues || v != null) {
					builder = argument.getMethod().invoke(builder, v);
				}
			}
			for (MethodGetterPair<S> argument : unchainedArguments) {
				Object v = argument.getGetter().get(s);
				if (!ignoreNullValues || v != null) {
					argument.getMethod().invoke(builder, v);
				}
			}
			return (T) buildMethod.invoke(builder);
		} catch (InvocationTargetException e) {
			return ErrorHelper.rethrow(e.getCause());
		}
	}
}
