package org.sfm.reflect.asm;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;

public class InstantiatorKey {
	private final Constructor<?> constructor;
	private final String[] injectedParams;
	
	public InstantiatorKey(Constructor<?> constructor, String[] injectedParams) {
		super();
		this.constructor = constructor;
		this.injectedParams = injectedParams;
	}
	public InstantiatorKey(Class<?> target) throws NoSuchMethodException, SecurityException {
		this(target.getConstructor(), null);
	}
	public InstantiatorKey(ConstructorDefinition<?> constructorDefinition,
			Set<Parameter>  injections) {
		this(constructorDefinition.getConstructor(), toParamNameS(injections));
	}
	private static String[] toParamNameS(Set<Parameter> keySet) {
		String[] names = new String[keySet.size()];
		int i = 0;
		for(Parameter param : keySet) {
			names[i++] = param.getName();
		}
		Arrays.sort(names);
		return names;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constructor == null) ? 0 : constructor.hashCode());
		result = prime * result + Arrays.hashCode(injectedParams);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InstantiatorKey other = (InstantiatorKey) obj;
		if (constructor == null) {
			if (other.constructor != null)
				return false;
		} else if (!constructor.equals(other.constructor))
			return false;
		if (!Arrays.equals(injectedParams, other.injectedParams))
			return false;
		return true;
	}
	public Constructor<?> getConstructor() {
		return constructor;
	}
	public String[] getInjectedParams() {
		return injectedParams;
	}
	
	
}
