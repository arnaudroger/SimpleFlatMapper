package org.simpleflatmapper.map;

import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;

public final class PropertyWithGetter implements Predicate<PropertyMeta<?, ?>> {
    public static final PropertyWithGetter INSTANCE = new PropertyWithGetter();
    private PropertyWithGetter() {
    }
    @Override
    public boolean test(PropertyMeta<?, ?> propertyMeta) {
        return !NullGetter.isNull(propertyMeta.getGetter());
    }
}
