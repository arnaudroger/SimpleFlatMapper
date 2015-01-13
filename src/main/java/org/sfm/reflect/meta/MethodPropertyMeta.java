package org.sfm.reflect.meta;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final Method method;

	public MethodPropertyMeta(String name, String columnName, ReflectionService reflectService, Method method) {
		super(name, columnName, reflectService);
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

	@Override
	public String getPath() {
		return getName();
	}

}
