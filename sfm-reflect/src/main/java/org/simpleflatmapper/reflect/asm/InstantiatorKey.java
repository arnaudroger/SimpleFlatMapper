package org.simpleflatmapper.reflect.asm;

import org.simpleflatmapper.reflect.BuilderInstantiatorDefinition;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

public class InstantiatorKey<S> {
	private final Object constructor;
	private final InjectedParam[] injectedParams;
	private final Class<?> source;
	
	public InstantiatorKey(Object constructor, InjectedParam[] injectedParams,  Class<S> source) {
		super();
		this.constructor = constructor;
		this.injectedParams = injectedParams;
		this.source = source;
	}
	public InstantiatorKey(Class<?> target, Class<S> source) throws NoSuchMethodException, SecurityException {
		this(target.getConstructor(), null, source);
	}
	public InstantiatorKey(InstantiatorDefinition instantiatorDefinition, Map<Parameter, Getter<? super S, ?>> injections, Class<S> source) {
		this(getConstructor(instantiatorDefinition), paramAndGetterClass(injections), source);
	}

	private static Object getConstructor(InstantiatorDefinition def) {
		if (def instanceof ExecutableInstantiatorDefinition) {
			return ((ExecutableInstantiatorDefinition)def).getExecutable();
		} else {
			return ((BuilderInstantiatorDefinition)def).getBuildMethod();
		}
	}
	private static <S> InjectedParam[] paramAndGetterClass(Map<Parameter, Getter<? super S, ?>> injections) {
		InjectedParam[] names = new InjectedParam[injections.size()];
		int i = 0;
		for(Map.Entry<Parameter, Getter<? super S, ?>> e : injections.entrySet()) {
			names[i++] = new InjectedParam(e.getKey().getName(), e.getValue().getClass());
		}
		Arrays.sort(names, new Comparator<InjectedParam>() {
			@Override
			public int compare(InjectedParam o1,InjectedParam o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
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
	public Object getConstructor() {
		return constructor;
	}
	public String[] getInjectedParams() {
		if (injectedParams == null) {
			return new String[0];
		}
		String[] params = new String[injectedParams.length];
		for(int i = 0; i < params.length; i++) {
			params[i] = injectedParams[i].getName();
		}
		return params;
	}
	public Class<?> getSource() {
		return source;
	}

	public Class<?> getDeclaringClass() {
		Object constructor = this.constructor;
		return getDeclaringClass(constructor);
	}

	public static Class<?> getDeclaringClass(Object constructor) {
		if (constructor instanceof Member) {
			return ((Member) constructor).getDeclaringClass();
		} else if(constructor instanceof ExecutableInstantiatorDefinition) {
			return ((ExecutableInstantiatorDefinition) constructor).getExecutable().getDeclaringClass();
		} else {
			return ((BuilderInstantiatorDefinition) constructor).getBuildMethod().getDeclaringClass();
		}
	}
	public static Class<?> getDeclaringClass(InstantiatorDefinition definition) {
		return getDeclaringClass(getConstructor(definition));
	}
}
