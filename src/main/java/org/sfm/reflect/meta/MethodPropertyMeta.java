package org.sfm.reflect.meta;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

public class MethodPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final Method method;

	public MethodPropertyMeta(String name, String column, ReflectionService reflectService, Method method) {
		super(name, column, reflectService);
		this.method = method;
	}

	@Override
	protected Setter<T, P> newSetter() {
		return reflectService.getSetterFactory().getMethodSetter(method);
	}

	@Override
	public Type getType() {
		return method.getGenericParameterTypes()[0];
	}

}
