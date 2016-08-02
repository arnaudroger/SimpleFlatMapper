package org.simpleflatmapper.core.reflect.impl;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.Instantiator;
import org.simpleflatmapper.core.tuples.Tuple2;
import org.simpleflatmapper.core.utils.ErrorHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class BuilderInstantiator<S, T> implements Instantiator<S, T> {

	private final Instantiator<Void, ?> builderInstantiator;
	private final Tuple2<Method, Getter<? super S, ?>>[] chainedArguments;
	private final Tuple2<Method, Getter<? super S, ?>>[] unchainedArguments;
	private final Method buildMethod;

	public BuilderInstantiator(
			Instantiator<Void, ?> builderInstantiator,
			Tuple2<Method, Getter<? super S, ?>>[] chainedArguments,
			Tuple2<Method, Getter<? super S, ?>>[] unchainedArguments,
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
			for (Tuple2<Method, Getter<? super S, ?>> argument : chainedArguments) {
				builder = argument.first().invoke(builder, argument.second().get(s));
			}
			for (Tuple2<Method, Getter<? super S, ?>> argument : unchainedArguments) {
				argument.first().invoke(builder, argument.second().get(s));
			}
			return (T) buildMethod.invoke(builder);
		} catch (InvocationTargetException e) {
			return ErrorHelper.rethrow(e.getCause());
		}
	}
}
