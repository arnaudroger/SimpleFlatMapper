package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.GetterOnGetter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;

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
	private final GetterOnGetter<O, I, P> getter;

	public SubPropertyMeta(ReflectionService reflectService, PropertyMeta<O, I> property, PropertyMeta<I, P> subProperty) {
		super(property.getName(), reflectService);
		this.ownerProperty = property;
		this.subProperty = subProperty;
		this.getter = new GetterOnGetter<O, I, P>(ownerProperty.getGetter(), subProperty.getGetter());
	}
	@Override
	public Setter<O, P> getSetter() {
		if (subProperty.getSetter() != null && ownerProperty.getGetter() != null) {
			return new Setter<O, P>() {
				@Override
				public void set(O target, P value) throws Exception {
					subProperty.getSetter().set(ownerProperty.getGetter().get(target), value);
				}
			};
		} else {
			return null;
		}
	}

    @Override
    public Getter<O, P> getGetter() {
		return getter;
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
