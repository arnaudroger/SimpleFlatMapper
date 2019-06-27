package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.GetterOnGetter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.setter.NullSetter;

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

	public SubPropertyMeta(ReflectionService reflectService, PropertyMeta<O, I> ownerProperty, PropertyMeta<I, P> subProperty) {
		super(ownerProperty.getName(), ownerProperty.getOwnerType(), reflectService);
		this.ownerProperty = ownerProperty;
		this.subProperty = subProperty;
	}

	@Override
	public PropertyMeta<O, P> withReflectionService(ReflectionService reflectionService) {
		return new SubPropertyMeta<O, I, P>(reflectionService, ownerProperty.withReflectionService(reflectionService), subProperty.withReflectionService(reflectionService));
	}


	@SuppressWarnings("unchecked")
	@Override
	public Setter<O, P> getSetter() {
		if (subProperty.isSelf()) {
			return (Setter<O, P>) ownerProperty.getSetter();
		}
		if (!NullSetter.isNull(subProperty.getSetter()) && !NullGetter.isNull(ownerProperty.getGetter())) {
			return new Setter<O, P>() {
				@Override
				public void set(O target, P value) throws Exception {
					subProperty.getSetter().set(ownerProperty.getGetter().get(target), value);
				}
			};
		} else {
			return (Setter<O, P>) NullSetter.NULL_SETTER;
		}
	}

    @Override
    public Getter<O, P> getGetter() {
		if (subProperty.isSelf()) return (Getter<O, P>) ownerProperty.getGetter();
		return  new GetterOnGetter<O, I, P>(this.ownerProperty.getGetter(), subProperty.getGetter());
    }

	@Override
	protected ClassMeta<P> newPropertyClassMeta() {
		return subProperty.getPropertyClassMeta();
	}

    @Override
	public Type getPropertyType() {
		return subProperty.getPropertyType();
	}

	@Override
	public int typeAffinityScore(PropertyFinder.TypeAffinityScorer typeAffinityScorer) {
		return subProperty.typeAffinityScore(typeAffinityScorer);
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


	public boolean isNonMapped() {
		return subProperty.isNonMapped();
	}

	@Override
	public boolean isValid() {
		return subProperty.isValid();
	}

	@Override
	public String getPath() {
		String subPath = subProperty.getPath();
		String subPathPrefix = subPath.startsWith("[") ? "" : ".";
		return getOwnerProperty().getPath() + subPathPrefix + subPath;
	}


	@Override
	public Object[] getDefinedProperties() {
		return subProperty.getDefinedProperties();
	}

	@Override
	public PropertyMeta<O, P> toNonMapped() {
		return new SubPropertyMeta<O, I,  P>(reflectService, ownerProperty, subProperty.toNonMapped());
	}

	@Override
	public PropertyMeta<O, P> compressSubSelf() {
		if (subProperty.isSelf()) return (PropertyMeta<O, P>) ownerProperty;
		return new SubPropertyMeta(reflectService, ownerProperty, subProperty.compressSubSelf());
	}

	@Override
    public String toString() {
        return "SubPropertyMeta{" +
                "ownerProperty=" + ownerProperty +
                ", subProperty=" + subProperty +
                '}';
    }
}
