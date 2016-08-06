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

	public BuilderInstantiator(
			Instantiator<Void, ?> builderInstantiator,
			MethodGetterPair<S>[] chainedArguments,
			MethodGetterPair<S>[] unchainedArguments,
			Method buildMethod) {
		this.builderInstantiator = builderInstantiator;
		this.chainedArguments = chainedArguments;
		this.unchainedArguments = unchainedArguments;
		this.buildMethod = buildMethod;
	}


	@Override
	@SuppressWarnings("unchecked")
	public T newInstance(S s) throws Exception {
		try {
			Object builder = builderInstantiator.newInstance(null);
			for (MethodGetterPair<S> argument : chainedArguments) {
				builder = argument.getMethod().invoke(builder, argument.getGetter().get(s));
			}
			for (MethodGetterPair<S> argument : unchainedArguments) {
				argument.getMethod().invoke(builder, argument.getGetter().get(s));
			}
			return (T) buildMethod.invoke(builder);
		} catch (InvocationTargetException e) {
			return ErrorHelper.rethrow(e.getCause());
		}
	}
}
