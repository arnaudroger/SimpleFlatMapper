package org.simpleflatmapper.reflect;

import org.simpleflatmapper.util.ErrorHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class BuilderBiInstantiator<S1, S2, T> implements BiInstantiator<S1, S2, T> {

	public final Instantiator<Void, ?> builderInstantiator;
	public final MethodBiFunctionPair<S1, S2>[] chainedArguments;
	public final MethodBiFunctionPair<S1, S2>[] unchainedArguments;
	public final Method buildMethod;
	public final boolean ignoreNullValues;
	private boolean notStatic;

	public BuilderBiInstantiator(
			Instantiator<Void, ?> builderInstantiator,
			MethodBiFunctionPair<S1, S2>[] chainedArguments,
			MethodBiFunctionPair<S1, S2>[] unchainedArguments,
			Method buildMethod, 
			boolean ignoreNullValues) {
		this.builderInstantiator = builderInstantiator;
		this.chainedArguments = chainedArguments;
		this.unchainedArguments = unchainedArguments;
		this.buildMethod = buildMethod;
		this.ignoreNullValues = ignoreNullValues;
		this.notStatic = !Modifier.isStatic(buildMethod.getModifiers());

	}


	@Override
	@SuppressWarnings("unchecked")
	public T newInstance(S1 s1, S2 s2) throws Exception {
		try {
			Object builder = newInitialisedBuilderInstace(s1, s2);
			if (notStatic) {
				return (T) buildMethod.invoke(builder);
			} else {
				return (T) buildMethod.invoke(null, builder);
			}
		} catch (InvocationTargetException e) {
			return ErrorHelper.rethrow(e.getCause());
		}
	}

	public Object newInitialisedBuilderInstace(S1 s1, S2 s2) throws Exception {
		Object builder = builderInstantiator.newInstance(null);
		for (MethodBiFunctionPair<S1, S2> argument : chainedArguments) {
			Object v = argument.getFunction().apply(s1, s2);
			if (!ignoreNullValues || v != null) {
				builder = argument.getMethod().invoke(builder, v);
			}
		}
		for (MethodBiFunctionPair<S1, S2> argument : unchainedArguments) {
			Object v = argument.getFunction().apply(s1, s2);
			if (!ignoreNullValues || v != null) {
				argument.getMethod().invoke(builder, v);
			}
		}
		return builder;
	}
}
