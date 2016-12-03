package org.simpleflatmapper.map.property;


import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class SetterProperty {

    private final Setter<?, ?> setter;
    private final Type targetType;
    private final Type propertyType;

    public SetterProperty(Setter<?, ?> setter) {
        this(setter, getTargetType(setter), getPropertyType(setter));
    }

    public SetterProperty(Setter<?, ?> setter, Type targetType, Type propertyType) {
        this.setter = setter;
        this.targetType = targetType;
        this.propertyType = propertyType;
    }

    public Setter<?, ?> getSetter() {
        return setter;
    }

    public Type getTargetType() {
        return targetType;
    }

    @Override
    public String toString() {
        return "Setter{" + setter + "}";
    }

    private static Type getTargetType(Setter<?, ?> setter) {
        Type[] types = TypeHelper.getGenericParameterForClass(setter.getClass(), Setter.class);
        return types != null ? types[0] : null;
    }

    private static Type getPropertyType(Setter<?, ?> setter) {
        Type[] types = TypeHelper.getGenericParameterForClass(setter.getClass(), Setter.class);
        return types != null ? types[1] : null;
    }

    public Type getPropertyType() {
        return propertyType;
    }
}
