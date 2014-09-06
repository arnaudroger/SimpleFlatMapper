package org.sfm.reflect.meta;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

public class SubPropertyMeta<T, P> extends PropertyMeta<T, P> {
	private final PropertyMeta<T, P> property;
	private final PropertyMeta<P, ?> subProperty;
	
	public SubPropertyMeta(ReflectionService reflectService, PropertyMeta<T, P> property, PropertyMeta<P, ?> subProperty) {
		super(property.getName(), reflectService);
		this.property = property;
		this.subProperty = subProperty;
	}
	@Override
	protected Setter<T, P> newSetter() {
		return property.newSetter();
	}
	@Override
	public Class<T> getType() {
		return property.getType();
	}
	public PropertyMeta<T, P> getProperty() {
		return property;
	}
	public PropertyMeta<P, ?> getSubProperty() {
		return subProperty;
	}
}
