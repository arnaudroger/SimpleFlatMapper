package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractIndexPropertyFinder<T> extends PropertyFinder<T> {
    protected final ClassMeta<T> classMeta;
    protected final List<IndexedElement<T, ?>> elements;

    public AbstractIndexPropertyFinder(ClassMeta<T> classMeta, Predicate<PropertyMeta<?, ?>> propertyFilter) {
        super(propertyFilter);
        this.elements = new ArrayList<IndexedElement<T, ?>>();
        this.classMeta = classMeta;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void lookForProperties(
            PropertyNameMatcher propertyNameMatcher,
            FoundProperty<T> matchingProperties,
            PropertyMatchingScore score,
            boolean allowSelfReference,
            PropertyFinderTransformer propertyFinderTransformer) {


        IndexedColumn indexedColumn = propertyNameMatcher.matchIndex();
        if (indexedColumn != null) {
            lookForAgainstColumn(indexedColumn, matchingProperties, score.index(indexedColumn.getIndexValue()), propertyFinderTransformer);
        } else {
            extrapolateIndex(propertyNameMatcher, matchingProperties, score.speculative(), propertyFinderTransformer);
            speculativeMatching(propertyNameMatcher, matchingProperties, score.speculative(), propertyFinderTransformer);
        }
    }

    @SuppressWarnings("unchecked")
    protected void lookForAgainstColumn(IndexedColumn indexedColumn, final FoundProperty<T> matchingProperties, PropertyMatchingScore score,
                                        PropertyFinderTransformer propertyFinderTransformer) {

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
            }, score.self(indexedElement.getElementClassMeta(), indexedColumn.getIndexProperty()));
            return ;
        }

        final PropertyFinder<?> eltPropertyFinder = indexedElement.getPropertyFinder();

        if (eltPropertyFinder == null) {
            return;
        }

        propertyFinderTransformer.apply(eltPropertyFinder)
                .lookForProperties(indexedColumn.getSubPropertyNameMatcher(),
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
                }, score.matches(indexedColumn.getIndexProperty()), true, propertyFinderTransformer);
    }


    private void speculativeMatching(PropertyNameMatcher propertyNameMatcher, FoundProperty foundProperty, PropertyMatchingScore score, PropertyFinderTransformer propertyFinderTransformer) {
        // try to match against prefix
        PropertyNameMatch speculativeMatch = propertyNameMatcher.speculativeMatch();

        if (speculativeMatch != null) {
                extrapolateIndex(speculativeMatch.getLeftOverMatcher(), foundProperty, score.speculative(), propertyFinderTransformer);
        }
    }

    protected abstract boolean isValidIndex(IndexedColumn indexedColumn);

    protected abstract <E> IndexedElement<T,?> getIndexedElement(IndexedColumn indexedColumn);

    protected abstract void extrapolateIndex(PropertyNameMatcher propertyNameMatcher, FoundProperty foundProperty, PropertyMatchingScore score, PropertyFinderTransformer propertyFinderTransformer);

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return classMeta.getInstantiatorDefinitions();
    }
    

    @Override
    public Type getOwnerType() {
        return classMeta.getType();
    }

    @Override
    public PropertyFinder<?> getSubPropertyFinder(PropertyMeta<?, ?> owner) {
        PropertyFinder<?> ie = lookForPropertyFinder(owner);
        if (ie != null) return ie;

        throw new IllegalArgumentException("Unexpected owner " + owner);
    }

    private PropertyFinder<?> lookForPropertyFinder(PropertyMeta<?, ?> owner) {
        for(IndexedElement<T, ?> ie : elements) {
            if (indexMatches(ie.getPropertyMeta(), owner)) {
                return ie.getPropertyFinder();
            }
        }
        return null;
    }

    protected abstract boolean indexMatches(PropertyMeta<T, ?> propertyMeta, PropertyMeta<?, ?> owner);

    @Override
    public PropertyFinder<?> getOrCreateSubPropertyFinder(SubPropertyMeta<?, ?, ?> subPropertyMeta) {
        PropertyMeta<?, ?> ownerProperty = subPropertyMeta.getOwnerProperty();

        PropertyFinder<?> ie = lookForPropertyFinder(ownerProperty);
        
        if (ie != null) return ie;
        
        
        return registerProperty(subPropertyMeta);
        
    }

    protected abstract PropertyFinder<?> registerProperty(SubPropertyMeta<?, ?, ?> subPropertyMeta);

}
