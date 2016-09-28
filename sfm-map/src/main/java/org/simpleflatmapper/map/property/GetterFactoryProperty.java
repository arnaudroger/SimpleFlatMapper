package org.simpleflatmapper.map.property;


import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class GetterFactoryProperty {
    private final GetterFactory<?, ?> getterFactory;
    private final Type sourceType;

    public GetterFactoryProperty(GetterFactory<?, ?> getterFactory) {
        this(getterFactory, getSourceType(getterFactory));
    }

    public GetterFactoryProperty(GetterFactory<?, ?> getterFactory, Type sourceType) {
        this.getterFactory = getterFactory;
        this.sourceType = sourceType;
    }

    public GetterFactory<?, ?> getGetterFactory() {
        return getterFactory;
    }

    public Type getSourceType() {
        return sourceType;
    }

    @Override
    public String toString() {
        return "GetterFactory{" + getterFactory + "}";
    }

    private static Type getSourceType(GetterFactory<?, ?> getterFactory) {
        Type[] types = TypeHelper.getGenericParameterForClass(getterFactory.getClass(), GetterFactory.class);
        return types != null ? types[0] : null;
    }
}
