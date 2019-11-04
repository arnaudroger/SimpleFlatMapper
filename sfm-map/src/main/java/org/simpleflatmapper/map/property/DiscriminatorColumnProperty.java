package org.simpleflatmapper.map.property;

import org.simpleflatmapper.reflect.property.EligibleAsNonMappedProperty;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class DiscriminatorColumnProperty implements Predicate<Type>, EligibleAsNonMappedProperty {

    private final Type commonType;
    private final Object discriminatorId;


    public DiscriminatorColumnProperty(Type commonType, Object discriminatorId) {
        this.commonType = commonType;
        this.discriminatorId = discriminatorId;
    }


    @Override
    public boolean test(Type type) {
        return TypeHelper.isAssignable(commonType, type);
    }

    public Object getDiscriminatorId() {
        return discriminatorId;
    }
}
