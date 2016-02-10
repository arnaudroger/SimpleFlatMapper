package org.sfm.map.column;


import org.sfm.reflect.Setter;

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
