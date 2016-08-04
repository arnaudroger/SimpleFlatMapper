package org.simpleflatmapper.core.reflect.impl;

import org.simpleflatmapper.core.reflect.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.Instantiator;
import org.simpleflatmapper.core.reflect.InstantiatorDefinition;
import org.simpleflatmapper.core.reflect.Parameter;
import org.simpleflatmapper.util.ErrorHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public final class InjectStaticMethodInstantiator<S, T> implements Instantiator<S, T> {

	private final Method method;
	private final Class<?> declaringClass;
	private final ArgumentBuilder<S> argBuilder;
	private final InstantiatorDefinition instantiatorDefinition;

	public InjectStaticMethodInstantiator(ExecutableInstantiatorDefinition instantiatorDefinition, Map<Parameter, Getter<? super S, ?>> injections) {
		this.argBuilder = new ArgumentBuilder<S>(instantiatorDefinition, injections);
		this.method = (Method) instantiatorDefinition.getExecutable();
		this.declaringClass = method.getDeclaringClass();
		this.instantiatorDefinition = instantiatorDefinition;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T newInstance(S s) throws Exception {
		try {
			return (T) method.invoke(declaringClass, argBuilder.build(s));
		} catch(InvocationTargetException e) {
			return ErrorHelper.rethrow(e.getCause());
		}
	}

    @Override
    public String toString() {
        return "InjectStaticMethodInstantiator{" +
                "instantiatorDefinition=" + instantiatorDefinition +
                '}';
    }
}
