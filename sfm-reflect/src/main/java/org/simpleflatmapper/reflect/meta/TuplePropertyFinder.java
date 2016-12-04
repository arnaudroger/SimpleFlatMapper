package org.simpleflatmapper.reflect.meta;


import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TuplePropertyFinder<T> extends AbstractIndexPropertyFinder<T> {


    public TuplePropertyFinder(final TupleClassMeta<T> tupleClassMeta, Predicate<PropertyMeta<?, ?>> propertyFilter) {
        super(tupleClassMeta, propertyFilter);

        tupleClassMeta.forEachProperties(new Consumer<PropertyMeta<T, ?>>() {
            @Override
            public void accept(PropertyMeta<T, ?> propertyMeta) {
                elements.add(newIndexedElement(tupleClassMeta, propertyMeta));
            }
        });
	}

	private <E> IndexedElement<T, E> newIndexedElement(TupleClassMeta<T> tupleClassMeta,  PropertyMeta<T, E> prop) {
		ClassMeta<E> classMeta = tupleClassMeta.getReflectionService().getClassMeta(prop.getPropertyType());
		return new IndexedElement<T, E>(prop, classMeta, propertyFilter);
	}

    @Override
    protected boolean isValidIndex(IndexedColumn indexedColumn) {
        return indexedColumn.getIndexValue() < elements.size();
    }
    @Override
    protected IndexedElement<T, ?> getIndexedElement(IndexedColumn indexedColumn) {
        return elements.get(indexedColumn.getIndexValue());
    }

    protected void extrapolateIndex(final PropertyNameMatcher propertyNameMatcher, final FoundProperty foundProperty, PropertyMatchingScore score, PropertyFinderTransformer propertyFinderTransformer) {
        for(int i = 0; i < elements.size(); i++) {
            final IndexedElement element = elements.get(i);

            if (element.getElementClassMeta() != null) {
                propertyFinderTransformer.apply(element.getPropertyFinder()).lookForProperties(propertyNameMatcher, new FoundProperty() {
                    @Override
                    public void found(final PropertyMeta propertyMeta, final Runnable selectionCallback, final PropertyMatchingScore score) {

                        if (!element.hasProperty(propertyMeta)) {
                            PropertyMeta subProperty;
                            if (propertyMeta.isSelf()) {
                                subProperty = element.getPropertyMeta();
                            } else {
                                subProperty = new SubPropertyMeta(classMeta.getReflectionService(), element.getPropertyMeta(), propertyMeta);
                            }

                            foundProperty.found(subProperty, new Runnable() {
                                @Override
                                public void run() {
                                    element.addProperty(propertyMeta);
                                    selectionCallback.run();
                                }
                            }, score);
                        }
                    }
                }, score, true, propertyFinderTransformer);

            }
            score = score.decrease(1);
        }
    }

    @Override
    public PropertyFinder<?> getSubPropertyFinder(String name) {
        return null;
    }
}
