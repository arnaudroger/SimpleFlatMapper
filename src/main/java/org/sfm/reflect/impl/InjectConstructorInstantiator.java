package org.sfm.reflect.impl;

import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;

import java.lang.reflect.Constructor;
import java.util.Map;

public final class InjectConstructorInstantiator<S, T> implements Instantiator<S, T> {

	private final Constructor<? extends T> constructor;
	private final ArgumentBuilder<S, T> argBuilder;
	private final InstantiatorDefinition instantiatorDefinition;

	public InjectConstructorInstantiator(InstantiatorDefinition instantiatorDefinition, Map<Parameter, Getter<S, ?>> injections) {
		this.argBuilder = new ArgumentBuilder<S, T>(instantiatorDefinition, injections);
		this.constructor = (Constructor<? extends T>) instantiatorDefinition.getExecutable();
		this.instantiatorDefinition = instantiatorDefinition;
	}

	@Override
	public T newInstance(S s) throws Exception {
		return constructor.newInstance(argBuilder.build(s));
	}

    @Override
    public String toString() {
        return "InjectConstructorInstantiator{" +
                "instantiatorDefinition=" + instantiatorDefinition +
                '}';
    }
}
