package org.simpleflatmapper.reflect.meta;


import org.simpleflatmapper.util.Consumer;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class TuplePropertyFinder<T> extends AbstractIndexPropertyFinder<T> {


    public TuplePropertyFinder(final TupleClassMeta<T> tupleClassMeta, boolean selfScoreFullName) {
        super(tupleClassMeta, selfScoreFullName);

        tupleClassMeta.forEachProperties(new Consumer<PropertyMeta<T, ?>>() {
            int i = 0;
            @Override
            public void accept(PropertyMeta<T, ?> propertyMeta) {
                elements.put(i++, newIndexedElement(tupleClassMeta, propertyMeta));
            }
        });
    }

    private <E> IndexedElement<T, E> newIndexedElement(TupleClassMeta<T> tupleClassMeta, PropertyMeta<T, E> prop) {
        ClassMeta<E> classMeta = tupleClassMeta.getReflectionService().getClassMeta(prop.getPropertyType());
        return new IndexedElement<T, E>(prop, classMeta);
    }

    @Override
    protected boolean isValidIndex(IndexedColumn indexedColumn) {
        return indexedColumn.getIndexValue() < elements.size();
    }

    @Override
    protected IndexedElement<T, ?> getIndexedElement(IndexedColumn indexedColumn) {
        return elements.get(indexedColumn.getIndexValue());
    }

    @Override
    protected void extrapolateIndex(final PropertyNameMatcher propertyNameMatcher, Object[] properties, final FoundProperty<T> foundProperty, PropertyMatchingScore score, PropertyFinderTransformer propertyFinderTransformer, TypeAffinityScorer typeAffinityScorer, PropertyFilter propertyFilter, ShortCircuiter shortCircuiter) {
        for (int i = 0; i < elements.size(); i++) {
            final IndexedElement element = elements.get(i);

            if (element.getElementClassMeta() != null) {
                PropertyFinder propertyFinder = element.getPropertyFinder();
                propertyFinderTransformer.apply(propertyFinder).lookForProperties(propertyNameMatcher, properties, new FoundProperty() {
                    @Override
                    public void found(final PropertyMeta propertyMeta, final Runnable selectionCallback, final PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {

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
                            }, score, typeAffinityScorer);
                        }
                    }
                }, score.tupleIndex(element.getPropertyMeta(),propertyNameMatcher, new IndexedColumn(i, null, 0)), true, propertyFinderTransformer, typeAffinityScorer, propertyFilter, shortCircuiter);

            }
        }
    }

    @Override
    protected boolean scoreFullName() {
        return true;
    }

    @Override
    protected boolean indexMatches(PropertyMeta<T, ?> propertyMeta, PropertyMeta<?, ?> owner) {
        if (owner == propertyMeta) return true;
        if (propertyMeta instanceof  ConstructorPropertyMeta && owner instanceof ConstructorPropertyMeta) {
            return ((ConstructorPropertyMeta) propertyMeta).getParameter().equals(((ConstructorPropertyMeta) owner).getParameter());
        }
        return false;
    }

    @Override
    protected PropertyFinder<?> registerProperty(SubPropertyMeta<?, ?, ?> subPropertyMeta) {
        throw new IllegalArgumentException("Cannot add element to tuples " + subPropertyMeta);
    }
}