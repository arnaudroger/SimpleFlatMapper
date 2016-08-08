package org.simpleflatmapper.map.property;


import org.simpleflatmapper.reflect.getter.GetterFactory;

public class GetterFactoryProperty {
    private final GetterFactory<?, ?> getterFactory;

    public GetterFactoryProperty(GetterFactory<?, ?> getterFactory) {
        this.getterFactory = getterFactory;
    }

    public GetterFactory<?, ?> getGetterFactory() {
        return getterFactory;
    }

    @Override
    public String toString() {
        return "GetterFactory{GetterFactory}";
    }
}
