package org.sfm.map.column;


import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;

public class GetterProperty implements ColumnProperty {

    private final Getter<?, ?> getter;

    public GetterProperty(Getter<?, ?> getter) {
        this.getter = getter;
    }

    public Getter<?, ?> getGetter() {
        return getter;
    }

    public Type getReturnType() {
        Type[] paramTypesForInterface = TypeHelper.getParamTypesForInterface(getter.getClass(), Getter.class);
        return paramTypesForInterface != null ? paramTypesForInterface[1] : null;
    }

    public String toString() {
        return "Getter{Getter}";
    }
}
