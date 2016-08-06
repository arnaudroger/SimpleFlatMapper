package org.simpleflatmapper.map.column;


import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class KeyProperty implements ColumnProperty {
    private static final Predicate<PropertyMeta<?, ?>> DEFAULT_PREDICATE = new Predicate<PropertyMeta<?, ?>>() {
        @Override
        public boolean test(PropertyMeta<?, ?> propertyMeta) {
            return !propertyMeta.isSubProperty();
        }
    };
    private final Predicate<PropertyMeta<?, ?>> appliesTo;

    public KeyProperty(Predicate<PropertyMeta<?, ?>> appliesTo) {
        this.appliesTo = requireNonNull("appliesTo", appliesTo);
    }
    public KeyProperty() {
       this(DEFAULT_PREDICATE);
    }

    public Predicate<PropertyMeta<?, ?>> getAppliesTo() {
        return appliesTo;
    }

    @Override
    public String toString() {
        return "Key{}";
    }
}
