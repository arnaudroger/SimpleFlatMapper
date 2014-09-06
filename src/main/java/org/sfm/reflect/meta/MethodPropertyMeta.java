package org.sfm.reflect.meta;

import java.lang.reflect.Method;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

public class MethodPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final Method method;

	public MethodPropertyMeta(String name, ReflectionService reflectService, Method method) {
		super(name, reflectService);
		this.method = method;
	}

	@Override
	protected Setter<T, P> newSetter() {
		return reflectService.getSetterFactory().getMethodSetter(method);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getType() {
		return (Class<T>) method.getParameterTypes()[0];
	}

}
