package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.reflect.getter.NullSetter;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;

public class PropertyWithSetter implements Predicate<PropertyMeta<?, ?>> {
    @Override
	public boolean test(PropertyMeta<?, ?> propertyMeta) {
		return
				propertyMeta.isDirect()
						|| propertyMeta.isConstructorProperty()
						|| !NullSetter.isNull(propertyMeta.getSetter());
	}
}
