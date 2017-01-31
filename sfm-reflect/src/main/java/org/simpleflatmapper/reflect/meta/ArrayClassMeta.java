package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.util.BooleanSupplier;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.IntFactory;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public class ArrayClassMeta<T, E> implements ClassMeta<T> {

	private final ReflectionService reflectionService;
	private final Type elementTarget;
	private final ClassMeta<E> elementClassMeta;
	private final Type type;
	private final InstantiatorDefinition constructor;

	public ArrayClassMeta(Type type, Type elementTarget, ReflectionService reflectionService) {
		this.type = type;
		this.elementTarget = elementTarget;
		this.reflectionService = reflectionService;
		this.elementClassMeta = reflectionService.getClassMeta(elementTarget);
		this.constructor = getConstructor(type);
	}

	private InstantiatorDefinition getConstructor(Type type) {

		if (TypeHelper.isArray(type)) {
			return null;
		} else {
			Class<?> implClass = findListImpl(type);
			try {
				return new ExecutableInstantiatorDefinition(implClass.getDeclaredConstructor());
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("No empty constructor for " + implClass);
			}
		}
	}

	private Class<?> findListImpl(Type type) {
		Class<?> clazz = TypeHelper.toClass(type);

		if (clazz.isInterface()) {
			if (List.class.equals(clazz)) {
				return ArrayList.class;
			} else if (Set.class.equals(clazz)) {
				return HashSet.class;
			}
		} else if (!Modifier.isAbstract(clazz.getModifiers())) {
			return clazz;
		}

		throw new IllegalArgumentException("No known List impl for " + type);
	}

	public ClassMeta<E> getElementClassMeta() {
		return elementClassMeta;
	}
	
	public Type getElementTarget() {
		return elementTarget;
	}

	@Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<T> newPropertyFinder(Predicate<PropertyMeta<?, ?>> propertyFilter) {
		return new ArrayPropertyFinder<T, E>(this, propertyFilter);
	}

	public Type getType() {
		return type;
	}

    public boolean isArray() {
        return TypeHelper.isArray(type);
    }

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		if (constructor != null) {
			return Arrays.asList(constructor);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public void forEachProperties(Consumer<? super PropertyMeta<T, ?>> consumer) {
		throw new UnsupportedOperationException("Cannot forEach property on array as variable");
	}

	@SuppressWarnings("unchecked")
	public <T, E> IntFactory<Setter<T, E>> newSetterFactory(final BooleanSupplier isVertical) {
		if (TypeHelper.isArray(type)) {
			return new IntFactory<Setter<T, E>>() {
				@Override
				public Setter<T, E> newInstance(int i) {
					return new IndexArraySetter(i);
				}
			};
		} else if (TypeHelper.isAssignable(type, List.class)){
			return new IntFactory<Setter<T, E>>() {
				@Override
				public Setter<T, E> newInstance(int i) {
					if (isVertical.getAsBoolean() && i == 0) {
						return new AppendListSetter();
					} else {
						return new IndexListSetter(i);
					}
				}
			};
		} else if (TypeHelper.isAssignable(type, Set.class)) {
			return new IntFactory<Setter<T, E>>() {
				@Override
				public Setter<T, E> newInstance(int i) {
					return new AppendSetSetter();
				}
			};
		}
		return new IntFactory<Setter<T, E>>() {
			@Override
			public Setter<T, E> newInstance(int i) {
				return (Setter<T, E>) NullSetter.NULL_SETTER;
			}
		};
	}

	@SuppressWarnings("unchecked")
	public <T, E> IntFactory<Getter<T, E>> newGetterFactory() {
		if (TypeHelper.isArray(type)) {
			return new IntFactory<Getter<T, E>>() {
				@Override
				public Getter<T, E> newInstance(int i) {
					return new IndexArrayGetter(i);
				}
			};
		} else if (TypeHelper.isAssignable(type, List.class)) {
			return new IntFactory<Getter<T, E>>() {
				@Override
				public Getter<T, E> newInstance(int i) {
					return new IndexListGetter(i);
				}
			};
		}
		return new IntFactory<Getter<T, E>>() {
			@Override
			public Getter<T, E> newInstance(int i) {
				return (Getter<T, E>) NullGetter.getter();
			}
		};
	}


	public static final class IndexArraySetter<E> implements Setter<E[], E> {
		private final int index;

		private IndexArraySetter(int index) {
			this.index = index;
		}

		@Override
		public void set(E[] target, E value) throws Exception {
			target[index] = value;
		}
	}

	public static final class IndexArrayGetter<E> implements Getter<E[], E> {
		private final int index;

		private IndexArrayGetter(int index) {
			this.index = index;
		}

		@Override
		public E get(E[] target) throws Exception {
			return target[index];
		}
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

	private static class AppendSetSetter<E> implements Setter<Set<E>, E> {

		private AppendSetSetter() {
		}

		@Override
		public void set(Set<E> target, E value) throws Exception {
			target.add(value);
		}

		@Override
		public String toString() {
			return "AppendSetSetter{}";
		}
	}

}
