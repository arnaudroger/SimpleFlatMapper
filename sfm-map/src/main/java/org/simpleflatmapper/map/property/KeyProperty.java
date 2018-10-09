package org.simpleflatmapper.map.property;


import org.simpleflatmapper.map.impl.DiscriminatorPropertyFinder;
import org.simpleflatmapper.map.impl.JoinUtils;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class KeyProperty {

    private static final Predicate<PropertyMeta<?, ?>> DEFAULT_PREDICATE = new Predicate<PropertyMeta<?, ?>>() {
        @Override
        public boolean test(PropertyMeta<?, ?> propertyMeta) {
            // if the property is in a join ... not electable
//            if (JoinUtils.isArrayElement(propertyMeta)) {
//                return false;
//            }
            
            // for now assume discriminator property are fine are they?
            // what if we have conflicting result
            if (propertyMeta instanceof DiscriminatorPropertyFinder.DiscriminatorPropertyMeta) {
                DiscriminatorPropertyFinder.DiscriminatorPropertyMeta<?, ?> dpm = (DiscriminatorPropertyFinder.DiscriminatorPropertyMeta) propertyMeta;
                return dpm.forEachProperty(new BiConsumer<Type, PropertyMeta<?, ?>>() {
                    boolean valid = true;
                    @Override
                    public void accept(Type type, PropertyMeta<?, ?> propertyMeta) {
                        if (valid && !test(propertyMeta)) {
                            valid = false;
                        }
                    }
                }).valid;
            }
            // where does the buck stop? on arrays
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
