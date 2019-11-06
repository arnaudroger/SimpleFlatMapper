package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

public class OptionalPropertyFinder<T> extends PropertyFinder<Optional<T>> {


    private final OptionalClassMeta<T> optionalClassMeta;
    private final PropertyFinder<T> propertyFinder;
    private final ClassMeta<T> innerMeta;

    @Deprecated
    public OptionalPropertyFinder(OptionalClassMeta<T> optionalClassMeta, boolean selfScoreFullName) {
        this(optionalClassMeta);
    }
    public OptionalPropertyFinder(OptionalClassMeta<T> optionalClassMeta) {
        super();
        this.optionalClassMeta = optionalClassMeta;
        innerMeta = optionalClassMeta.getInnerMeta();
        this.propertyFinder = innerMeta != null ? innerMeta.newPropertyFinder() : null;
	}


    @SuppressWarnings("unchecked")
    @Override
    public void lookForProperties(
            PropertyNameMatcher propertyNameMatcher,
            Object[] properties, 
            FoundProperty<Optional<T>> matchingProperties,
            PropertyMatchingScore score, boolean allowSelfReference, 
            PropertyFinderTransformer propertyFinderTransformer, 
            TypeAffinityScorer typeAffinityScorer, 
            PropertyFilter propertyFilter, ShortCircuiter shortCircuiter) {

        propertyFinderTransformer.apply(propertyFinder).lookForProperties(propertyNameMatcher, properties, new FoundProperty<T>() {
            @Override
            public <P extends PropertyMeta<T, ?>> void found(P propertyMeta, Runnable selectionCallback, PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {
                matchingProperties.found(getSubPropertyMeta(propertyMeta), selectionCallback, score, typeAffinityScorer);
            }
        }, score, allowSelfReference, propertyFinderTransformer, typeAffinityScorer,  propertyFilter, shortCircuiter);
    }

    @SuppressWarnings("unchecked")
    private <I, E> PropertyMeta<Optional<T>, E> getSubPropertyMeta(PropertyMeta<I, ?> property) {
        return new SubPropertyMeta<Optional<T>, I, E>(
                optionalClassMeta.getReflectionService(),
                (PropertyMeta<Optional<T>, I>) optionalClassMeta.getProperty(),
                (PropertyMeta<I, E>)property);
    }

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return optionalClassMeta.getInstantiatorDefinitions();
    }

    @Override
    public PropertyFinder<?> getSubPropertyFinder(PropertyMeta<?, ?> owner) {
        if (owner.equals(optionalClassMeta.getProperty())) {
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
        return optionalClassMeta.getType();
    }
}
