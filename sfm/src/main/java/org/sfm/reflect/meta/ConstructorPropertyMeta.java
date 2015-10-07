package org.sfm.reflect.meta;

import org.sfm.reflect.*;
import org.sfm.reflect.impl.NullGetter;
import org.sfm.reflect.impl.NullSetter;

import java.lang.reflect.Type;

public class ConstructorPropertyMeta<T, P> extends PropertyMeta<T, P> {

    private final Class<T> owner;
    private final Setter<T, P> setter = new NullSetter<T, P>();
    private final Getter<T, P> getter;
    private final Parameter parameter;

    public ConstructorPropertyMeta(String name,
                                   ReflectionService reflectService,
                                   Parameter parameter,
                                   Class<T> owner) {
        this(name, reflectService, parameter, owner, new NullGetter<T, P>());
    }

    public ConstructorPropertyMeta(String name,
                                   ReflectionService reflectService,
                                   Parameter parameter,
                                   Class<T> owner, Getter<T, P> getter) {
		super(name, reflectService);
		this.parameter = parameter;
        this.owner = owner;
        this.getter = getter;
    }


	@Override
	public Setter<T, P> getSetter() {
        return setter;
	}

    @Override
    public Getter<T, P> getGetter() {
        return getter;
    }

    public ConstructorPropertyMeta<T, P> getter(Getter<T, P> getter) {
        return new ConstructorPropertyMeta<T, P>(getName(), reflectService, parameter, owner, getter);
    }

    @Override
	public Type getPropertyType() {
		return parameter.getGenericType();
	}

	public Parameter getParameter() {
		return parameter;
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
                ", constructorParameter=" + parameter +
                '}';
    }
}
