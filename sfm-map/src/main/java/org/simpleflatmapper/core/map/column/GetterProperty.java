package org.simpleflatmapper.core.map.column;


import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

public class GetterProperty implements ColumnProperty {

    private final Getter<?, ?> getter;
    private final Type returnType;

    public GetterProperty(Getter<?, ?> getter) {
        this(getter, getReturnType(getter));
    }

    public GetterProperty(Getter<?, ?> getter, Type returnType) {
        this.getter = getter;
        this.returnType = returnType;
    }

    public Type getReturnType() {
        return returnType;
    }

    public Getter<?, ?> getGetter() {
        return getter;
    }

    public String toString() {
        return "Getter{Getter}";
    }

    public static Type getReturnType(Getter<?, ?> getter) {
        Type[] paramTypesForInterface = TypeHelper.getParamTypesForInterface(getter.getClass(), Getter.class);
        return paramTypesForInterface != null ? paramTypesForInterface[1] : null;
    }

}
