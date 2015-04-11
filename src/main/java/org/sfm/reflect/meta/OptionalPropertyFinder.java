package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;
import org.sfm.reflect.TypeHelper;

import java.util.Arrays;
import java.util.List;

public class OptionalPropertyFinder<T> implements PropertyFinder<T> {


    private final OptionalClassMeta<T> tupleClassMeta;
    private final PropertyFinder<?> propertyFinder;
    private int nbProp = 0;

    public OptionalPropertyFinder(OptionalClassMeta<T> tupleClassMeta) {
        this.tupleClassMeta = tupleClassMeta;
        final ClassMeta<T> innerMeta = tupleClassMeta.getInnerMeta();
        this.propertyFinder = innerMeta != null ? innerMeta.newPropertyFinder() : null;
	}


    @Override
    public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {
        if (propertyFinder != null) {
            final PropertyMeta<?, Object> property = propertyFinder.findProperty(propertyNameMatcher);

            if (property != null) {
                return getSubPropertyMeta((PropertyMeta<E, ?>) property);
            }
        } else if (nbProp == 0){
            nbProp++;
            return (PropertyMeta<T, E>) tupleClassMeta.getProperty();
        }

        return null;
    }

    private <E> PropertyMeta<T, E> getSubPropertyMeta(PropertyMeta<E, ?> property) {
        return new SubPropertyMeta<T, E>(tupleClassMeta.getReflectionService(), (PropertyMeta<T, E>) tupleClassMeta.getProperty(), property);
    }

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return Arrays.asList(tupleClassMeta.getInstantiatorDefinition());
    }

    @Override
    public <E> ConstructorPropertyMeta<T, E> findConstructor(InstantiatorDefinition instantiatorDefinition) {
        return null;
    }
}
