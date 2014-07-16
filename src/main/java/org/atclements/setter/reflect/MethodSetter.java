package org.atclements.setter.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.atclements.setter.Setter;

public class MethodSetter implements Setter {

	private final Method method;
	
	public MethodSetter(Method method) {
		this.method = method;
	}

	public void set(Object target, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		method.invoke(target, value);
	}

}
