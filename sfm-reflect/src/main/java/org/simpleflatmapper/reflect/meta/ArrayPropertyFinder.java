package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.util.BooleanSupplier;
import org.simpleflatmapper.util.Predicate;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ArrayPropertyFinder<T, E> extends AbstractIndexPropertyFinder<T> {


    public ArrayPropertyFinder(ArrayClassMeta<T, E> arrayClassMeta, Predicate<PropertyMeta<?, ?>> propertyFilter, boolean selfScoreFullName) {
        super(arrayClassMeta, propertyFilter, selfScoreFullName);
    }

    @Override
    protected IndexedElement<T, E> getIndexedElement(IndexedColumn indexedColumn) {
        while (elements.size() <= indexedColumn.getIndexValue()) {
            elements.add(new IndexedElement<T, E>(
                    newElementPropertyMeta(elements.size(), "element" + elements.size()), ((ArrayClassMeta<T, E>)classMeta).getElementClassMeta(),
                    propertyFilter));
        }

        return (IndexedElement<T, E>) elements.get(indexedColumn.getIndexValue());
	}

    private PropertyMeta<T, E> newElementPropertyMeta(int index, String name) {
        ArrayClassMeta<T, E> arrayClassMeta = (ArrayClassMeta<T, E>) classMeta;
        BooleanSupplier appendSetter = new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                for(int i = 1; i < elements.size(); i++) {
                    if (elements.get(i).hasAnyProperty()) {
                        return false;
                    }
                }
                return true;
            }
        };
        return new ArrayElementPropertyMeta<T, E>(name,
                classMeta.getType(), arrayClassMeta.getReflectionService(), index, arrayClassMeta,
                arrayClassMeta.<T, E>newSetterFactory(appendSetter), arrayClassMeta.<T, E>newGetterFactory());
    }

    @Override
    protected void extrapolateIndex(PropertyNameMatcher propertyNameMatcher, Object[] properties, FoundProperty foundProperty, PropertyMatchingScore score, PropertyFinderTransformer propertyFinderTransformer, TypeAffinityScorer typeAffinityScorer) {
        final ClassMeta<E> elementClassMeta = ((ArrayClassMeta)classMeta).getElementClassMeta();

        // all element has same type so check if can find any property matching
        PropertyMeta<E, ?> property =
                elementClassMeta.newPropertyFinder(propertyFilter).findProperty(propertyNameMatcher, properties, typeAffinityScorer);

        if (property != null) {
            for (int i = 0; i < elements.size(); i++) {
                IndexedElement element = elements.get(i);
                ExtrapolateFoundProperty<T> matchingProperties = new ExtrapolateFoundProperty<T>(element, foundProperty);
                lookForAgainstColumn(new IndexedColumn(i, propertyNameMatcher), properties, matchingProperties, score.speculativeArrayIndex(i), propertyFinderTransformer, typeAffinityScorer);
                if (matchingProperties.hasFound()) {
                    return;
                }
            }

            int index = elements.size();
            lookForAgainstColumn(new IndexedColumn(index,  propertyNameMatcher), properties, foundProperty, score.speculativeArrayIndex(index), propertyFinderTransformer, typeAffinityScorer);
        }
	}

    @Override
    protected boolean indexMatches(PropertyMeta<T, ?> propertyMeta, PropertyMeta<?, ?> owner) {
        if (owner == propertyMeta) return true;
        if (propertyMeta instanceof  ArrayElementPropertyMeta && owner instanceof ArrayElementPropertyMeta) {
            return ((ArrayElementPropertyMeta) propertyMeta).getIndex() == ((ArrayElementPropertyMeta) owner).getIndex();
        }
        return false;
    }

    @Override
    protected PropertyFinder<?> registerProperty(SubPropertyMeta<?, ?, ?> subPropertyMeta) {
        PropertyMeta<?, ?> ownerProperty = subPropertyMeta.getOwnerProperty();
        
        if (ownerProperty instanceof  ArrayElementPropertyMeta) {
            ArrayElementPropertyMeta arrayElementPropertyMeta = (ArrayElementPropertyMeta) ownerProperty;
            IndexedElement<T, E> indexedElement = getIndexedElement(new IndexedColumn(arrayElementPropertyMeta.getIndex(), null));
            return indexedElement.getPropertyFinder();
        }
        
        throw new IllegalArgumentException("Illegal owner expected ArrayElementPropertyMeta got " + subPropertyMeta);
    }

    @Override
    protected boolean isValidIndex(IndexedColumn indexedColumn) {
        return indexedColumn.getIndexValue() >= 0;
    }

    private static class ExtrapolateFoundProperty<T> implements FoundProperty<T> {
        private final IndexedElement element;
        private final FoundProperty foundProperty;
        private boolean found;

        public ExtrapolateFoundProperty(IndexedElement element, FoundProperty foundProperty) {
            this.element = element;
            this.foundProperty = foundProperty;
        }

        @Override
        public <P extends PropertyMeta<T, ?>> void found(P propertyMeta, Runnable selectionCallback, PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {
            String pathCheck;

            if (propertyMeta instanceof ArrayElementPropertyMeta) {
                pathCheck = SelfPropertyMeta.PROPERTY_PATH;
            } else if (propertyMeta.isSubProperty()) {
                pathCheck = ((SubPropertyMeta)propertyMeta).getSubProperty().getPath();
            } else {
                throw new IllegalArgumentException("Excepted match " + propertyMeta);
            }

            if (!element.hasProperty(pathCheck)) {
                foundProperty.found(propertyMeta, selectionCallback, score, typeAffinityScorer);
                this.found = true;
            }
        }

        public boolean hasFound() {
            return found;
        }
    }
}
