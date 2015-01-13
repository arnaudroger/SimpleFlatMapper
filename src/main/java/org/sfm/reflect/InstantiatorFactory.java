package org.sfm.reflect;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.impl.CalculateMaxIndex;
import org.sfm.map.impl.FieldKey;
import org.sfm.map.impl.PropertyMappingsBuilder;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.InjectConstructorInstantiator;
import org.sfm.reflect.impl.StaticConstructorInstantiator;

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


	public <S, T, K extends FieldKey<K>, D extends ColumnDefinition<K>> Instantiator<S,T> getInstantiator(Type source, Type target, PropertyMappingsBuilder<T, K, D> propertyMappingsBuilder, Map<ConstructorParameter, Getter<S, ?>> constructorParameterGetterMap) throws NoSuchMethodException {
		return  getInstantiator(source, target, propertyMappingsBuilder, constructorParameterGetterMap, true);
	}

	public <S, T, K extends FieldKey<K>, D extends ColumnDefinition<K>> Instantiator<S,T> getInstantiator(Type source, Type target, PropertyMappingsBuilder<T, K, D> propertyMappingsBuilder, Map<ConstructorParameter, Getter<S, ?>> constructorParameterGetterMap, boolean useAsmIfEnabled) throws NoSuchMethodException {
		if (TypeHelper.isArray(target)) {
			return getArrayInstantiator(TypeHelper.toClass(TypeHelper.getComponentType(target)), propertyMappingsBuilder.forEachProperties(new CalculateMaxIndex<T, K, D>()).maxIndex + 1);
		} else {
			return getInstantiator(target, TypeHelper.toClass(source), propertyMappingsBuilder.getPropertyFinder().getEligibleConstructorDefinitions(), constructorParameterGetterMap,useAsmIfEnabled);
		}
	}

	public <S, T> Instantiator<S, T> getInstantiator(Type target, final Class<?> source, List<ConstructorDefinition<T>> constructors, Map<ConstructorParameter, Getter<S, ?>> injections, boolean useAsmIfEnabled) throws NoSuchMethodException, SecurityException {
		final ConstructorDefinition<T> constructorDefinition = getSmallerConstructor(constructors);

		if (constructorDefinition == null) {
			throw new IllegalArgumentException("No constructor available for " + target);
		}
		Constructor<? extends T> constructor = constructorDefinition.getConstructor();
		
		if (asmFactory != null && Modifier.isPublic(constructor.getModifiers()) && useAsmIfEnabled) {
			try {
				return asmFactory.createInstatiantor(source, constructorDefinition, injections);
			} catch (Exception e) {
				// fall back on reflection
			}
		}
		
		constructor.setAccessible(true);
		
		if (constructor.getParameterTypes().length == 0) {
			return new StaticConstructorInstantiator<S, T>(constructor, EMPTY_ARGS); 
		} else {
			return new InjectConstructorInstantiator<S, T>(constructorDefinition, injections);
		}
	}

	private <T> ConstructorDefinition<T> getSmallerConstructor(final List<ConstructorDefinition<T>> constructors) {
		ConstructorDefinition<T> selectedConstructor = null;
		
		for(ConstructorDefinition<T> c : constructors) {
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
