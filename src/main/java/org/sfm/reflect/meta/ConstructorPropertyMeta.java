package org.sfm.reflect.meta;

import org.sfm.reflect.ConstructorParameter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

import java.lang.reflect.Type;

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
		return constructorParameter.getResolvedType();
	}

	public ConstructorParameter getConstructorParameter() {
		return constructorParameter;
	}
	
	public boolean isConstructorProperty() {
		return true;
	}

	@Override
	public String getPath() {
		return getName();
	}

}
