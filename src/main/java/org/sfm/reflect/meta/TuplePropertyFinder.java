package org.sfm.reflect.meta;

import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.ConstructorParameter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TuplePropertyFinder<T> implements PropertyFinder<T> {

	private final TupleClassMeta<T> tupleClassMeta;
	private final Map<Integer, ConstructorPropertyMeta<T, ?>> properties = new HashMap<Integer,  ConstructorPropertyMeta<T, ?>>();
	private final Map<Integer, PropertyFinder<?>> subPropertyFinders = new HashMap<Integer, PropertyFinder<?>>();
	private final Map<String, Integer> assignedProperties = new HashMap<String, Integer>();
	private int maxIndex = -1;
	private List<ConstructorDefinition<T>> constructorDefinitions;


	public TuplePropertyFinder(TupleClassMeta<T> tupleClassMeta) {
		this.tupleClassMeta = tupleClassMeta;
		this.constructorDefinitions = tupleClassMeta.getConstructorDefinitions();
	}

	@Override
	public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {

		IndexedColumn indexedColumn = propertyNameMatcher.matchesIndex();

//		if (indexedColumn == null) {
//			indexedColumn = extrapolateIndex(propertyNameMatcher, indexedColumn);
//		}

		if (indexedColumn == null) {
			return null;
		}



		maxIndex = Math.max(indexedColumn.getIndexValue(),  maxIndex);

		ConstructorPropertyMeta<T, E> prop = gettConstructorPropertyMeta(indexedColumn);
		
		if (!indexedColumn.hasProperty()) {
			return prop;
		}


		PropertyFinder<?> propertyFinder = getPropertyFinder(indexedColumn, prop, tupleClassMeta.getReflectionService());
		
		PropertyMeta<?, ?> subProp = propertyFinder.findProperty(indexedColumn.getPropertyName());

		if (subProp == null) {
			return null;
		}

		String path = subProp.getPath();

		Integer lastValue = assignedProperties.get(path);

		if (lastValue == null) {
			lastValue = new Integer(indexedColumn.getIndexValue() + 1);
		} else {
			lastValue = Math.max(lastValue, indexedColumn.getIndexValue() + 1);
		}

		assignedProperties.put(path, lastValue);

		
		if (subProp != null) {
			return new SubPropertyMeta(tupleClassMeta.getReflectionService(), prop, subProp);
		}
		
		return null;
	}

	private <E> PropertyFinder<E> getPropertyFinder(IndexedColumn indexedColumn, ConstructorPropertyMeta<T, E> constructorPropertyMeta, ReflectionService reflectionService) {
		PropertyFinder<E> propertyFinder = (PropertyFinder<E>) subPropertyFinders.get(indexedColumn.getIndexValue());

		if (propertyFinder == null) {
			propertyFinder = (PropertyFinder<E>) reflectionService.getClassMeta(constructorPropertyMeta.getConstructorParameter().getResolvedType()).newPropertyFinder();
			subPropertyFinders.put(indexedColumn.getIndexValue(), propertyFinder);
		}
		return propertyFinder;
	}

	private <P> ConstructorPropertyMeta<T, P> gettConstructorPropertyMeta(IndexedColumn indexedColumn) {
		ConstructorPropertyMeta<T, P> prop = (ConstructorPropertyMeta<T, P>) properties.get(indexedColumn.getIndexValue());
		if (prop == null) {
			prop = new ConstructorPropertyMeta<T, P>("element" + indexedColumn.getIndexValue(),
					indexedColumn.getIndexName(), tupleClassMeta.getReflectionService(),
					new ConstructorParameter("element" + indexedColumn.getIndexValue(),
							Object.class,
							((ParameterizedType)tupleClassMeta.getType()).getActualTypeArguments()[indexedColumn.getIndexValue() - 1]));
			properties.put(indexedColumn.getIndexValue(), prop);
		}
		return prop;
	}

	private IndexedColumn extrapolateIndex(PropertyNameMatcher propertyNameMatcher, IndexedColumn indexedColumn) {
		return null;
	}

	@Override
	public PropertyMeta<T, ?> findProperty(String propertyName) {
		return findProperty(new PropertyNameMatcher(propertyName));
	}

	@Override
	public List<ConstructorDefinition<T>> getEligibleConstructorDefinitions() {
		return constructorDefinitions;
	}

	@Override
	public Class<?> getClassToInstantiate() {
		return TypeHelper.toClass(tupleClassMeta.getType());
	}

	public int getLength() {
		return maxIndex + 1;
	}

}
