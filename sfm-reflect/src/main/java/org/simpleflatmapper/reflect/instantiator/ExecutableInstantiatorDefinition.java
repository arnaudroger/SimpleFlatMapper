package org.simpleflatmapper.reflect.instantiator;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.Arrays;

public final class ExecutableInstantiatorDefinition implements InstantiatorDefinition {
	private final Member executable;
	private final Parameter[] parameters;
	private final Type type;

	public ExecutableInstantiatorDefinition(Member executable,
											Parameter... parameters) {
		super();
		this.executable = executable;
		this.parameters = parameters;
		this.type = executable instanceof Constructor ? Type.CONSTRUCTOR : Type.METHOD;
	}

	public Member getExecutable() {
		return executable;
	}

	@Override
	public Parameter[] getParameters() {
		return parameters;
	}

	@Override
	public boolean hasParam(Parameter param) {
		for (Parameter p : parameters) {
			if (p.equals(param)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String getName() {
		return executable.getName();
	}

	@Override
    public String toString() {
        return "InstantiatorDefinition{" +
                "executable=" + executable +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
