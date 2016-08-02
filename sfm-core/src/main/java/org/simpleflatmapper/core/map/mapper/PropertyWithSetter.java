package org.simpleflatmapper.core.map.mapper;

import org.simpleflatmapper.core.reflect.NullSetter;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;
import org.simpleflatmapper.core.utils.Predicate;

public class PropertyWithSetter implements Predicate<PropertyMeta<?, ?>> {
    @Override
	public boolean test(PropertyMeta<?, ?> propertyMeta) {
		return
				propertyMeta.isDirect()
						|| propertyMeta.isConstructorProperty()
						|| !NullSetter.isNull(propertyMeta.getSetter());
	}
}
