package org.sfm.map.column;


import org.sfm.map.GetterFactory;

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
