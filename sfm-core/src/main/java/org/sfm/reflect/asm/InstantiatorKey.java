package org.sfm.reflect.asm;

import org.sfm.reflect.BuilderInstantiatorDefinition;
import org.sfm.reflect.ExecutableInstantiatorDefinition;
import org.sfm.reflect.Getter;
import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;
import org.sfm.tuples.Tuple2;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

public class InstantiatorKey {
	private final Object constructor;
	private final Tuple2<String, Class>[] injectedParams;
	private final Class<?> source;
	
	public InstantiatorKey(Object constructor, Tuple2<String, Class>[] injectedParams,  Class<?> source) {
		super();
		this.constructor = constructor;
		this.injectedParams = injectedParams;
		this.source = source;
	}
	public InstantiatorKey(Class<?> target, Class<?> source) throws NoSuchMethodException, SecurityException {
		this(target.getConstructor(), null, source);
	}
	public InstantiatorKey(InstantiatorDefinition instantiatorDefinition, Map injections, Class<?> source) {
		this(getConstructor(instantiatorDefinition), paramAndGetterClass(injections), source);
	}

	private static Object getConstructor(InstantiatorDefinition def) {
		if (def instanceof ExecutableInstantiatorDefinition) {
			return ((ExecutableInstantiatorDefinition)def).getExecutable();
		} else {
			return ((BuilderInstantiatorDefinition)def).getBuildMethod();
		}
	}
	private static Tuple2<String, Class>[] paramAndGetterClass(Map<Parameter, Getter<?, ?>> injections) {
		Tuple2<String, Class>[] names = new Tuple2[injections.size()];
		int i = 0;
		for(Map.Entry<Parameter, Getter<?, ?>> e : injections.entrySet()) {
			names[i++] = new Tuple2<String, Class>(e.getKey().getName(), e.getValue().getClass());
		}
		Arrays.sort(names, new Comparator<Tuple2<String, Class>>() {
			@Override
			public int compare(Tuple2<String, Class> o1, Tuple2<String, Class> o2) {
				return o1.first().compareTo(o2.first());
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
			params[i] = injectedParams[i].first();
		}
		return params;
	}
	public Class<?> getSource() {
		return source;
	}

	public Class<?> getDeclaringClass() {
		if (constructor instanceof Member) {
			return ((Member)constructor).getDeclaringClass();
		} else if(constructor instanceof ExecutableInstantiatorDefinition) {
			return ((ExecutableInstantiatorDefinition)constructor).getExecutable().getDeclaringClass();
		} else {
			return ((BuilderInstantiatorDefinition)constructor).getBuildMethod().getDeclaringClass();
		}
	}
}
