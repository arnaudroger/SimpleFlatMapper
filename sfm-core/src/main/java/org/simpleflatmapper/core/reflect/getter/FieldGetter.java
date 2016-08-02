package org.simpleflatmapper.core.reflect.getter;

import org.simpleflatmapper.core.reflect.Getter;

import java.lang.reflect.Field;

public final class FieldGetter<T, P> implements Getter<T, P> {

	private final Field field;

    public FieldGetter(final Field field) {
		this.field = field;
	}

	@SuppressWarnings("unchecked")
    public P get(final T target) throws IllegalAccessException {
		return (P) field.get(target);
	}

    @Override
    public String toString() {
        return "FieldSetter{" +
                "field=" + field +
                '}';
    }
}
