package org.sfm.map;

import org.sfm.reflect.impl.NullGetter;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.utils.Predicate;

public class PropertyWithGetter implements Predicate<PropertyMeta<?, ?>> {
    @Override
    public boolean test(PropertyMeta<?, ?> propertyMeta) {
        return !NullGetter.isNull(propertyMeta.getGetter());
    }
}
