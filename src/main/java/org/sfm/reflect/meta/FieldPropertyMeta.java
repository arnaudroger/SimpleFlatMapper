package org.sfm.reflect.meta;

import java.lang.reflect.Field;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.impl.FieldSetter;

public class FieldPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final Field field;

	public FieldPropertyMeta(String name, String column, ReflectionService reflectService, Field field) {
		super(name, column, reflectService);
		this.field = field;
	}

	@Override
	protected Setter<T, P> newSetter() {
		field.setAccessible(true);
		return new FieldSetter<T, P>(field);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getType() {
		return (Class<T>) field.getType();
	}

}
