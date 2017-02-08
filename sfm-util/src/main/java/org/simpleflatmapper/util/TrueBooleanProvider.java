package org.simpleflatmapper.util;

public class TrueBooleanProvider implements BooleanProvider {
    public static TrueBooleanProvider INSTANCE = new TrueBooleanProvider();

    private TrueBooleanProvider() {
    }

    @Override
    public boolean getBoolean() {
        return true;
    }
}
