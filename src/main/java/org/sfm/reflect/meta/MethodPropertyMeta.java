package org.sfm.reflect.meta;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class MethodPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final Method method;
	private final Type type;

	public MethodPropertyMeta(String name, String columnName, ReflectionService reflectService, Method method, Type type) {
		super(name, columnName, reflectService);
		this.method = method;
		this.type = type;
	}

	@Override
	protected Setter<T, P> newSetter() {
		return reflectService.getSetterFactory().getMethodSetter(method);
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public String getPath() {
		return getName();
	}
}
