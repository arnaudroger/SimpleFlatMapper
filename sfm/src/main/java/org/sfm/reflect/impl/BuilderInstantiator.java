package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ErrorHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class BuilderInstantiator<S, T> implements Instantiator<S, T> {

	private final Instantiator<Void, ?> builderInstantiator;
	private final Tuple2<Method, Getter<? super S, ?>>[] arguments;
	private final Method buildMethod;

	public BuilderInstantiator(
			Instantiator<Void, ?> builderInstantiator,
			Tuple2<Method, Getter<? super S, ?>>[] arguments,
			Method buildMethod) {
		this.builderInstantiator = builderInstantiator;
		this.arguments = arguments;
		this.buildMethod = buildMethod;
	}


	@Override
	@SuppressWarnings("unchecked")
	public T newInstance(S s) throws Exception {
		try {
			Object builder = builderInstantiator.newInstance(null);
			for (Tuple2<Method, Getter<? super S, ?>> argument : arguments) {
				builder = argument.first().invoke(builder, argument.second().get(s));
			}
			return (T) buildMethod.invoke(builder);
		} catch (InvocationTargetException e) {
			return ErrorHelper.rethrow(e.getCause());
		}
	}
}
