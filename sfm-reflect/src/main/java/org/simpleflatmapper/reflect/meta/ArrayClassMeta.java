package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.BuilderInstantiatorDefinition;
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
import org.simpleflatmapper.util.ErrorHelper;
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
	private final InstantiatorDefinitionAndIntermediatType instInfo;
	private final boolean needTransformer;

	public ArrayClassMeta(Type type, Type elementTarget, ReflectionService reflectionService, InstantiatorDefinitionAndIntermediatType typeInfo) {
		this.type = type;
		this.elementTarget = elementTarget;
		this.reflectionService = reflectionService;
		this.needTransformer = typeInfo.needTransform;
		this.elementClassMeta = reflectionService.getClassMeta(elementTarget);
		this.instInfo = typeInfo;
	}

	public ArrayClassMeta(Type type, Type elementTarget, ReflectionService reflectionService, boolean needTransformer, ClassMeta<E> elementClassMeta, InstantiatorDefinitionAndIntermediatType instInfo) {
		this.reflectionService = reflectionService;
		this.elementTarget = elementTarget;
		this.elementClassMeta = elementClassMeta;
		this.type = type;
		this.instInfo = instInfo;
		this.needTransformer = needTransformer;
	}

	@Override
	public ArrayClassMeta<T, E> withReflectionService(ReflectionService reflectionService) {
		return new ArrayClassMeta<T, E>(type, elementTarget, reflectionService, needTransformer, reflectionService.<E>getClassMeta(elementClassMeta.getType()), instInfo);
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
	public PropertyFinder<T> newPropertyFinder() {
		return new ArrayPropertyFinder<T, E>(this, reflectionService.selfScoreFullName());
	}

	public Type getType() {
		return type;
	}

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		if (instInfo.instantiatorDefinition != null) {
			return Arrays.asList(instInfo.instantiatorDefinition);
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

	@Override
	public boolean needTransformer() {
		return needTransformer;
	}



	@SuppressWarnings("unchecked")
	public <T, E> IntFactory<Setter<T, E>> newSetterFactory(final BooleanSupplier appendSetter) {
		if (TypeHelper.isArray(type)) {
			if (TypeHelper.isPrimitive(elementTarget)) {
				if (boolean.class.equals(elementTarget)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedBooleanArraySetter(i);
						}
					};
				} else if (byte.class.equals(elementTarget)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedByteArraySetter(i);
						}
					};
				} else if (char.class.equals(elementTarget)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedCharArraySetter(i);
						}
					};
				} else if (short.class.equals(elementTarget)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedShortArraySetter(i);
						}
					};
				} else if (int.class.equals(elementTarget)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedIntArraySetter(i);
						}
					};
				} else if (long.class.equals(elementTarget)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedLongArraySetter(i);
						}
					};
				} else if (float.class.equals(elementTarget)) {
					return new IntFactory<Setter<T, E>>() {
						@Override
						public Setter<T, E> newInstance(int i) {
							return (Setter<T, E>) new IndexedFloatArraySetter(i);
						}
					};
				} else if (double.class.equals(elementTarget)) {
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
		} else if (TypeHelper.isAssignable(List.class, instInfo.intermediateType)){
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
		} else if (TypeHelper.isAssignable(Collection.class, instInfo.intermediateType)) {
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

		} else if (TypeHelper.isAssignable(List.class, instInfo.intermediateType)) {
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

	public boolean isIndexedAccessible() {
		return TypeHelper.isAssignable(List.class, instInfo.intermediateType) || TypeHelper.isArray(type);
	}


	public static class InstantiatorDefinitionAndIntermediatType {
		public final InstantiatorDefinition instantiatorDefinition;
		public final Type intermediateType;
		public final boolean needTransform;

		public InstantiatorDefinitionAndIntermediatType(InstantiatorDefinition instantiatorDefinition, Type intermediateType, boolean needTransform) {
			this.instantiatorDefinition = instantiatorDefinition;
			this.intermediateType = intermediateType;
			this.needTransform = needTransform;
		}
	}
	
	public interface InstantiatorDefinitionAndIntermediatTypeFactory {
		boolean supports(Type type);
		InstantiatorDefinitionAndIntermediatType newTypeInfo(Type type);
		
	}
	
	
	private static final List<InstantiatorDefinitionAndIntermediatTypeFactory> intermediatTypeFactories;
	static {
		intermediatTypeFactories = new ArrayList<InstantiatorDefinitionAndIntermediatTypeFactory>();


		intermediatTypeFactories.add(new InstantiatorDefinitionAndIntermediatTypeFactory() {
			@Override
			public boolean supports(Type type) {
				return TypeHelper.isArray(type);
			}

			@Override
			public InstantiatorDefinitionAndIntermediatType newTypeInfo(Type type) {
				return new InstantiatorDefinitionAndIntermediatType(null, type, false);
			}
		});


		intermediatTypeFactories.add(new InstantiatorDefinitionAndIntermediatTypeFactory() {
			@Override
			public boolean supports(Type type) {
				return TypeHelper.areEquals(type, List.class)
						|| TypeHelper.areEquals(type, Collection.class)
						|| TypeHelper.areEquals(type, Iterable.class)
						;
			}

			@Override
			public InstantiatorDefinitionAndIntermediatType newTypeInfo(Type type) {
				return new InstantiatorDefinitionAndIntermediatType(getConstructor(ArrayList.class), ArrayList.class, false);
			}
		});

		intermediatTypeFactories.add(new InstantiatorDefinitionAndIntermediatTypeFactory() {
			@Override
			public boolean supports(Type type) {
				return TypeHelper.areEquals(type, Set.class)
						;
			}
			@Override
			public InstantiatorDefinitionAndIntermediatType newTypeInfo(Type type) {
				return new InstantiatorDefinitionAndIntermediatType(getConstructor(HashSet.class), HashSet.class, false);
			}
		});

		intermediatTypeFactories.add(new InstantiatorDefinitionAndIntermediatTypeFactory() {
			@Override
			public boolean supports(Type type) {
				return "org.eclipse.collections.api.list.MutableList".equals(TypeHelper.toClass(type).getName());
			}
			@Override
			public InstantiatorDefinitionAndIntermediatType newTypeInfo(Type type) {
				try {
					Class clazz = TypeHelper.toClass(type).getClassLoader().loadClass("org.eclipse.collections.impl.list.mutable.FastList");
					return new InstantiatorDefinitionAndIntermediatType(getConstructor(clazz), clazz, false);
				} catch (ClassNotFoundException e) {
					return ErrorHelper.rethrow(e);
				}

			}
		});

		intermediatTypeFactories.add(new InstantiatorDefinitionAndIntermediatTypeFactory() {
			@Override
			public boolean supports(Type type) {
				return "com.google.protobuf.ProtocolStringList".equals(TypeHelper.toClass(type).getName());
			}
			@Override
			public InstantiatorDefinitionAndIntermediatType newTypeInfo(Type type) {
				try {
					Class clazz = TypeHelper.toClass(type).getClassLoader().loadClass("com.google.protobuf.LazyStringArrayList");
					return new InstantiatorDefinitionAndIntermediatType(getConstructor(clazz), clazz, false);
				} catch (ClassNotFoundException e) {
					return ErrorHelper.rethrow(e);
				}

			}
		});

		intermediatTypeFactories.add(new InstantiatorDefinitionAndIntermediatTypeFactory() {
			@Override
			public boolean supports(Type type) {
				return "org.eclipse.collections.api.list.ImmutableList".equals(TypeHelper.toClass(type).getName());
			}
			@Override
			public InstantiatorDefinitionAndIntermediatType newTypeInfo(Type type) {
				try {
					Class clazz = TypeHelper.toClass(type).getClassLoader().loadClass("org.eclipse.collections.impl.list.mutable.FastList");

					return new InstantiatorDefinitionAndIntermediatType(
							new BuilderInstantiatorDefinition(getConstructor(clazz),
									new HashMap<org.simpleflatmapper.reflect.Parameter, java.lang.reflect.Method>(),
									clazz.getMethod("toImmutable")),
							clazz, true);
				} catch (ClassNotFoundException e) {
					return ErrorHelper.rethrow(e);
				} catch (NoSuchMethodException e) {
					return ErrorHelper.rethrow(e);
				}
			}
		});

		intermediatTypeFactories.add(new InstantiatorDefinitionAndIntermediatTypeFactory() {
			@Override
			public boolean supports(Type type) {
				return "com.google.common.collect.ImmutableList".equals(TypeHelper.toClass(type).getName());
			}
			@Override
			public InstantiatorDefinitionAndIntermediatType newTypeInfo(Type type) {
				try {
					Class builderClass = TypeHelper.toClass(type).getClassLoader().loadClass("com.google.common.collect.ImmutableList");
					return new InstantiatorDefinitionAndIntermediatType(
							new BuilderInstantiatorDefinition(getConstructor(ArrayList.class),
									new HashMap<org.simpleflatmapper.reflect.Parameter, java.lang.reflect.Method>(),
									builderClass.getMethod("copyOf", Collection.class)),
							ArrayList.class, true);
				} catch (ClassNotFoundException e) {
					return ErrorHelper.rethrow(e);
				} catch (NoSuchMethodException e) {
					return ErrorHelper.rethrow(e);
				}
			}
		});
	}


	public static boolean supports(Type target) {
		Class<?> clazz = TypeHelper.toClass(target);

		for(InstantiatorDefinitionAndIntermediatTypeFactory factory  :intermediatTypeFactories) {
			if (factory.supports(target)) {
				return true;
			}
		}

		return (Collection.class.isAssignableFrom(clazz) || Iterable.class.equals(clazz));
	}

	public static <T, E> ArrayClassMeta<T, E> of(Type type, Type elementTarget, ReflectionService reflectionService) {
		InstantiatorDefinitionAndIntermediatType typeInfo = getTypeInfo(type);
		return new ArrayClassMeta<T, E>(type, elementTarget, reflectionService, typeInfo);
	}
	
	
	public static InstantiatorDefinitionAndIntermediatType getTypeInfo(Type type) {
		for(InstantiatorDefinitionAndIntermediatTypeFactory factory  :intermediatTypeFactories) {
			if (factory.supports(type)) {
				return factory.newTypeInfo(type);
			}
		}

		Class<Object> clazz = TypeHelper.toClass(type);

		if (!Modifier.isAbstract(clazz.getModifiers())) {
			return new InstantiatorDefinitionAndIntermediatType(getConstructor(type), type, false);
		}
		throw new IllegalArgumentException("Unknown List impl for " + type);
	}


	private static InstantiatorDefinition getConstructor(Type type) {
		if (TypeHelper.isArray(type)) {
			return null;
		} else {
			try {
				return new ExecutableInstantiatorDefinition(TypeHelper.toClass(type).getDeclaredConstructor());
			} catch (NoSuchMethodException e) {
				throw new IllegalArgumentException("No empty constructor for " + type);
			}
		}
	}


}
