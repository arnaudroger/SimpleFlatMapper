package org.simpleflatmapper.core.reflect.impl;

import org.simpleflatmapper.core.reflect.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.core.reflect.InstantiatorDefinition;
import org.simpleflatmapper.core.reflect.Parameter;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.Instantiator;
import org.simpleflatmapper.core.utils.ErrorHelper;

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
