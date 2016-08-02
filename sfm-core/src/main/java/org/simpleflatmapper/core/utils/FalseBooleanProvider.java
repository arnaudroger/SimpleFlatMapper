package org.simpleflatmapper.core.utils;

public class FalseBooleanProvider implements BooleanProvider {
    @Override
    public boolean getBoolean() {
        return false;
    }
}
