package org.sfm.reflect.meta;

import java.lang.reflect.Type;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

public class SubPropertyMeta<O, P> extends PropertyMeta<O, P> {
	private final PropertyMeta<O, P> ownerProperty;
	private final PropertyMeta<P, ?> subProperty;
	
	public SubPropertyMeta(ReflectionService reflectService, PropertyMeta<O, P> property, PropertyMeta<P, ?> subProperty) {
		super(property.getName(), reflectService);
		this.ownerProperty = property;
		this.subProperty = subProperty;
	}
	@Override
	protected Setter<O, P> newSetter() {
		return ownerProperty.newSetter();
	}
	@Override
	public Type getType() {
		return ownerProperty.getType();
	}
	public PropertyMeta<O, P> getOwnerProperty() {
		return ownerProperty;
	}
	public PropertyMeta<P, ?> getSubProperty() {
		return subProperty;
	}
	@Override
	protected ClassMeta<P> newClassMeta() {
		return ownerProperty.getClassMeta();
	}
	@SuppressWarnings("rawtypes")
	public Type getFinalType() {
		if (subProperty instanceof SubPropertyMeta) {
			return ((SubPropertyMeta) subProperty).getFinalType();
		} else {
			return subProperty.getType();
		}
	}
	@Override
	public boolean isSubProperty() {
		return true;
	}
	
}
