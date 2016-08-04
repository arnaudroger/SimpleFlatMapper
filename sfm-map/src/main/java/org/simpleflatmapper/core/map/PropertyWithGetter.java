package org.simpleflatmapper.core.map;

import org.simpleflatmapper.core.reflect.NullGetter;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;

public class PropertyWithGetter implements Predicate<PropertyMeta<?, ?>> {
    @Override
    public boolean test(PropertyMeta<?, ?> propertyMeta) {
        return !NullGetter.isNull(propertyMeta.getGetter());
    }
}
