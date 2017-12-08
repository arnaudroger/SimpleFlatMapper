package org.simpleflatmapper.map.property;


import org.simpleflatmapper.map.impl.JoinUtils;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.util.Predicate;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class KeyProperty {

    private static final Predicate<PropertyMeta<?, ?>> DEFAULT_PREDICATE = new Predicate<PropertyMeta<?, ?>>() {
        @Override
        public boolean test(PropertyMeta<?, ?> propertyMeta) {
            // if the property is in a join ... not electable
//            if (JoinUtils.isArrayElement(propertyMeta)) {
//                return false;
//            }
            
            if (propertyMeta.isSubProperty()) {
                SubPropertyMeta subPropertyMeta = (SubPropertyMeta) propertyMeta;
                if (JoinUtils.isArrayElement(subPropertyMeta.getOwnerProperty())) {
                    return false;
                } else {
                    PropertyMeta subProperty = subPropertyMeta.getSubProperty();
                    return !JoinUtils.isArrayElement(subProperty) && test(subProperty);
                }
            }
            return true; //!propertyMeta.isSubProperty();
        }
    };

    public static final KeyProperty DEFAULT = new KeyProperty(DEFAULT_PREDICATE);

    private final Predicate<PropertyMeta<?, ?>> appliesTo;

    public KeyProperty() {
        this(DEFAULT_PREDICATE);
    }

    public KeyProperty(Predicate<PropertyMeta<?, ?>> appliesTo) {
        this.appliesTo = requireNonNull("appliesTo", appliesTo);
    }

    public Predicate<PropertyMeta<?, ?>> getAppliesTo() {
        return appliesTo;
    }

    @Override
    public String toString() {
        return "Key{" + appliesTo + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KeyProperty that = (KeyProperty) o;

        return appliesTo.equals(that.appliesTo);

    }

    @Override
    public int hashCode() {
        return appliesTo.hashCode();
    }
}
