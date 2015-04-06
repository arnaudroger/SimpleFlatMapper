package org.sfm.reflect;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public final class ConstructorDefinition<T> {
	private final Constructor<? extends T> constructor;
	private final Parameter[] parameters;
	public ConstructorDefinition(Constructor<? extends T> constructor,
			Parameter... parameters) {
		super();
		this.constructor = constructor;
		this.parameters = parameters;
	}
	public Constructor<? extends T> getConstructor() {
		return constructor;
	}
	public Parameter[] getParameters() {
		return parameters;
	}


	public boolean hasParam(Parameter param) {
		for (Parameter p : parameters) {
			if (p.equals(param)) {
				return true;
			}
		}
		return false;
	}

    @Override
    public String toString() {
        return "ConstructorDefinition{" +
                "constructor=" + constructor +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
