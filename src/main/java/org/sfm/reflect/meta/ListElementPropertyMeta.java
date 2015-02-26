package org.sfm.reflect.meta;

import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.utils.BooleanSupplier;

import java.lang.reflect.Type;
import java.util.List;

public class ListElementPropertyMeta<T, E> extends PropertyMeta<T, E> {

	private final int index;
	private final ArrayClassMeta<T, E> arrayMetaData;
    private final BooleanSupplier isVerticalList;

    public ListElementPropertyMeta(String name, String column, ReflectionService reflectService, int index, ArrayClassMeta<T, E> arrayMetaData, BooleanSupplier isVerticalList) {
		super(name, column, reflectService);
		this.index = index;
		this.arrayMetaData = arrayMetaData;
        this.isVerticalList = isVerticalList;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Setter<T, E> newSetter() {
		if (isVerticalList.getAsBoolean()) {
            return (Setter<T, E>) new AppendListSetter<E>();
        } else {
			return (Setter<T, E>) new IndexListSetter<E>(index);
		}
	}

    @SuppressWarnings("unchecked")
    @Override
    protected Getter<T, E> newGetter() {
        if (isVerticalList.getAsBoolean()) {
            return (Getter<T, E>) new LastIndexListGetter<E>();
        } else {
            return (Getter<T, E>) new IndexListGetter<E>(index);
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

    private static class LastIndexListGetter<E> implements Getter<List<E>, E> {

        private LastIndexListGetter() {
        }

        @Override
        public E get(List<E> target) throws Exception {
            if (!target.isEmpty()) {
                return  target.get(target.size() - 1);
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
    }

    @Override
    public String toString() {
        return "ListElementPropertyMeta{" +
                "index=" + index +
                '}';
    }
}
