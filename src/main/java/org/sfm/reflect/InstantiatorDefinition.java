package org.sfm.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class InstantiatorDefinition implements Comparable<InstantiatorDefinition> {
	private final Member executable;
	private final Parameter[] parameters;
	public InstantiatorDefinition(Member executable,
								  Parameter... parameters) {
		super();
		this.executable = executable;
		this.parameters = parameters;
	}

	public Member getExecutable() {
		return executable;
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
        return "InstantiatorDefinition{" +
                "executable=" + executable +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }

	@Override
	public int compareTo(InstantiatorDefinition o) {

		if (executable instanceof Constructor) {
			if (o.executable instanceof Method) {
				return -1;
			}
		} else {
			if (o.executable instanceof Constructor) {
				return 1;
			}
		}

		if (executable instanceof Method) {
			if (isValueOf(executable.getName())) {
				if (!isValueOf(o.executable.getName())) {
					return -1;
				}
			} else if (isValueOf(o.executable.getName())) {
				return 1;
			}
		}

		final int p = parameters.length - o.parameters.length;

		if (p == 0) {
			return executable.getName().compareTo(o.executable.getName());
		}

		return p;
	}

	private boolean isValueOf(String name) {
		return name.equals("valueOf") || name.equals("of") || name.equals("newInstance");
	}
}
