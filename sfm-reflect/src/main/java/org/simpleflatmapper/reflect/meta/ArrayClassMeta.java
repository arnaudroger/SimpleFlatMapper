package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.getter.IndexedBooleanArrayGetter;
import org.simpleflatmapper.reflect.getter.IndexedByteArrayGetter;
import org.simpleflatmapper.reflect.getter.IndexedCharArrayGetter;
import org.simpleflatmapper.reflect.getter.IndexedDoubleArrayGetter;
import org.simpleflatmapper.reflect.getter.IndexedFloatArrayGetter;
import org.simpleflatmapper.reflect.getter.IndexedIntArrayGetter;
import org.simpleflatmapper.reflect.getter.IndexedListGetter;
import org.simpleflatmapper.reflect.getter.IndexedLongArrayGetter;
import org.simpleflatmapper.reflect.getter.IndexedObjectArrayGetter;
import org.simpleflatmapper.reflect.getter.IndexedShortArrayGetter;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.setter.AppendCollectionSetter;
import org.simpleflatmapper.reflect.setter.IndexedBooleanArraySetter;
import org.simpleflatmapper.reflect.setter.IndexedByteArraySetter;
import org.simpleflatmapper.reflect.setter.IndexedCharArraySetter;
import org.simpleflatmapper.reflect.setter.IndexedDoubleArraySetter;
import org.simpleflatmapper.reflect.setter.IndexedFloatArraySetter;
import org.simpleflatmapper.reflect.setter.IndexedIntArraySetter;
import org.simpleflatmapper.reflect.setter.IndexedListSetter;
import org.simpleflatmapper.reflect.setter.IndexedLongArraySetter;
import org.simpleflatmapper.reflect.setter.IndexedObjectArraySetter;
import org.simpleflatmapper.reflect.setter.IndexedShortArraySetter;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.util.BooleanSupplier;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.IntFactory;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

public class ArrayClassMeta<T, E> implements ClassMeta<T> {

	private final ReflectionService reflectionService;
	private final Type elementTarget;
	private final ClassMeta<E> elementClassMeta;
	private final Type type;
	private final Class<?> implementationType;
	private final InstantiatorDefinition constructor;

	public ArrayClassMeta(Type type, Type elementTarget, ReflectionService reflectionService) {
		this.type = type;
		this.elementTarget = elementTarget;
		this.reflectionService = reflectionService;
		this.elementClassMeta = reflectionService.getClassMeta(elementTarget);
		this.implementationType = findImpl(type);
		this.constructor = getConstructor(type);
	}

	private InstantiatorDefinition getConstructor(Type type) {
		if (TypeHelper.isArray(type)) {
			return null;
		} else {
			try {
				return new ExecutableInstantiatorDefinition(implementationType.getDeclaredConstructor());
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("No empty constructor for " + implementationType);
			}
		}
	}

	private Class<?> findImpl(Type type) {

		Class<?> clazz = TypeHelper.toClass(type);

		if (clazz.isArray()) {
			return clazz;
		}

		if (clazz.isInterface()) {
			if (List.class.equals(clazz) || Collection.class.equals(clazz) || Iterable.class.equals(clazz)) {
				return ArrayList.class;
			} else if (Set.class.equals(clazz)) {
				return HashSet.class;
			}
		} else if (!Modifier.isAbstract(clazz.getModifiers())) {
			return clazz;
		}

		throw new IllegalArgumentException("Unknown List impl for " + type);
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
		return new ArrayPropertyFinder<T, E>(this, propertyFilter, reflectionService.selfScoreFullName());
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

	@Override
	public int getNumberOfProperties() {
		return 10000;
	}

	@SuppressWarnings("unchecked")
	public <T, E> IntFactory<Setter<T, E>> newSetterFactory(final BooleanSupplier appendSetter) {
		if (TypeHelper.isArray(type)) {
			Type elementType = TypeHelper.getComponentTypeOfListOrArray(type);

			if (TypeHelper.isPrimitive(elementType)) {
				if (boolean.class.equals(elementType)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedBooleanArraySetter(i);
						}
					};
				} else if (byte.class.equals(elementType)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedByteArraySetter(i);
						}
					};
				} else if (char.class.equals(elementType)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedCharArraySetter(i);
						}
					};
				} else if (short.class.equals(elementType)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedShortArraySetter(i);
						}
					};
				} else if (int.class.equals(elementType)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedIntArraySetter(i);
						}
					};
				} else if (long.class.equals(elementType)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedLongArraySetter(i);
						}
					};
				} else if (float.class.equals(elementType)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedFloatArraySetter(i);
						}
					};
				} else if (double.class.equals(elementType)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedDoubleArraySetter(i);
						}
					};
				}
			} else {
				return new IntFactory<Setter<T, E>>() {
					@Override
					public Setter<T, E> newInstance(int i) {
						return new IndexedObjectArraySetter(i);
					}
				};
			}
		} else if (TypeHelper.isAssignable(List.class, implementationType)){
			return new IntFactory<Setter<T, E>>() {
				@Override
				public Setter<T, E> newInstance(int i) {
					if (appendSetter.getAsBoolean() && i == 0) {
						return AppendCollectionSetter.INSTANCE;
					} else {
						return new IndexedListSetter(i);
					}
				}
			};
		} else if (TypeHelper.isAssignable(Collection.class, implementationType)) {
			return new IntFactory<Setter<T, E>>() {
				@Override
				public Setter<T, E> newInstance(int i) {
					return AppendCollectionSetter.INSTANCE;
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
			Type elementType = TypeHelper.getComponentTypeOfListOrArray(type);

			if (TypeHelper.isPrimitive(elementType)) {
				if (boolean.class.equals(elementType)) {
					return new IntFactory<Getter<T, E>>() {
						@Override
						public Getter<T, E> newInstance(int i) {
							return (Getter<T, E>) new IndexedBooleanArrayGetter(i);
						}
					};
				} else if (byte.class.equals(elementType)) {
					return new IntFactory<Getter<T, E>>() {
						@Override
						public Getter<T, E> newInstance(int i) {
							return (Getter<T, E>) new IndexedByteArrayGetter(i);
						}
					};
				} else if (char.class.equals(elementType)) {
					return new IntFactory<Getter<T, E>>() {
						@Override
						public Getter<T, E> newInstance(int i) {
							return (Getter<T, E>) new IndexedCharArrayGetter(i);
						}
					};
				} else if (short.class.equals(elementType)) {
					return new IntFactory<Getter<T, E>>() {
						@Override
						public Getter<T, E> newInstance(int i) {
							return (Getter<T, E>) new IndexedShortArrayGetter(i);
						}
					};
				} else if (int.class.equals(elementType)) {
					return new IntFactory<Getter<T, E>>() {
						@Override
						public Getter<T, E> newInstance(int i) {
							return (Getter<T, E>) new IndexedIntArrayGetter(i);
						}
					};
				} else if (long.class.equals(elementType)) {
					return new IntFactory<Getter<T, E>>() {
						@Override
						public Getter<T, E> newInstance(int i) {
							return (Getter<T, E>) new IndexedLongArrayGetter(i);
						}
					};
				} else if (float.class.equals(elementType)) {
					return new IntFactory<Getter<T, E>>() {
						@Override
						public Getter<T, E> newInstance(int i) {
							return (Getter<T, E>) new IndexedFloatArrayGetter(i);
						}
					};
				} else if (double.class.equals(elementType)) {
					return new IntFactory<Getter<T, E>>() {
						@Override
						public Getter<T, E> newInstance(int i) {
							return (Getter<T, E>) new IndexedDoubleArrayGetter(i);
						}
					};
				}
			} else {
				return new IntFactory<Getter<T, E>>() {
					@Override
					public Getter<T, E> newInstance(int i) {
						return new IndexedObjectArrayGetter(i);
					}
				};
			}

		} else if (TypeHelper.isAssignable(List.class, implementationType)) {
			return new IntFactory<Getter<T, E>>() {
				@Override
				public Getter<T, E> newInstance(int i) {
					return new IndexedListGetter(i);
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


}
