package org.sfm.reflect.meta;

import java.lang.reflect.Type;
import java.util.List;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;

public class ArrayElementPropertyMeta<T, E> extends PropertyMeta<T, E> {

	private final int index;
	private final ArrayClassMeta<T, E> arrayMetaData;
	public ArrayElementPropertyMeta(String name,  String column, ReflectionService reflectService, int index, ArrayClassMeta<T, E> arrayMetaData) {
		super(name, column, reflectService);
		this.index = index;
		this.arrayMetaData = arrayMetaData;
	}

	@Override
	protected Setter<T, E> newSetter() {
		if (List.class.isAssignableFrom(TypeHelper.toClass(arrayMetaData.getType()))) {
			return (Setter<T, E>) new ListSetter<E>(index);
		} else if (TypeHelper.toClass(arrayMetaData.getType()).isArray()) {
			return (Setter<T, E>) new ArraySetter<E>(index);
		} else {
			throw new IllegalArgumentException("Asking setter on unsupported type " + arrayMetaData.getType());
		}
	}

	@Override
	public Type getType() {
		return arrayMetaData.getElementTarget();
	}


	public int getIndex() {
		return index;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public String getPath() {
		return index + "." + getName();
	}


	private class ArraySetter<E> implements Setter<E[], E> {
		private final int index;

		private ArraySetter(int index) {
			this.index = index;
		}

		@Override
        public void set(E[] target, E value) throws Exception {
			target[index] = value;
        }
	}

	private class ListSetter<E> implements Setter<List<E>, E> {
		private final int index;

		private ListSetter(int index) {
			this.index = index;
		}

		@Override
		public void set(List<E> target, E value) throws Exception {
			while(target.size() <= index) {
				target.add(null);
			}
			target.set(index, value);
		}
	}
}
