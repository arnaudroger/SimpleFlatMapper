package org.sfm.reflect;

import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.impl.CalculateMaxIndex;
import org.sfm.map.FieldKey;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.mapper.PropertyMappingsBuilder;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.BuilderInstantiator;
import org.sfm.reflect.impl.EmptyConstructorInstantiator;
import org.sfm.reflect.impl.EmptyStaticMethodInstantiator;
import org.sfm.reflect.impl.InjectConstructorInstantiator;
import org.sfm.reflect.impl.InjectStaticMethodInstantiator;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.ForEachCallBack;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstantiatorFactory {
	private final AsmFactory asmFactory;

	private final boolean failOnAsmError;
	
	public InstantiatorFactory(final AsmFactory asmFactory) {
		this(asmFactory, false);
	}

	public InstantiatorFactory(AsmFactory asmFactory, boolean faileOnAsmError) {
		this.asmFactory = asmFactory;
		this.failOnAsmError = faileOnAsmError;
	}


	public <S, T, K extends FieldKey<K>, D extends ColumnDefinition<K, D>> Instantiator<S,T> getInstantiator(Type source, Type target, PropertyMappingsBuilder<T, K, D> propertyMappingsBuilder, Map<org.sfm.reflect.Parameter, Getter<? super S, ?>> constructorParameterGetterMap, org.sfm.map.GetterFactory<? super S, K> getterFactory) throws NoSuchMethodException {
		return  getInstantiator(source, target, propertyMappingsBuilder, constructorParameterGetterMap, getterFactory, true);
	}

	@SuppressWarnings("unchecked")
    public <S, T, K extends FieldKey<K>, D extends ColumnDefinition<K, D>> Instantiator<S,T> getInstantiator(Type source, Type target, PropertyMappingsBuilder<T, K, D> propertyMappingsBuilder, Map<org.sfm.reflect.Parameter, Getter<? super S, ?>> constructorParameterGetterMap, final org.sfm.map.GetterFactory<? super S, K> getterFactory,  boolean useAsmIfEnabled) throws NoSuchMethodException {

        if (propertyMappingsBuilder.isDirectProperty()) {
            Getter getter = propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, K, D>>() {
                public Getter getter;
                @Override
                public void handle(PropertyMapping<T, ?, K, D> propertyMapping) {
                    getter = getterFactory.newGetter(propertyMapping.getPropertyMeta().getPropertyType(), propertyMapping.getColumnKey(), propertyMapping.getColumnDefinition());
                }
            }).getter;

            return new GetterInstantiator<S, T>(getter);

        }

		if (TypeHelper.isArray(target)) {
			return getArrayInstantiator(TypeHelper.toClass(TypeHelper.getComponentTypeOfListOrArray(target)), propertyMappingsBuilder.forEachProperties(new CalculateMaxIndex<T, K, D>()).maxIndex + 1);
		} else {
			return getInstantiator(target, TypeHelper.toClass(source), propertyMappingsBuilder.getPropertyFinder().getEligibleInstantiatorDefinitions(), constructorParameterGetterMap,useAsmIfEnabled);
		}
	}

	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> getInstantiator(Type target, final Class<?> source, List<InstantiatorDefinition> constructors, Map<org.sfm.reflect.Parameter, Getter<? super S, ?>> injections, boolean useAsmIfEnabled) throws SecurityException {
		final InstantiatorDefinition instantiatorDefinition = getSmallerConstructor(constructors);

		if (instantiatorDefinition == null) {
			throw new IllegalArgumentException("No constructor available for " + target);
		}
		return getInstantiator(instantiatorDefinition, source, injections, useAsmIfEnabled);


	}

	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> getInstantiator(InstantiatorDefinition instantiatorDefinition, Class<?> source, Map<Parameter, Getter<? super S, ?>> injections, boolean useAsmIfEnabled) {
		if (asmFactory != null  && useAsmIfEnabled) {
			if (instantiatorDefinition instanceof ExecutableInstantiatorDefinition) {
				ExecutableInstantiatorDefinition executableInstantiatorDefinition = (ExecutableInstantiatorDefinition) instantiatorDefinition;
				Member executable = executableInstantiatorDefinition.getExecutable();
				if (Modifier.isPublic(executable.getModifiers())) {
					try {
						return asmFactory.createInstantiator(source, executableInstantiatorDefinition, injections);
					} catch (Exception e) {
						// fall back on reflection
						if (failOnAsmError) ErrorHelper.rethrow(e);
					}
				}
			} else {
				try {
					return asmFactory.createInstantiator(source, (BuilderInstantiatorDefinition)instantiatorDefinition, injections);
				} catch (Exception e) {
					// fall back on reflection
					if (failOnAsmError) ErrorHelper.rethrow(e);
				}
			}
		}

		switch (instantiatorDefinition.getType()) {
			case CONSTRUCTOR:
				return constructorInstantiator((ExecutableInstantiatorDefinition)instantiatorDefinition, injections);
			case METHOD:
				return methodInstantiator((ExecutableInstantiatorDefinition)instantiatorDefinition, injections);
			case BUILDER:
				return builderInstantiator((BuilderInstantiatorDefinition)instantiatorDefinition, injections, useAsmIfEnabled);
			default:
				throw new IllegalArgumentException("Unsupported executable type " + instantiatorDefinition);
		}
	}

	@SuppressWarnings("unchecked")
	private <S, T> Instantiator<S, T> builderInstantiator(BuilderInstantiatorDefinition instantiatorDefinition,
														  Map<Parameter, Getter<? super S, ?>> injections, boolean useAsmIfEnabled) {

		final Instantiator<Void, ?> buildInstantiator =
				getInstantiator(instantiatorDefinition.getBuilderInstantiator(), Void.class,
				 new HashMap<Parameter, Getter<? super Void, ?>>(), useAsmIfEnabled);
		Tuple2<Method, Getter<? super S, ?>>[] arguments = new Tuple2[injections.size()];

		int i = 0;
		for(Map.Entry<Parameter, Getter<? super S, ?>> e : injections.entrySet()) {
			arguments[i++] = new Tuple2<Method, Getter<? super S, ?>>(instantiatorDefinition.getSetters().get(e.getKey()), e.getValue());
		}

		return new BuilderInstantiator<S, T>(buildInstantiator, arguments, instantiatorDefinition.getBuildMethod());
	}

	private <S, T> Instantiator<S, T> methodInstantiator(
			ExecutableInstantiatorDefinition instantiatorDefinition,
			Map<Parameter, Getter<? super S, ?>> injections) {
		Method m = (Method) instantiatorDefinition.getExecutable();
		if (m.getParameterTypes().length == 0) {
			return new EmptyStaticMethodInstantiator<S, T>(m);
		} else {
			return new InjectStaticMethodInstantiator<S, T>(instantiatorDefinition, injections);
		}
	}


	@SuppressWarnings("unchecked")
	private <S, T> Instantiator<S, T> constructorInstantiator(
			ExecutableInstantiatorDefinition instantiatorDefinition,
			Map<Parameter, Getter<? super S, ?>> injections) {
		Constructor<? extends T> c = (Constructor<? extends T>) instantiatorDefinition.getExecutable();
		if (c.getParameterTypes().length == 0) {
			return new EmptyConstructorInstantiator<S, T>(c);
		} else {
			return new InjectConstructorInstantiator<S, T>(instantiatorDefinition, injections);
		}
	}

	public static InstantiatorDefinition getSmallerConstructor(final List<InstantiatorDefinition> constructors) {
        if (constructors == null) {
            return null;
        }

		InstantiatorDefinition selectedConstructor = null;
		
		for(InstantiatorDefinition c : constructors) {
			if (selectedConstructor == null || InstantiatorDefinitions.COMPARATOR.compare(c, selectedConstructor) < 0) {
				selectedConstructor = c;
			}
		}
		
		return selectedConstructor;
	}

	public <S, T> Instantiator<S, T> getArrayInstantiator(final Class<?> elementType, final int length) {
		return new ArrayInstantiator<S, T>(elementType, length);
	}

	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> getOneArgIdentityInstantiator(InstantiatorDefinition id) {
		Map<org.sfm.reflect.Parameter, Getter<? super S, ?>> injections = new HashMap<org.sfm.reflect.Parameter, Getter<? super S, ?>>();
		injections.put(id.getParameters()[0], new IdentityGetter());
		return getInstantiator(id, id.getParameters()[0].getType(), injections, true);
	}


	private static final class ArrayInstantiator<S, T> implements Instantiator<S, T> {
		private final Class<?> elementType;
		private final int length;

		public ArrayInstantiator(Class<?> elementType, int length) {
			this.elementType = elementType;
			this.length = length;
		}

		@SuppressWarnings("unchecked")
        @Override
        public T newInstance(S s) throws Exception {
            return (T) Array.newInstance(elementType, length);
        }
	}
}
