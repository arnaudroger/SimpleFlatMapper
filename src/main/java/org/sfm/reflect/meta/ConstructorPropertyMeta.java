package org.sfm.reflect.meta;

import java.lang.reflect.Type;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.asm.ConstructorParameter;

public class ConstructorPropertyMeta<T, P> extends PropertyMeta<T, P> {

	public ConstructorPropertyMeta(String name, String column, ReflectionService reflectService, ConstructorParameter constructorParameter) {
		super(name, column, reflectService);
		this.constructorParameter = constructorParameter;
	}

	private final ConstructorParameter constructorParameter;
	
	@Override
	protected Setter<T, P> newSetter() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Type getType() {
		return constructorParameter.getType();
	}

	public ConstructorParameter getConstructorParameter() {
		return constructorParameter;
	}
	
	public boolean isConstructorProperty() {
		return true;
	}

}
