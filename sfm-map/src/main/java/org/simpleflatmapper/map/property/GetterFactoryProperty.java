package org.simpleflatmapper.map.property;


import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class GetterFactoryProperty {
    private final ContextualGetterFactory<?, ?> getterFactory;
    private final Type sourceType;

    public GetterFactoryProperty(ContextualGetterFactory<?, ?> getterFactory) {
        this(getterFactory, getSourceType(getterFactory));
    }

    public GetterFactoryProperty(ContextualGetterFactory<?, ?> getterFactory, Type sourceType) {
        this.getterFactory = getterFactory;
        this.sourceType = sourceType;
    }

    public GetterFactoryProperty(GetterFactory<?, ?> getterFactory) {
        this(getterFactory, getSourceType(getterFactory));
    }

    public GetterFactoryProperty(GetterFactory<?, ?> getterFactory, Type sourceType) {
        this.getterFactory = new ContextualGetterFactoryAdapter(getterFactory);
        this.sourceType = sourceType;
    }

    public ContextualGetterFactory<?, ?> getGetterFactory() {
        return getterFactory;
    }

    public Type getSourceType() {
        return sourceType;
    }

    @Override
    public String toString() {
        return "GetterFactory{" + getterFactory + "}";
    }

    private static Type getSourceType(ContextualGetterFactory<?, ?> getterFactory) {
        Type[] types = TypeHelper.getGenericParameterForClass(getterFactory.getClass(), ContextualGetterFactory.class);
        return types != null ? types[0] : null;
    }
    private static Type getSourceType(GetterFactory<?, ?> getterFactory) {
        Type[] types = TypeHelper.getGenericParameterForClass(getterFactory.getClass(), GetterFactory.class);
        return types != null ? types[0] : null;
    }
}
