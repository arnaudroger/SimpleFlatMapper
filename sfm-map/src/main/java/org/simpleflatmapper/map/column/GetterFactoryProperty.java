package org.simpleflatmapper.map.column;


import org.simpleflatmapper.reflect.getter.GetterFactory;

public class GetterFactoryProperty implements ColumnProperty {
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
