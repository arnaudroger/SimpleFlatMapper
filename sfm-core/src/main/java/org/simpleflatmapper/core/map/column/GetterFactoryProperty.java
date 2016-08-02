package org.simpleflatmapper.core.map.column;


import org.simpleflatmapper.core.map.GetterFactory;

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
