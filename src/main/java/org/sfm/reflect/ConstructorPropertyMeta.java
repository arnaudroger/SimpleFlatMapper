package org.sfm.reflect;

import org.sfm.reflect.asm.ConstructorParameter;

public class ConstructorPropertyMeta<T, P> extends PropertyMeta<T, P> {

	public ConstructorPropertyMeta(String name, ConstructorParameter constructorParameter) {
		super(name);
		this.constructorParameter = constructorParameter;
	}

	private final ConstructorParameter constructorParameter;
	
	@Override
	protected Setter<T, P> newSetter() {
		throw new UnsupportedOperationException();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getType() {
		return (Class<T>) constructorParameter.getType();
	}

	public ConstructorParameter getConstructorParameter() {
		return constructorParameter;
	}
}
