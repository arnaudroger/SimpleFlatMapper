package org.simpleflatmapper.reflect.asm;

import org.simpleflatmapper.reflect.BuilderInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.getter.BiFunctionGetter;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.util.BiFunction;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

public class BiInstantiatorKey {
	private final Object constructor;
	private final InjectedParam[] injectedParams;
	private final Class<?> s1;
	private final Class<?> s2;

	public BiInstantiatorKey(Object constructor, InjectedParam[] injectedParams, Class<?> s1, Class<?> s2) {
		super();
		this.constructor = constructor;
		this.injectedParams = injectedParams;
		this.s1 = s1;
		this.s2 = s2;
	}
	public BiInstantiatorKey(Class<?> target, Class<?> s1, Class<?> s2) throws NoSuchMethodException, SecurityException {
		this(target.getConstructor(), null, s1, s2);
	}
	public <S1, S2> BiInstantiatorKey(InstantiatorDefinition instantiatorDefinition, Map<Parameter, BiFunction<? super S1, ? super S2, ?>> injections, Class<?> s1, Class<?> s2) {
		this(getConstructor(instantiatorDefinition), paramAndBuilderFactoryClass(injections), s1, s2);
	}

	public static Object getConstructor(InstantiatorDefinition def) {
		if (def instanceof ExecutableInstantiatorDefinition) {
			return ((ExecutableInstantiatorDefinition)def).getExecutable();
		} else {
			return ((BuilderInstantiatorDefinition)def).getBuildMethod();
		}
	}
	private static <S1, S2> InjectedParam[] paramAndBuilderFactoryClass(Map<Parameter, BiFunction<? super S1, ? super S2, ?>> injections) {
		InjectedParam[] names = new InjectedParam[injections.size()];
		int i = 0;
		for(Map.Entry<Parameter, BiFunction<? super S1, ? super S2, ?>> e : injections.entrySet()) {
			BiFunction<? super S1, ? super S2, ?> function = e.getValue();

			Class<?> functionClass = function.getClass();

			if (function instanceof BiFunctionGetter) {
				functionClass = ((BiFunctionGetter)function).getGetter().getClass();
			}

			names[i++] = new InjectedParam(e.getKey().getName(), functionClass);
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
		result = prime * result + ((s1 == null) ? 0 : s1.hashCode());
		result = prime * result + ((s2 == null) ? 0 : s2.hashCode());
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
		BiInstantiatorKey other = (BiInstantiatorKey) obj;
		if (constructor == null) {
			if (other.constructor != null)
				return false;
		} else if (!constructor.equals(other.constructor))
			return false;
		if (!Arrays.equals(injectedParams, other.injectedParams))
			return false;
		if (s1 == null) {
			if (other.s1 != null)
				return false;
		} else if (!s1.equals(other.s1))
			return false;
		if (s2 == null) {
			if (other.s2 != null)
				return false;
		} else if (!s2.equals(other.s2))
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
	public Class<?> getS1() {
		return s1;
	}
	public Class<?> getS2() {
		return s2;
	}

	public Class<?> getDeclaringClass() {
		return getDeclaringClass(constructor);
	}

	public static Class<?> getDeclaringClass(Object constructor) {
		if (constructor instanceof Member) {
			return ((Member)constructor).getDeclaringClass();
		} else if(constructor instanceof ExecutableInstantiatorDefinition) {
			return ((ExecutableInstantiatorDefinition)constructor).getExecutable().getDeclaringClass();
		} else {
			return ((BuilderInstantiatorDefinition)constructor).getBuildMethod().getDeclaringClass();
		}
	}

	public static Class<?> getDeclaringClass(InstantiatorDefinition def) {
		return getDeclaringClass(getConstructor(def));
	}

	public ClassLoader getClassLoader() {
		return getDeclaringClass().getClassLoader();
	}
}
