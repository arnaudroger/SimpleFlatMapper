package org.simpleflatmapper.map.property;


import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class GetterProperty {

    private final Getter<?, ?> getter;
    private final Type returnType;
    private final Type sourceType;

    public GetterProperty(Getter<?, ?> getter) {
        this(getter, getSourceType(getter), getReturnType(getter));
    }

    public GetterProperty(Getter<?, ?> getter, Type sourceType, Type returnType) {
        this.getter = getter;
        this.returnType = returnType;
        this.sourceType = sourceType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Type getSourceType() {
        return sourceType;
    }

    public Getter<?, ?> getGetter() {
        return getter;
    }

    public String toString() {
        return "Getter{" + getter + "}";
    }

    public static Type getReturnType(Getter<?, ?> getter) {
        Type[] paramTypesForInterface = TypeHelper.getGenericParameterForClass(getter.getClass(), Getter.class);
        return paramTypesForInterface != null ? paramTypesForInterface[1] : null;
    }

    public static Type getSourceType(Getter<?, ?> getter) {
        Type[] paramTypesForInterface = TypeHelper.getGenericParameterForClass(getter.getClass(), Getter.class);
        return paramTypesForInterface != null ? paramTypesForInterface[0] : null;
    }

}
