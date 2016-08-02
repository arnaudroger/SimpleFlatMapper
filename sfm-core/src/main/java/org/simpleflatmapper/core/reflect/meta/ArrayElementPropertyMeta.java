package org.simpleflatmapper.core.reflect.meta;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.ReflectionService;
import org.simpleflatmapper.core.reflect.Setter;

import java.lang.reflect.Type;

public class ArrayElementPropertyMeta<T, E> extends PropertyMeta<T, E> {

	private final int index;
	private final ArrayClassMeta<T, E> arrayMetaData;
	private final Setter<T, E> setter;
	private final Getter<T, E> getter;

	@SuppressWarnings("unchecked")
	public ArrayElementPropertyMeta(String name,  ReflectionService reflectService, int index, ArrayClassMeta<T, E> arrayMetaData) {
		super(name, reflectService);
        if (index < 0) throw new IllegalArgumentException("Invalid array index " + index);
		this.index = index;
		this.arrayMetaData = arrayMetaData;
		setter = (Setter<T, E>) new IndexArraySetter<E>(index);
		getter = (Getter<T, E>) new IndexArrayGetter<E>(index);
	}

	@Override
	public Setter<T, E> getSetter() {
        return setter;
	}

    @Override
    public Getter<T, E> getGetter() {
        return getter;
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
		return index + "." + getName();
	}


	private static class IndexArraySetter<E> implements Setter<E[], E> {
		private final int index;

		private IndexArraySetter(int index) {
			this.index = index;
		}

		@Override
        public void set(E[] target, E value) throws Exception {
			target[index] = value;
        }
	}

    private static class IndexArrayGetter<E> implements Getter<E[], E> {
        private final int index;

        private IndexArrayGetter(int index) {
            this.index = index;
        }

        @Override
        public E get(E[] target) throws Exception {
            return target[index];
        }
    }

    @Override
    public String toString() {
        return "ArrayElementPropertyMeta{" +
                "index=" + index +
                '}';
    }
}
