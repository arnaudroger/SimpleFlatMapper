package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.BooleanSupplier;

import java.lang.reflect.Type;
import java.util.List;

public class ListElementPropertyMeta<T, E> extends PropertyMeta<T, E> {

	private final int index;
	private final ArrayClassMeta<T, E> arrayMetaData;
    private final BooleanSupplier isVerticalList;

    public ListElementPropertyMeta(String name, ReflectionService reflectService, int index, ArrayClassMeta<T, E> arrayMetaData, BooleanSupplier isVerticalList) {
		super(name, reflectService);
		this.index = index;
		this.arrayMetaData = arrayMetaData;
        this.isVerticalList = isVerticalList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Setter<T, E> getSetter() {
		if (isVerticalList.getAsBoolean()) {
            return (Setter<T, E>) new AppendListSetter<E>();
        } else {
			return (Setter<T, E>) new IndexListSetter<E>(index);
		}
	}

    @SuppressWarnings("unchecked")
    @Override
    public Getter<T, E> getGetter() {
        return (Getter<T, E>) new IndexListGetter<E>(index);
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
		return getName();
	}

    private static class IndexListGetter<E> implements Getter<List<E>, E> {
        private final int index;

        private IndexListGetter(int index) {
            this.index = index;
        }

        @Override
        public E get(List<E> target) throws Exception {
            if (index < target.size()) {
                return  target.get(index);
            }
            return null;
        }
    }

	private static class IndexListSetter<E> implements Setter<List<E>, E> {
		private final int index;

		private IndexListSetter(int index) {
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

    private static class AppendListSetter<E> implements Setter<List<E>, E> {

        private AppendListSetter() {
        }

        @Override
        public void set(List<E> target, E value) throws Exception {
            target.add(value);
        }

        @Override
        public String toString() {
            return "AppendListSetter{}";
        }
    }

    @Override
    public String toString() {
        return "ListElementPropertyMeta{" +
                "index=" + index +
                '}';
    }
}
