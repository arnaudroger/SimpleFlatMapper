package org.sfm.reflect;

import java.lang.reflect.Member;
import java.util.Arrays;

public final class InstantiatorDefinition {
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
}
