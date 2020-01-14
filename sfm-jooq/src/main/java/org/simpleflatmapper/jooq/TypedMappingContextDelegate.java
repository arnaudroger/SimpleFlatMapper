package org.simpleflatmapper.jooq;

import org.simpleflatmapper.converter.Context;

import java.lang.reflect.Type;

public class TypedMappingContextDelegate implements Context {

    private final Type targetType;

    private final Context context;

    public TypedMappingContextDelegate(Type targetType, Context context) {
        this.targetType = targetType;
        this.context = context;
    }

    @Override
    public <T> T context(int i) {
        return context.context(i);
    }

    public Type getTargetType() {
        return targetType;
    }
}
