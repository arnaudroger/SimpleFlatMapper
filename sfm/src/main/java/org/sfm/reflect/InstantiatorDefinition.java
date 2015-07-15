package org.sfm.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

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


	public static InstantiatorDefinition lookForCompatibleOneArgument(Collection<InstantiatorDefinition> col, CompatibilityScorer scorer) {
		InstantiatorDefinition current = null;
		int currentScore = -1;

		for(InstantiatorDefinition id : col ) {
			if (id.getParameters().length == 1) {
				int score = scorer.score(id);
				if (score > currentScore) {
					current = id;
					currentScore = score;
				}
			}
		}
		return current;
	}

	public interface CompatibilityScorer {
		int score(InstantiatorDefinition id);
	}
}
