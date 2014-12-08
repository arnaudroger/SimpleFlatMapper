package org.sfm.reflect.meta;

import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.ConstructorParameter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TuplePropertyFinder<T> implements PropertyFinder<T> {

	private final TupleClassMeta<T> tupleClassMeta;

	private final List<IndexedElement<T, ?>> elements;

	private List<ConstructorDefinition<T>> constructorDefinitions;


	public TuplePropertyFinder(TupleClassMeta<T> tupleClassMeta) {
		this.tupleClassMeta = tupleClassMeta;
		this.constructorDefinitions = tupleClassMeta.getConstructorDefinitions();

		this.elements = new ArrayList<IndexedElement<T, ?>>();

		for(int i = 0; i < tupleClassMeta.getTupleSize(); i++) {
			elements.add(newIndexedElement(tupleClassMeta, i));
		}
	}

	private <E> IndexedElement<T, E> newIndexedElement(TupleClassMeta<T> tupleClassMeta, int i) {
		Type resolvedType = ((ParameterizedType) tupleClassMeta.getType()).getActualTypeArguments()[i];
		ConstructorPropertyMeta<T, E> prop =
				new ConstructorPropertyMeta<T, E>("element" + (i),
					"element" + (i), 	tupleClassMeta.getReflectionService(),
					new ConstructorParameter("element" + (i), Object.class, resolvedType));
		ClassMeta<E> classMeta = tupleClassMeta.getReflectionService().getClassMeta(resolvedType);
		return new IndexedElement<T, E>(prop, classMeta);
	}

	@Override
	public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {

		IndexedColumn indexedColumn = propertyNameMatcher.matchesIndex();

		if (indexedColumn == null) {
			indexedColumn = extrapolateIndex(propertyNameMatcher);
		}

		if (indexedColumn == null || calculateTupleIndex(indexedColumn) >= elements.size()) {
			return null;
		}

		IndexedElement indexedElement = elements.get(calculateTupleIndex(indexedColumn));

		if (!indexedColumn.hasProperty()) {
			return indexedElement.getPropertyMeta();
		}

		PropertyFinder<?> propertyFinder = indexedElement.getPropertyFinder();
		if (propertyFinder == null) {
			return null;
		}

		PropertyMeta<?, ?> subProp = propertyFinder.findProperty(indexedColumn.getPropertyName());
		if (subProp == null) {
			return null;
		}

		indexedElement.addProperty(subProp);

		return new SubPropertyMeta(tupleClassMeta.getReflectionService(), indexedElement.getPropertyMeta(), subProp);
	}

	private int calculateTupleIndex(IndexedColumn indexedColumn) {
		return indexedColumn.getIndexValue();
	}


	private IndexedColumn extrapolateIndex(PropertyNameMatcher propertyNameMatcher) {
		for(int i = 0; i < elements.size(); i++) {
			IndexedElement element = elements.get(i);

			if (element.getElementClassMeta() != null) {
				PropertyFinder<?> pf = element.getPropertyFinder();
				PropertyMeta<?, Object> property = pf.findProperty(propertyNameMatcher);
				if (property != null) {
					if (!element.hasProperty(property)) {
						return new IndexedColumn("element" + (i), i , propertyNameMatcher.getColumn());
					}
				}

			}
		}
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

}
