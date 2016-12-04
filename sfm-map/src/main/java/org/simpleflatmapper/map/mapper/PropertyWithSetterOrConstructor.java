package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;

public class PropertyWithSetterOrConstructor implements Predicate<PropertyMeta<?, ?>> {

	public static final PropertyWithSetterOrConstructor INSTANCE = new PropertyWithSetterOrConstructor();

	private PropertyWithSetterOrConstructor() {
	}

    @Override
	public boolean test(PropertyMeta<?, ?> propertyMeta) {
		if (propertyMeta.isSelf()) {
			return true;
		}
		if (!NullSetter.isNull(propertyMeta.getSetter())) {
			return true;
		}
		if (propertyMeta.isConstructorProperty()) {
			return true;
		}

		if (propertyMeta.isSubProperty()) {
			SubPropertyMeta subPropertyMeta = (SubPropertyMeta) propertyMeta;
			if (subPropertyMeta.getOwnerProperty().isConstructorProperty()
					|| !NullSetter.isNull(subPropertyMeta.getOwnerProperty().getSetter())) {
				return test(subPropertyMeta.getSubProperty());
			}
		}
 		return false;
	}
}
