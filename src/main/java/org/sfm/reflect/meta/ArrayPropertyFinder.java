package org.sfm.reflect.meta;

import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.BooleanSupplier;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ArrayPropertyFinder<T, E> extends AbstractIndexPropertyFinder<T> {

	private final List<IndexedElement<T, E>> elements = new ArrayList<IndexedElement<T, E>>();


    public ArrayPropertyFinder(ArrayClassMeta<T, E> arrayClassMeta) {
        super(arrayClassMeta);
    }

    @Override
    protected IndexedElement<T, E> getIndexedElement(IndexedColumn indexedColumn) {
        while (elements.size() <= indexedColumn.getIndexValue()) {
            elements.add(new IndexedElement<T, E>(newElementPropertyMeta(elements.size(), "element" + elements.size()), ((ArrayClassMeta<T, E>)classMeta).getElementClassMeta()));
        }

        return elements.get(indexedColumn.getIndexValue());
	}

    private PropertyMeta<T, E> newElementPropertyMeta(int index, String name) {
        ArrayClassMeta<T, E> arrayClassMeta = (ArrayClassMeta<T, E>) classMeta;
        if (arrayClassMeta.isArray()) {
            return new ArrayElementPropertyMeta<T, E>(name,
                    arrayClassMeta.getReflectionService(), index, arrayClassMeta);
        } else {
            return new ListElementPropertyMeta<T, E>(name,
                    arrayClassMeta.getReflectionService(), index, arrayClassMeta, new BooleanSupplier() {

                @Override
                public boolean getAsBoolean() {
                    return elements.size() == 1;
                }
            });
        }
    }

    @Override
    protected IndexedColumn extrapolateIndex(PropertyNameMatcher propertyNameMatcher) {
        final ClassMeta<E> elementClassMeta = ((ArrayClassMeta)classMeta).getElementClassMeta();

        PropertyMeta<E, Object> property;

        if (elementClassMeta != null) {
            property = elementClassMeta.newPropertyFinder().findProperty(propertyNameMatcher);
            if (property != null) {

                for (int i = 0; i < elements.size(); i++) {
                    IndexedElement element = elements.get(i);
                    if (!element.hasProperty(property)) {
                        return new IndexedColumn(i, propertyNameMatcher);
                    }
                }

                return new IndexedColumn(elements.size(), propertyNameMatcher);
            }
        } else {
            return new IndexedColumn(elements.size(), null);
        }
		return null;
	}

    @Override
    protected boolean isValidIndex(IndexedColumn indexedColumn) {
        return indexedColumn.getIndexValue() >= 0;
    }

	@Override
	public List<ConstructorDefinition<T>> getEligibleConstructorDefinitions() {
		if (List.class.isAssignableFrom(TypeHelper.toClass(classMeta.getType()))) {
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
    public <E> ConstructorPropertyMeta<T, E> findConstructor(ConstructorDefinition<T> constructorDefinition) {
        return null;
    }
}
