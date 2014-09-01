package org.sfm.reflect;

public class SubPropertyMeta<T, P> extends PropertyMeta<T, P> {
	private final PropertyMeta<T, P> property;
	private final PropertyMeta<P, ?> subProperty;
	
	public SubPropertyMeta(PropertyMeta<T, P> property, PropertyMeta<P, ?> subProperty) {
		super(property.getName());
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
