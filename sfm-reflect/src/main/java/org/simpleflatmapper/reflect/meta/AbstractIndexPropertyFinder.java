package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractIndexPropertyFinder<T> extends PropertyFinder<T> {
    protected final ClassMeta<T> classMeta;
    protected final List<IndexedElement<T, ?>> elements;

    public AbstractIndexPropertyFinder(ClassMeta<T> classMeta) {
        this.elements = new ArrayList<IndexedElement<T, ?>>();
        this.classMeta = classMeta;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void lookForProperties(
            PropertyNameMatcher propertyNameMatcher,
            FoundProperty<T> matchingProperties,
            PropertyMatchingScore score, boolean allowSelfReference) {


        IndexedColumn indexedColumn = propertyNameMatcher.matchIndex();
        if (indexedColumn != null) {
            lookForAgainstColumn(indexedColumn, matchingProperties, score);
        } else {
            extrapolateIndex(propertyNameMatcher, matchingProperties, score.decrease(1));
            speculativeMatching(propertyNameMatcher, matchingProperties, score.shift());
        }

    }

    @SuppressWarnings("unchecked")
    protected void lookForAgainstColumn(IndexedColumn indexedColumn, final FoundProperty<T> matchingProperties, PropertyMatchingScore score) {

        if (indexedColumn == null || !isValidIndex(indexedColumn)) {
            // no index found
            return;
        }

        final IndexedElement<T, ?> indexedElement = getIndexedElement(indexedColumn);

        if (indexedColumn.getSubPropertyNameMatcher() == null) {
            matchingProperties.found(indexedElement.getPropertyMeta(), new Runnable() {
                @Override
                public void run() {
                    if (!indexedElement.hasProperty(SelfPropertyMeta.PROPERTY_PATH)) {
                        indexedElement.addProperty(SelfPropertyMeta.PROPERTY_PATH);
                    }
                }
            }, score);
            return ;
        }

        final PropertyFinder<?> eltPropertyFinder = indexedElement.getPropertyFinder();

        if (eltPropertyFinder == null) {
            return;
        }

        eltPropertyFinder.lookForProperties(indexedColumn.getSubPropertyNameMatcher(),
                new FoundProperty() {
                    @Override
                    public void found(final PropertyMeta propertyMeta, final Runnable selectionCallback, final PropertyMatchingScore score) {
                        PropertyMeta subProperty;
                        if (propertyMeta.isSelf()) {
                            subProperty = indexedElement.getPropertyMeta();
                        } else {
                            subProperty = new SubPropertyMeta(classMeta.getReflectionService(), indexedElement.getPropertyMeta(), propertyMeta);
                        }


                        matchingProperties.found(subProperty, new Runnable() {
                            @Override
                            public void run() {
                                selectionCallback.run();
                                indexedElement.addProperty(propertyMeta);
                            }
                        }, score);
                    }
                }, score, true);
    }


    private void speculativeMatching(PropertyNameMatcher propertyNameMatcher, FoundProperty foundProperty, PropertyMatchingScore score) {
        // try to match against prefix
        PropertyNameMatch speculativeMatch = propertyNameMatcher.speculativeMatch();

        if (speculativeMatch != null) {
                extrapolateIndex(speculativeMatch.getLeftOverMatcher(), foundProperty, score);
        }
    }

    protected abstract boolean isValidIndex(IndexedColumn indexedColumn);

    protected abstract <E> IndexedElement<T,?> getIndexedElement(IndexedColumn indexedColumn);

    protected abstract void extrapolateIndex(PropertyNameMatcher propertyNameMatcher, FoundProperty foundProperty, PropertyMatchingScore score);

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return classMeta.getInstantiatorDefinitions();
    }
}
