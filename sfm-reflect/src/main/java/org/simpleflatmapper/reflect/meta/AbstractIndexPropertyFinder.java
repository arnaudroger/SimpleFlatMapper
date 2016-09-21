package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.util.Consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractIndexPropertyFinder<T> extends PropertyFinder<T> {
    protected final ClassMeta<T> classMeta;
    protected final List<IndexedElement<T, ?>> elements;
    private final Map<String, Integer> speculativeIndexes = new HashMap<String, Integer>();

    public AbstractIndexPropertyFinder(ClassMeta<T> classMeta) {
        this.elements = new ArrayList<IndexedElement<T, ?>>();
        this.classMeta = classMeta;
    }

    @Override
    protected void lookForProperties(
            PropertyNameMatcher propertyNameMatcher,
            MatchingProperties matchingProperties,
            PropertyMatchingScore score) {

        IndexedColumn indexedColumn = propertyNameMatcher.matchIndex();

        if (indexedColumn == null) {
            indexedColumn = extrapolateIndex(propertyNameMatcher);
        }

        if (indexedColumn == null) {
            indexedColumn = speculativeMatching(propertyNameMatcher);
        }

        if (indexedColumn == null || !isValidIndex(indexedColumn)) {
            // no index found
            return;
        }

        final IndexedElement<T, ?> indexedElement = getIndexedElement(indexedColumn);

        if (indexedElement.getElementClassMeta().isLeaf() || indexedColumn.getSubPropertyNameMatcher() == null) {
            matchingProperties.found(indexedElement.getPropertyMeta(), new Consumer() {
                @Override
                public void accept(Object o) {
                    indexedElement.addProperty(".");
                }
            }, score);
            return ;
        }

        final PropertyFinder<?> eltPropertyFinder = indexedElement.getPropertyFinder();

        if (eltPropertyFinder == null) {
            return;
        }

        final PropertyMeta<?, ?> eltProp = eltPropertyFinder.findProperty(indexedColumn.getSubPropertyNameMatcher());
        if (eltProp == null) {
            return;
        }

        SubPropertyMeta propertyMeta = new SubPropertyMeta(classMeta.getReflectionService(), indexedElement.getPropertyMeta(), eltProp);
        matchingProperties.found(propertyMeta, new Consumer() {
            @Override
            public void accept(Object o) {
                indexedElement.addProperty(eltProp);
            }
        }, score);
    }

    protected abstract boolean isValidIndex(IndexedColumn indexedColumn);

    protected abstract <E> IndexedElement<T,?> getIndexedElement(IndexedColumn indexedColumn);

    private IndexedColumn speculativeMatching(PropertyNameMatcher propertyNameMatcher) {
        // try to match against prefix
        PropertyNameMatch speculativeMatch = propertyNameMatcher.speculativeMatch();

        IndexedColumn indexedColumn = null;
        if (speculativeMatch != null) {
            Integer index = speculativeIndexes.get(speculativeMatch.getProperty());

            if (index == null) {
                indexedColumn = extrapolateIndex(speculativeMatch.getLeftOverMatcher());
                if (indexedColumn != null) {
                    speculativeIndexes.put(speculativeMatch.getProperty(), indexedColumn.getIndexValue());
                }
            } else {
                indexedColumn = new IndexedColumn(index, speculativeMatch.getLeftOverMatcher());
            }
        }
        return indexedColumn;
    }

    protected abstract IndexedColumn extrapolateIndex(PropertyNameMatcher propertyNameMatcher);

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return classMeta.getInstantiatorDefinitions();
    }
}
