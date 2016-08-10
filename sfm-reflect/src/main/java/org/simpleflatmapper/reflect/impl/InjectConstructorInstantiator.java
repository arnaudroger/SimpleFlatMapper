package org.simpleflatmapper.reflect.impl;

import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.util.ErrorHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public final class InjectConstructorInstantiator<S, T> implements Instantiator<S, T> {

	private final Constructor<? extends T> constructor;
	private final ArgumentBuilder<S> argBuilder;
	private final InstantiatorDefinition instantiatorDefinition;

	@SuppressWarnings("unchecked")
	public InjectConstructorInstantiator(ExecutableInstantiatorDefinition instantiatorDefinition, Map<Parameter, Getter<? super S, ?>> injections) {
		this.argBuilder = new ArgumentBuilder<S>(instantiatorDefinition, injections);
		this.constructor = (Constructor<? extends T>) instantiatorDefinition.getExecutable();
		this.instantiatorDefinition = instantiatorDefinition;
	}

	@Override
	public T newInstance(S s) throws Exception {
		try {
			return constructor.newInstance(argBuilder.build(s));
		} catch(InvocationTargetException e) {
			return ErrorHelper.rethrow(e.getCause());
		}
	}

    @Override
    public String toString() {
        return "InjectConstructorInstantiator{" +
                "instantiatorDefinition=" + instantiatorDefinition +
                '}';
    }
}
