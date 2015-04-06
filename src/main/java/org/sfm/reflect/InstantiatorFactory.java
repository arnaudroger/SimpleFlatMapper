package org.sfm.reflect;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.impl.CalculateMaxIndex;
import org.sfm.map.FieldKey;
import org.sfm.map.impl.PropertyMapping;
import org.sfm.map.impl.PropertyMappingsBuilder;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.InjectConstructorInstantiator;
import org.sfm.reflect.impl.StaticConstructorInstantiator;
import org.sfm.utils.ForEachCallBack;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class InstantiatorFactory {
	private static final Object[] EMPTY_ARGS = new Object[]{};

	private final AsmFactory asmFactory;
	
	public InstantiatorFactory(final AsmFactory asmFactory) {
		this.asmFactory = asmFactory;
	}


	public <S, T, K extends FieldKey<K>, D extends ColumnDefinition<K, D>> Instantiator<S,T> getInstantiator(Type source, Type target, PropertyMappingsBuilder<T, K, D> propertyMappingsBuilder, Map<Parameter, Getter<S, ?>> constructorParameterGetterMap, org.sfm.map.GetterFactory<S, K> getterFactory) throws NoSuchMethodException {
		return  getInstantiator(source, target, propertyMappingsBuilder, constructorParameterGetterMap, getterFactory, true);
	}

	@SuppressWarnings("unchecked")
    public <S, T, K extends FieldKey<K>, D extends ColumnDefinition<K, D>> Instantiator<S,T> getInstantiator(Type source, Type target, PropertyMappingsBuilder<T, K, D> propertyMappingsBuilder, Map<Parameter, Getter<S, ?>> constructorParameterGetterMap, final org.sfm.map.GetterFactory<S, K> getterFactory,  boolean useAsmIfEnabled) throws NoSuchMethodException {

        if (propertyMappingsBuilder.isDirectProperty()) {
            Getter getter = propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, K, D>>() {
                public Getter getter;
                @Override
                public void handle(PropertyMapping<T, ?, K, D> propertyMapping) {
                    getter = getterFactory.newGetter(propertyMapping.getPropertyMeta().getType(), propertyMapping.getColumnKey());
                }
            }).getter;

            return new GetterInstantiator<S, T>(getter);

        }

		if (TypeHelper.isArray(target)) {
			return getArrayInstantiator(TypeHelper.toClass(TypeHelper.getComponentType(target)), propertyMappingsBuilder.forEachProperties(new CalculateMaxIndex<T, K, D>()).maxIndex + 1);
		} else {
			return getInstantiator(target, TypeHelper.toClass(source), propertyMappingsBuilder.getPropertyFinder().getEligibleInstantiatorDefinitions(), constructorParameterGetterMap,useAsmIfEnabled);
		}
	}

	@SuppressWarnings("unchecked")
	public <S, T> Instantiator<S, T> getInstantiator(Type target, final Class<?> source, List<InstantiatorDefinition> constructors, Map<Parameter, Getter<S, ?>> injections, boolean useAsmIfEnabled) throws SecurityException {
		final InstantiatorDefinition instantiatorDefinition = getSmallerConstructor(constructors);

		if (instantiatorDefinition == null) {
			throw new IllegalArgumentException("No constructor available for " + target);
		}
		Constructor<? extends T> constructor = (Constructor<? extends T>) instantiatorDefinition.getExecutable();
		
		if (asmFactory != null && Modifier.isPublic(constructor.getModifiers()) && useAsmIfEnabled) {
			try {
				return asmFactory.createInstantiator(source, instantiatorDefinition, injections);
			} catch (Exception e) {
				// fall back on reflection
			}
		}
		
		if (constructor.getParameterTypes().length == 0) {
			return new StaticConstructorInstantiator<S, T>(constructor, EMPTY_ARGS); 
		} else {
			return new InjectConstructorInstantiator<S, T>(instantiatorDefinition, injections);
		}
	}

	private InstantiatorDefinition getSmallerConstructor(final List<InstantiatorDefinition> constructors) {
        if (constructors == null) {
            return null;
        }

		InstantiatorDefinition selectedConstructor = null;
		
		for(InstantiatorDefinition c : constructors) {
			if (selectedConstructor == null || (c.getParameters().length < selectedConstructor.getParameters().length)) {
				selectedConstructor = c;
			}
		}
		
		return selectedConstructor;
	}

	public <S, T> Instantiator<S, T> getArrayInstantiator(final Class<?> elementType, final int length) {
		return new ArrayInstantiator<S, T>(elementType, length);
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
