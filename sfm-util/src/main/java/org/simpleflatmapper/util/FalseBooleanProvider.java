package org.simpleflatmapper.util;

public class FalseBooleanProvider implements BooleanProvider {
    @Override
    public boolean getBoolean() {
        return false;
    }
}
