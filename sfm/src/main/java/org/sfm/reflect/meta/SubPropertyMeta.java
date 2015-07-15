package org.sfm.reflect.meta;

import org.sfm.reflect.Getter;
import org.sfm.reflect.GetterOnGetter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

import java.lang.reflect.Type;

/**
 *
 * @param <O> the root property owner type
 * @param <I> the intermediate owner type
 * @param <P> the property type
 */
public class SubPropertyMeta<O, I,  P> extends PropertyMeta<O, P> {
	private final PropertyMeta<O, I> ownerProperty;
	private final PropertyMeta<I, P> subProperty;
	
	public SubPropertyMeta(ReflectionService reflectService, PropertyMeta<O, I> property, PropertyMeta<I, P> subProperty) {
		super(property.getName(), reflectService);
		this.ownerProperty = property;
		this.subProperty = subProperty;
	}
	@Override
	protected Setter<O, P> newSetter() {
		throw new UnsupportedOperationException();
	}

    @Override
    protected Getter<O, P> newGetter() {
		return new GetterOnGetter<O, I, P>(ownerProperty.getGetter(), subProperty.getGetter());
    }

	@Override
	protected ClassMeta<P> newPropertyClassMeta() {
		return subProperty.getPropertyClassMeta();
	}

    @Override
	public Type getPropertyType() {
		return subProperty.getPropertyType();
	}
	public PropertyMeta<O, I> getOwnerProperty() {
		return ownerProperty;
	}
	public PropertyMeta<I, P> getSubProperty() {
		return subProperty;
	}

	@Override
	public boolean isSubProperty() {
		return true;
	}
	@Override
	public String getPath() {
		return getOwnerProperty().getPath() + "." + subProperty.getPath();
	}

    @Override
    public String toString() {
        return "SubPropertyMeta{" +
                "ownerProperty=" + ownerProperty +
                ", subProperty=" + subProperty +
                '}';
    }
}
