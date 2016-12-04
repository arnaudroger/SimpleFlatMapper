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

    public OptionalPropertyFinder(OptionalClassMeta<T> optionalClassMeta, Predicate<PropertyMeta<?, ?>> propertyFilter) {
        super(propertyFilter);
        this.optionalClassMeta = optionalClassMeta;
        innerMeta = optionalClassMeta.getInnerMeta();
        this.propertyFinder = innerMeta != null ? innerMeta.newPropertyFinder(propertyFilter) : null;
	}


    @SuppressWarnings("unchecked")
    @Override
    public void lookForProperties(
            PropertyNameMatcher propertyNameMatcher,
            FoundProperty matchingProperties,
            PropertyMatchingScore score, boolean allowSelfReference, PropertyFinderTransformer propertyFinderTransformer){
        propertyFinderTransformer.apply(propertyFinder).lookForProperties(propertyNameMatcher, new FoundProperty<T>() {
            @Override
            public <P extends PropertyMeta<T, ?>> void found(P propertyMeta, Runnable selectionCallback, PropertyMatchingScore score) {
                matchingProperties.found(getSubPropertyMeta(propertyMeta), selectionCallback, score);
            }
        }, score, allowSelfReference, propertyFinderTransformer);
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
    public PropertyFinder<?> getSubPropertyFinder(String name) {
        return null;
    }

    @Override
    public Type getOwnerType() {
        return optionalClassMeta.getType();
    }
}
