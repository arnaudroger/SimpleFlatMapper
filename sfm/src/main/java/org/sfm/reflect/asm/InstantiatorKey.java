package org.sfm.reflect.asm;

import org.sfm.reflect.ExecutableInstantiatorDefinition;
import org.sfm.reflect.Parameter;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Set;

public class InstantiatorKey {
	private final Member constructor;
	private final String[] injectedParams;
	private final Class<?> source;
	
	public InstantiatorKey(Member constructor, String[] injectedParams,  Class<?> source) {
		super();
		this.constructor = constructor;
		this.injectedParams = injectedParams;
		this.source = source;
	}
	public InstantiatorKey(Class<?> target, Class<?> source) throws NoSuchMethodException, SecurityException {
		this(target.getConstructor(), null, source);
	}
	public InstantiatorKey(ExecutableInstantiatorDefinition instantiatorDefinition, Set<Parameter>  injections, Class<?> source) {
		this(instantiatorDefinition.getExecutable(), toParamNameS(injections), source);
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
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}
	public Member getConstructor() {
		return constructor;
	}
	public String[] getInjectedParams() {
		return injectedParams;
	}
	public Class<?> getSource() {
		return source;
	}
}
