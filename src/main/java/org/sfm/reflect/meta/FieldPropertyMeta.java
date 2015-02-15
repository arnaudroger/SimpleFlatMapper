package org.sfm.reflect.meta;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.impl.FieldSetter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final Field field;
	private final Type type;

	public FieldPropertyMeta(String name, String columnName, ReflectionService reflectService, Field field, Type type) {
		super(name, columnName, reflectService);
		this.field = field;
		this.type = type;
	}

	@Override
	protected Setter<T, P> newSetter() {
		field.setAccessible(true);
		return new FieldSetter<T, P>(field);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getType() {
		return (Class<T>) type;
	}

	@Override
	public String getPath() {
		return getName();
	}

}
