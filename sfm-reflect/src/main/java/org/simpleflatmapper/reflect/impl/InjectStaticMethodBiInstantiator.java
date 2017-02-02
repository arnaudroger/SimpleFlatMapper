package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ErrorHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public final class InjectStaticMethodBiInstantiator<S1, S2, T> implements BiInstantiator<S1, S2, T> {

	private final Method method;
	private final Class<?> declaringClass;
	private final BiArgumentBuilder<S1, S2> argBuilder;
	private final InstantiatorDefinition instantiatorDefinition;

	public InjectStaticMethodBiInstantiator(ExecutableInstantiatorDefinition instantiatorDefinition, Map<Parameter, BiFunction<? super S1, ? super S2, ?>> injections) {
		this.argBuilder = new BiArgumentBuilder<S1, S2>(instantiatorDefinition, injections);
		this.method = (Method) instantiatorDefinition.getExecutable();
		this.declaringClass = method.getDeclaringClass();
		this.instantiatorDefinition = instantiatorDefinition;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T newInstance(S1 s1, S2 s2) throws Exception {
		try {
			return (T) method.invoke(declaringClass, argBuilder.build(s1, s2));
		} catch(InvocationTargetException e) {
			return ErrorHelper.rethrow(e.getCause());
		}
	}

    @Override
    public String toString() {
        return "InjectStaticMethodBiInstantiator{" +
                "instantiatorDefinition=" + instantiatorDefinition +
                '}';
    }
}
