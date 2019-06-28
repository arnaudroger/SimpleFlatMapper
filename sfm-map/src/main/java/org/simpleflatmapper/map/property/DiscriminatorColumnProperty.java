package org.simpleflatmapper.map.property;

import org.simpleflatmapper.reflect.property.EligibleAsNonMappedProperty;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class DiscriminatorColumnProperty implements Predicate<Type>, EligibleAsNonMappedProperty {

    private final Type commonType;


    public DiscriminatorColumnProperty(Type commonType) {
        this.commonType = commonType;
    }


    @Override
    public boolean test(Type type) {
        return TypeHelper.isAssignable(commonType, type);
    }
}
