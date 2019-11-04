package org.simpleflatmapper.map.property;


import org.simpleflatmapper.map.impl.DiscriminatorPropertyFinder;
import org.simpleflatmapper.map.impl.JoinUtils;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.reflect.property.EligibleAsNonMappedProperty;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class InferNullProperty implements EligibleAsNonMappedProperty {

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
                return dpm.forEachProperty(new Consumer<DiscriminatorPropertyFinder.DiscriminatorMatch>() {
                    boolean valid = false;
                    @Override
                    public void accept(DiscriminatorPropertyFinder.DiscriminatorMatch dm) {
                        PropertyMeta<?, ?> propertyMeta = dm.matchedProperty.getPropertyMeta();
                        if (!valid && test(propertyMeta)) {
                            valid = true;
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

    public static final InferNullProperty DEFAULT = new InferNullProperty(DEFAULT_PREDICATE);

    private final Predicate<PropertyMeta<?, ?>> appliesTo;

    public InferNullProperty() {
        this(DEFAULT_PREDICATE);
    }

    public InferNullProperty(Predicate<PropertyMeta<?, ?>> appliesTo) {
        this.appliesTo = requireNonNull("appliesTo", appliesTo);
    }

    public Predicate<PropertyMeta<?, ?>> getAppliesTo() {
        return appliesTo;
    }

    @Override
    public String toString() {
        return "InferNullProperty{" + appliesTo + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InferNullProperty that = (InferNullProperty) o;

        return appliesTo.equals(that.appliesTo);

    }

    @Override
    public int hashCode() {
        return appliesTo.hashCode();
    }
}
