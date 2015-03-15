package org.sfm.reflect.meta;

import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

import java.lang.reflect.Type;

public class ArrayElementPropertyMeta<T, E> extends PropertyMeta<T, E> {

	private final int index;
	private final ArrayClassMeta<T, E> arrayMetaData;
	public ArrayElementPropertyMeta(String name,  ReflectionService reflectService, int index, ArrayClassMeta<T, E> arrayMetaData) {
		super(name, reflectService);
        if (index < 0) throw new IllegalArgumentException("Invalid array index " + index);
		this.index = index;
		this.arrayMetaData = arrayMetaData;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Setter<T, E> newSetter() {
        return (Setter<T, E>) new IndexArraySetter<E>(index);
	}

    @SuppressWarnings("unchecked")
    @Override
    protected Getter<T, E> newGetter() {
        return (Getter<T, E>) new IndexArrayGetter<E>(index);
    }

    @Override
	public Type getType() {
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
