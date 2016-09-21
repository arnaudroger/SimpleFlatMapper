package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.util.Consumer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OptionalPropertyFinder<T> extends PropertyFinder<Optional<T>> {


    private final OptionalClassMeta<T> optionalClassMeta;
    private final PropertyFinder<T> propertyFinder;
    private int nbProp = 0;
    private final ClassMeta<T> innerMeta;

    public OptionalPropertyFinder(OptionalClassMeta<T> optionalClassMeta) {
        this.optionalClassMeta = optionalClassMeta;
        innerMeta = optionalClassMeta.getInnerMeta();
        this.propertyFinder = innerMeta != null ? innerMeta.newPropertyFinder() : null;
	}


    @SuppressWarnings("unchecked")
    @Override
    protected  void lookForProperties(
            PropertyNameMatcher propertyNameMatcher,
            MatchingProperties matchingProperties,
            PropertyMatchingScore score){
        if (!innerMeta.isLeaf()) {
            final PropertyMeta<T, ?> property = propertyFinder.findProperty(propertyNameMatcher);

            if (property != null) {
                matchingProperties.found(getSubPropertyMeta(property), null, score);
            }
        } else if (nbProp == 0){
            nbProp++;
            matchingProperties.found(optionalClassMeta.getProperty(), new Consumer() {
                @Override
                public void accept(Object o) {
                    nbProp++;
                }
            }, score);
        }
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
}
