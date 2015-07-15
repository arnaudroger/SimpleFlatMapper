package org.sfm.utils;

public class FalseBooleanProvider implements BooleanProvider {
    @Override
    public boolean getBoolean() {
        return false;
    }
}
