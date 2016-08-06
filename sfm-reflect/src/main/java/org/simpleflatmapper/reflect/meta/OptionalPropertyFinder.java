package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OptionalPropertyFinder<T> implements PropertyFinder<Optional<T>> {


    private final OptionalClassMeta<T> tupleClassMeta;
    private final PropertyFinder<T> propertyFinder;
    private int nbProp = 0;
    private final ClassMeta<T> innerMeta;

    public OptionalPropertyFinder(OptionalClassMeta<T> tupleClassMeta) {
        this.tupleClassMeta = tupleClassMeta;
        innerMeta = tupleClassMeta.getInnerMeta();
        this.propertyFinder = innerMeta != null ? innerMeta.newPropertyFinder() : null;
	}


    @SuppressWarnings("unchecked")
    @Override
    public <E> PropertyMeta<Optional<T>, E> findProperty(PropertyNameMatcher propertyNameMatcher) {
        if (!innerMeta.isLeaf()) {
            final PropertyMeta<T, ?> property = propertyFinder.findProperty(propertyNameMatcher);

            if (property != null) {
                return getSubPropertyMeta(property);
            }
        } else if (nbProp == 0){
            nbProp++;
            return (PropertyMeta<Optional<T>, E>) tupleClassMeta.getProperty();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <I, E> PropertyMeta<Optional<T>, E> getSubPropertyMeta(PropertyMeta<I, ?> property) {
        return new SubPropertyMeta<Optional<T>, I, E>(
                tupleClassMeta.getReflectionService(),
                (PropertyMeta<Optional<T>, I>)tupleClassMeta.getProperty(),
                (PropertyMeta<I, E>)property);
    }

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return Arrays.asList(tupleClassMeta.getInstantiatorDefinition());
    }

    @Override
    public PropertyFinder<?> getSubPropertyFinder(String name) {
        return null;
    }
}
