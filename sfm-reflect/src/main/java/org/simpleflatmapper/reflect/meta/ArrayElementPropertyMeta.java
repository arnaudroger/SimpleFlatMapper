package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.IntFactory;

import java.lang.reflect.Type;

public class ArrayElementPropertyMeta<T, E> extends PropertyMeta<T, E> {

	private final int index;
	private final ArrayClassMeta<T, E> arrayMetaData;
	private final IntFactory<Setter<T, E>> setterFactory;
	private final IntFactory<Getter<T, E>> getterFactory;

	@SuppressWarnings("unchecked")
	public ArrayElementPropertyMeta(String name, Type ownerType, ReflectionService reflectService,
									int index,
									ArrayClassMeta<T, E> arrayMetaData,
									IntFactory<Setter<T, E>> setterFactory,
									IntFactory<Getter<T, E>> getterFactory) {
		super(name, ownerType, reflectService);
		if (index < 0) throw new IllegalArgumentException("Invalid array index " + index);
		this.index = index;
		this.arrayMetaData = arrayMetaData;
		this.setterFactory = setterFactory;
		this.getterFactory = getterFactory;
	}

	@Override
	public Setter<T, E> getSetter() {
        return setterFactory.newInstance(index);
	}

    @Override
    public Getter<T, E> getGetter() {
        return getterFactory.newInstance(index);
    }

    @Override
	public Type getPropertyType() {
		return arrayMetaData.getElementTarget();
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String getPath() {
		return  "[" + index + "]";
	}

	@Override
	public PropertyMeta<T, E> withReflectionService(ReflectionService reflectionService) {
		return new ArrayElementPropertyMeta<T, E>(getName(), getOwnerType(), reflectionService, index, arrayMetaData.withReflectionService(reflectionService), setterFactory, getterFactory);
	}

	@Override
	public PropertyMeta<T, E> toNonMapped() {
		throw new UnsupportedOperationException();
	}

	@Override
    public String toString() {
        return "ArrayElementPropertyMeta{" +
                "index=" + index +
                '}';
    }


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ArrayElementPropertyMeta<?, ?> that = (ArrayElementPropertyMeta<?, ?>) o;

		if (index != that.index) return false;
		return arrayMetaData != null ? arrayMetaData.equals(that.arrayMetaData) : that.arrayMetaData == null;
	}

	@Override
	public int hashCode() {
		int result = index;
		result = 31 * result + (arrayMetaData != null ? arrayMetaData.hashCode() : 0);
		return result;
	}
}
