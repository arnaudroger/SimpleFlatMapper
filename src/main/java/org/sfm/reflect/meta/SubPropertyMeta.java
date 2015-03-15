package org.sfm.reflect.meta;

import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

import java.lang.reflect.Type;

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
    protected Getter<O, P> newGetter() {
        return ownerProperty.newGetter();
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
	@Override
	public boolean isSubProperty() {
		return true;
	}
	@Override
	public String getPath() {
		return getOwnerProperty().getPath() + "." + subProperty.getPath();
	}

    @SuppressWarnings("unchecked")
    public Type getLeafType() {
        if (subProperty.isSubProperty()) {
            return ((SubPropertyMeta<P,?>)subProperty).getLeafType();
        } else {
            return subProperty.getType();
        }
    }

    @Override
    public String toString() {
        return "SubPropertyMeta{" +
                "ownerProperty=" + ownerProperty +
                ", subProperty=" + subProperty +
                '}';
    }
}
