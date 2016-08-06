package org.simpleflatmapper.map.column;


import org.simpleflatmapper.reflect.Setter;

public class SetterProperty implements ColumnProperty {

    private final Setter<?, ?> setter;

    public SetterProperty(Setter<?, ?> setter) {
        this.setter = setter;
    }

    public Setter<?, ?> getSetter() {
        return setter;
    }

    @Override
    public String toString() {
        return "Setter{Setter}";
    }
}
