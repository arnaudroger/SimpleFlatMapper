package org.simpleflatmapper.map.property;


import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class SetterProperty {

    private final Setter<?, ?> setter;
    private final Type targetType;

    public SetterProperty(Setter<?, ?> setter) {
        this(setter, getTargetType(setter));
    }

    public SetterProperty(Setter<?, ?> setter, Type targetType) {
        this.setter = setter;
        this.targetType = targetType;
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
}
