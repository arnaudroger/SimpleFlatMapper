package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.util.List;

public class PassThroughPropertyFinder<T, V> extends PropertyFinder<T> {


    private final PassThroughClassMeta<T, V> passThroughClassMeta;
    private final PropertyFinder<V> propertyFinder;
    private final ClassMeta<V> innerMeta;

    @Deprecated
    public PassThroughPropertyFinder(PassThroughClassMeta<T, V> passThroughClassMeta, boolean selfScoreFullName) {
        this(passThroughClassMeta);
    }
    public PassThroughPropertyFinder(PassThroughClassMeta<T, V> passThroughClassMeta) {
        super();
        this.passThroughClassMeta = passThroughClassMeta;
        innerMeta = passThroughClassMeta.getInnerMeta();
        this.propertyFinder = innerMeta != null ? innerMeta.newPropertyFinder() : null;
	}


    @SuppressWarnings("unchecked")
    @Override
    public void lookForProperties(
            PropertyNameMatcher propertyNameMatcher,
            Object[] properties, final FoundProperty<T> matchingProperties,
            PropertyMatchingScore score, boolean allowSelfReference, PropertyFinderTransformer propertyFinderTransformer, TypeAffinityScorer typeAffinityScorer, PropertyFilter propertyFilter, ShortCircuiter shortCircuiter) {
        propertyFinderTransformer.apply(propertyFinder).lookForProperties(propertyNameMatcher, properties, new FoundProperty<V>() {
            @Override
            public <P extends PropertyMeta<V, ?>> void found(P propertyMeta, Runnable selectionCallback, PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {
                matchingProperties.found(getSubPropertyMeta(propertyMeta), selectionCallback, score, typeAffinityScorer);
            }
        }, score, allowSelfReference, propertyFinderTransformer, typeAffinityScorer, propertyFilter, shortCircuiter);
    }

    @SuppressWarnings("unchecked")
    private <I, E> PropertyMeta<T, E> getSubPropertyMeta(PropertyMeta<I, ?> property) {
        return new SubPropertyMeta<T, I, E>(
                passThroughClassMeta.getReflectionService(),
                (PropertyMeta<T, I>) passThroughClassMeta.getProperty(),
                (PropertyMeta<I, E>)property);
    }

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return passThroughClassMeta.getInstantiatorDefinitions();
    }

    @Override
    public PropertyFinder<?> getSubPropertyFinder(PropertyMeta<?, ?> owner) {
        if (owner.equals(passThroughClassMeta.getProperty())) {
            return propertyFinder;
        }
        throw new IllegalArgumentException("Unexpected owner " + owner);
    }

    @Override
    public PropertyFinder<?> getOrCreateSubPropertyFinder(SubPropertyMeta<?, ?, ?> subPropertyMeta) {
        return getSubPropertyFinder(subPropertyMeta.getOwnerProperty());
    }


    @Override
    public Type getOwnerType() {
        return passThroughClassMeta.getType();
    }
}
