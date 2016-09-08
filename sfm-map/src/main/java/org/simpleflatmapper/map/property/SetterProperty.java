package org.simpleflatmapper.map.property;


import org.simpleflatmapper.reflect.Setter;

public class SetterProperty {

    private final Setter<?, ?> setter;

    public SetterProperty(Setter<?, ?> setter) {
        this.setter = setter;
    }

    public Setter<?, ?> getSetter() {
        return setter;
    }

    @Override
    public String toString() {
        return "Setter{" + setter + "}";
    }
}
