package org.sfm.reflect;

import java.lang.reflect.Method;

public class MethodPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final Method method;
	private final SetterFactory setterFactory;

	public MethodPropertyMeta(String name, Method method, SetterFactory setterFactory) {
		super(name);
		this.method = method;
		this.setterFactory = setterFactory;
	}

	@Override
	protected Setter<T, P> newSetter() {
		return setterFactory.getMethodSetter(method);
	}

}
