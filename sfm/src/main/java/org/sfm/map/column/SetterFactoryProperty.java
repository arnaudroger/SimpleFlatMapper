package org.sfm.map.column;


import org.sfm.reflect.SetterFactory;

public class SetterFactoryProperty implements ColumnProperty {
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
