package org.sfm.map.column;


import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.utils.Predicate;

public class KeyProperty implements ColumnProperty {
    private static final Predicate<PropertyMeta<?, ?>> DEFAULT_PREDICATE = new Predicate<PropertyMeta<?, ?>>() {
        @Override
        public boolean test(PropertyMeta<?, ?> propertyMeta) {
            return !propertyMeta.isSubProperty();
        }
    };
    private final Predicate<PropertyMeta<?, ?>> appliesTo;

    public KeyProperty(Predicate<PropertyMeta<?, ?>> appliesTo) {
        if (appliesTo == null) throw new NullPointerException();
        this.appliesTo = appliesTo;
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
