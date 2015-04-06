package org.sfm.reflect.impl;

import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;

import java.util.Map;

public final class InjectConstructorInstantiator<S, T> implements Instantiator<S, T> {

	private final ConstructorDefinition<T> constructorDefinition;
	private final ArgumentBuilder<S, T> argBuilder;

	public InjectConstructorInstantiator(ConstructorDefinition<T> constructorDefinition, Map<Parameter, Getter<S, ?>> injections) {
		this.constructorDefinition = constructorDefinition;
		this.argBuilder = new ArgumentBuilder<S, T>(constructorDefinition, injections);
	}

	@Override
	public T newInstance(S s) throws Exception {
		return constructorDefinition.getConstructor().newInstance(argBuilder.build(s));
	}

    @Override
    public String toString() {
        return "InjectConstructorInstantiator{" +
                "constructorDefinition=" + constructorDefinition +
                '}';
    }
}
