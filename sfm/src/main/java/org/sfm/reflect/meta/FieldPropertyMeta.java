package org.sfm.reflect.meta;

import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.impl.FieldGetter;
import org.sfm.reflect.impl.FieldSetter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final Field field;
	private final Type type;

	public FieldPropertyMeta(String name, ReflectionService reflectService, Field field, Type type) {
		super(name, reflectService);
		this.field = field;
		this.type = type;
	}

	@Override
	protected Setter<T, P> newSetter() {
		field.setAccessible(true);
		return new FieldSetter<T, P>(field);
	}

    @Override
    protected Getter<T, P> newGetter() {
        field.setAccessible(true);
        return new FieldGetter<T, P>(field);
    }

    @Override
	public Type getPropertyType() {
		return type;
	}

	@Override
	public String getPath() {
		return getName();
	}

    @Override
    public String toString() {
        return "FieldPropertyMeta{" +
                "field=" + field +
                ", type=" + type +
                '}';
    }
}
