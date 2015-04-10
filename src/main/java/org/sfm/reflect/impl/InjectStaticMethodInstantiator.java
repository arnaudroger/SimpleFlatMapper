package org.sfm.reflect.impl;

import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;

import java.lang.reflect.Method;
import java.util.Map;

public final class InjectStaticMethodInstantiator<S, T> implements Instantiator<S, T> {

	private final Method method;
	private final Class<?> declaringClass;
	private final ArgumentBuilder<S, T> argBuilder;
	private final InstantiatorDefinition instantiatorDefinition;

	public InjectStaticMethodInstantiator(InstantiatorDefinition instantiatorDefinition, Map<Parameter, Getter<S, ?>> injections) {
		this.argBuilder = new ArgumentBuilder<S, T>(instantiatorDefinition, injections);
		this.method = (Method) instantiatorDefinition.getExecutable();
		this.declaringClass = method.getDeclaringClass();
		this.instantiatorDefinition = instantiatorDefinition;
	}

	@Override
	public T newInstance(S s) throws Exception {
		return (T) method.invoke(declaringClass, argBuilder.build(s));
	}

    @Override
    public String toString() {
        return "InjectStaticMethodInstantiator{" +
                "instantiatorDefinition=" + instantiatorDefinition +
                '}';
    }
}
