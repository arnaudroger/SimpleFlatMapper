package org.sfm.reflect.meta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.*;

import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.TypeHelper;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ArrayPropertyFinder<T, E> implements PropertyFinder<T> {

	private final ArrayClassMeta<T, E> arrayClassMeta;
	private final Map<Integer, ArrayElementPropertyMeta<T, E>> properties = new HashMap<Integer, ArrayElementPropertyMeta<T, E>>();
	private final Map<Integer, PropertyFinder<E>> subPropertyFinders = new HashMap<Integer, PropertyFinder<E>>();
	private final Map<String, Integer> assignedProperties = new HashMap<String, Integer>();
	private int maxIndex = -1;
	
	
	public ArrayPropertyFinder(ArrayClassMeta<T, E> arrayClassMeta) {
		this.arrayClassMeta = arrayClassMeta;
	}

	@Override
	public PropertyMeta<T, ?> findProperty(PropertyNameMatcher propertyNameMatcher) {

		IndexedColumn indexedColumn = propertyNameMatcher.matchesIndex();

		if (indexedColumn == null) {
			indexedColumn = extrapolateIndex(propertyNameMatcher, indexedColumn);
		}

		if (indexedColumn == null) {
			return null;
		}

		maxIndex = Math.max(indexedColumn.getIndexValue(),  maxIndex);

		ArrayElementPropertyMeta<T, E> prop = properties.get(indexedColumn.getIndexValue());
		if (prop == null) {
			prop = new ArrayElementPropertyMeta<T, E>(indexedColumn.getIndexName(), indexedColumn.getIndexName() , arrayClassMeta.getReflectionService(), indexedColumn.getIndexValue(), arrayClassMeta);
			properties.put(indexedColumn.getIndexValue(), prop);
		}
		
		if (!indexedColumn.hasProperty()) {
			return prop;
		}
		

		PropertyFinder<E> propertyFinder = subPropertyFinders.get(indexedColumn.getIndexValue());
		
		if (propertyFinder == null) {
			propertyFinder = arrayClassMeta.getElementClassMeta().newPropertyFinder();
			subPropertyFinders.put(indexedColumn.getIndexValue(), propertyFinder);
		}
		
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
			return new SubPropertyMeta(arrayClassMeta.getReflectionService(), prop, subProp);
		}
		
		return null;
	}

	private IndexedColumn extrapolateIndex(PropertyNameMatcher propertyNameMatcher, IndexedColumn indexedColumn) {
		PropertyMeta<E, ?> property = arrayClassMeta.getElementClassMeta().newPropertyFinder().findProperty(propertyNameMatcher);
		if (property != null) {

            String path = property.getPath();

            Integer lastValue = assignedProperties.get(path);

            if (lastValue == null) {
                lastValue = new Integer(0);
            }

            indexedColumn = new IndexedColumn("elt" + lastValue, lastValue, propertyNameMatcher.getColumn());
        }
		return indexedColumn;
	}

	@Override
	public PropertyMeta<T, ?> findProperty(String propertyName) {
		return findProperty(new PropertyNameMatcher(propertyName));
	}

	@Override
	public List<ConstructorDefinition<T>> getEligibleConstructorDefinitions() {
		if (List.class.isAssignableFrom(TypeHelper.toClass(this.arrayClassMeta.getType()))) {
			try {
				return Arrays.asList(new ConstructorDefinition<T>((Constructor<? extends T>) ArrayList.class.getConstructor()));
			} catch (NoSuchMethodException e) {
				throw new Error("Unexpected error " + e, e);
			}
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public Class<?> getClassToInstantiate() {
		return TypeHelper.toClass(arrayClassMeta.getType());
	}

	public Type getElementType() {
		return arrayClassMeta.getElementTarget();
	}
	
	public int getLength() {
		return maxIndex + 1;
	}

}
