package org.sfm.map.mapper;

import org.sfm.reflect.impl.NullSetter;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.utils.Predicate;

public class PropertyWithSetter implements Predicate<PropertyMeta<?, ?>> {
    @Override
	public boolean test(PropertyMeta<?, ?> propertyMeta) {
		return
				propertyMeta.isDirect()
						|| propertyMeta.isConstructorProperty()
						|| !NullSetter.isNull(propertyMeta.getSetter());
	}
}
