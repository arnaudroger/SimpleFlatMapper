package org.simpleflatmapper.map.property;


import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class SetterFactoryProperty {
    private final SetterFactory<?, ?> setterFactory;
    private final Type targetType;

    public SetterFactoryProperty(SetterFactory<?, ?> setterFactory) {
        this(setterFactory, getTargetType(setterFactory));

    }
    public SetterFactoryProperty(SetterFactory<?, ?> setterFactory, Type targetType) {
        this.setterFactory = setterFactory;
        this.targetType = targetType;
    }

    public Type getTargetType() {
        return targetType;
    }

    public SetterFactory<?, ?> getSetterFactory() {
        return setterFactory;
    }

    @Override
    public String toString() {
        return "SetterFactory{" + setterFactory + "}";
    }

    private static Type getTargetType(SetterFactory<?, ?> setterFactory) {
        Type[] types = TypeHelper.getGenericParameterForClass(setterFactory.getClass(), SetterFactory.class);
        return types != null ? types[0] : null;
    }
}
