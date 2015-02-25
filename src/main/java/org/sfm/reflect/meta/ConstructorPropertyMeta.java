package org.sfm.reflect.meta;

import org.sfm.reflect.*;
import org.sfm.reflect.impl.NullSetter;

import java.lang.reflect.Type;

public class ConstructorPropertyMeta<T, P> extends PropertyMeta<T, P> {

    private final Class<T> owner;

    public ConstructorPropertyMeta(String name, String column, ReflectionService reflectService, ConstructorParameter constructorParameter, Class<T> owner) {
		super(name, column, reflectService);
		this.constructorParameter = constructorParameter;
        this.owner = owner;
	}

	private final ConstructorParameter constructorParameter;
	
	@Override
	protected Setter<T, P> newSetter() {
		return new NullSetter<T, P>();
	}

    @Override
    protected Getter<T, P> newGetter() {
        return reflectService.getObjectGetterFactory().getGetter(owner, getName());
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

    @Override
    public String toString() {
        return "ConstructorPropertyMeta{" +
                "owner=" + owner +
                ", constructorParameter=" + constructorParameter +
                '}';
    }
}
