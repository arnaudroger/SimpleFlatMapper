package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;

import java.util.Arrays;
import java.util.List;

public class OptionalPropertyFinder<T> implements PropertyFinder<T> {


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
    public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {
        if (!innerMeta.isLeaf()) {
            final PropertyMeta<T, ?> property = propertyFinder.findProperty(propertyNameMatcher);

            if (property != null) {
                return getSubPropertyMeta(property);
            }
        } else if (nbProp == 0){
            nbProp++;
            return (PropertyMeta<T, E>) tupleClassMeta.getProperty();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <I, E> PropertyMeta<T, E> getSubPropertyMeta(PropertyMeta<I, ?> property) {
        return new SubPropertyMeta<T, I, E>(
                tupleClassMeta.getReflectionService(),
                (PropertyMeta<T, I>)tupleClassMeta.getProperty(),
                (PropertyMeta<I, E>)property);
    }

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return Arrays.asList(tupleClassMeta.getInstantiatorDefinition());
    }
}
