package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
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
