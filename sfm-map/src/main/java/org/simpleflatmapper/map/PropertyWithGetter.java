package org.simpleflatmapper.map;

import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;

public class PropertyWithGetter implements Predicate<PropertyMeta<?, ?>> {
    @Override
    public boolean test(PropertyMeta<?, ?> propertyMeta) {
        return !NullGetter.isNull(propertyMeta.getGetter());
    }
}
