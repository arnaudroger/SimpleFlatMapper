package org.simpleflatmapper.map.property;


import org.simpleflatmapper.reflect.SetterFactory;

public class SetterFactoryProperty {
    private final SetterFactory<?, ?> setterFactory;

    public SetterFactoryProperty(SetterFactory<?, ?> setterFactory) {
        this.setterFactory = setterFactory;
    }
    public SetterFactory<?, ?> getSetterFactory() {
        return setterFactory;
    }

    @Override
    public String toString() {
        return "SetterFactory{SetterFactory}";
    }
}
