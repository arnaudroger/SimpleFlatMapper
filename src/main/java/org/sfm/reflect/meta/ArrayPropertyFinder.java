package org.sfm.reflect.meta;

import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ArrayPropertyFinder<T, E> implements PropertyFinder<T> {

	private final ArrayClassMeta<T, E> arrayClassMeta;

	private final List<IndexedElement<T, E>> elements = new ArrayList<IndexedElement<T, E>>();
	
	
	public ArrayPropertyFinder(ArrayClassMeta<T, E> arrayClassMeta) {
		this.arrayClassMeta = arrayClassMeta;
	}

	@Override
	public PropertyMeta<T, ?> findProperty(PropertyNameMatcher propertyNameMatcher) {

		IndexedColumn indexedColumn = propertyNameMatcher.matchesIndex();

		if (indexedColumn == null) {
			indexedColumn = extrapolateIndex(propertyNameMatcher);
		}

		if (indexedColumn == null) {
			return null;
		}

		IndexedElement<T, E> indexedElement = getIndexedElement(indexedColumn);

		if (!indexedColumn.hasSubProperty()) {
			return indexedElement.getPropertyMeta();
		}

		PropertyFinder<?> propertyFinder = indexedElement.getPropertyFinder();

		if (propertyFinder == null) {
			return null;
		}

		PropertyMeta<?, ?> subProp = propertyFinder.findProperty(indexedColumn.getSubPropertyNameMatcher());
		if (subProp == null) {
			return null;
		}

		indexedElement.addProperty(subProp);

		return new SubPropertyMeta(arrayClassMeta.getReflectionService(), indexedElement.getPropertyMeta(), subProp);
	}

	private IndexedElement<T, E> getIndexedElement(IndexedColumn indexedColumn) {

		while (elements.size() <= indexedColumn.getIndexValue()) {
			ArrayElementPropertyMeta<T, E> arrayElementPropertyMeta =
					new ArrayElementPropertyMeta<T, E>("element" + elements.size(), "element" + elements.size(),
							arrayClassMeta.getReflectionService(), elements.size(), arrayClassMeta);
			elements.add(new IndexedElement<T, E>(arrayElementPropertyMeta, arrayClassMeta.getElementClassMeta()));
		}

		return elements.get(indexedColumn.getIndexValue());
	}

	private IndexedColumn extrapolateIndex(PropertyNameMatcher propertyNameMatcher) {
		PropertyMeta<E, Object> property = arrayClassMeta.getElementClassMeta().newPropertyFinder().findProperty(propertyNameMatcher);
		if (property != null) {

			for (int i = 0; i < elements.size(); i++) {
				IndexedElement element = elements.get(i);
				if (!element.hasProperty(property)) {
					return new IndexedColumn("element" + i, i, propertyNameMatcher);
				}
			}

			return new IndexedColumn("element" + elements.size(), elements.size(), propertyNameMatcher);
		}
		return null;
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

	public int getLength() {
		return elements.size();
	}

}
