package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.ScoredGetter;
import org.simpleflatmapper.reflect.ScoredSetter;
import org.simpleflatmapper.reflect.Setter;

import java.lang.reflect.Type;

public class ObjectPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final ScoredSetter<T, P> setter;
    private final ScoredGetter<T, P> getter;
	private final Type type;
	private final Object[] defineProperties;

	public ObjectPropertyMeta(
			String name,
			Type ownerType,
			ReflectionService reflectService,
			Type propertyType,
			ScoredGetter<T, P> getter,
			ScoredSetter<T, P> setter, Object[] defineProperties) {

		super(name, ownerType, reflectService);
		this.type = propertyType;
        this.getter = getter;
        this.setter = setter;
		this.defineProperties = defineProperties;
	}

	@Override
	public PropertyMeta<T, P> withReflectionService(ReflectionService reflectionService) {
		return new ObjectPropertyMeta<T, P>(getName(), getOwnerType(), reflectionService, type, getter, setter, defineProperties);
	}

	@Override
	public PropertyMeta<T, P> toNonMapped() {
		throw new UnsupportedOperationException();
	}


	public PropertyMeta<T, P> getterSetter(ScoredGetter<T, P> getter, ScoredSetter<T, P> setter, Object[] defineProperties) {
        return new ObjectPropertyMeta<T, P>(getName(), getOwnerType(), reflectService, type, this.getter.best(getter), this.setter.best(setter), concatenate(this.defineProperties, defineProperties));
    }


	public static Object[] concatenate(Object[] p1, Object[] p2) {
		int l = 0;
		if (p1 != null) {
			l += p1.length;
		}
		if (p2 != null) {
			l += p2.length;
		}

		Object[] merged = new Object[l];


		int start = 0;

		if (p1 != null) {
			System.arraycopy(p1, 0, merged, 0, p1.length);
			start += p1.length;
		}

		if (p2 != null) {
			System.arraycopy(p2, 0, merged, start, p2.length);
		}

		return merged;
	}
	@Override
	public Setter<? super T, ? super P> getSetter() {
		return setter.getSetter();
	}

    @Override
    public Getter<? super T, ? extends P> getGetter() {
        return getter.getGetter();
    }

    @Override
	public Type getPropertyType() {
		return type;
	}

	@Override
	public String getPath() {
		return getName();
	}

	@Override
	public Object[] getDefinedProperties() {
		return defineProperties;
	}


	@Override
    public String toString() {
        return "ObjectPropertyMeta{" +
                "name="+ getName() +
                ", type=" + type +
                ", setter=" + setter +
                ", getter=" + getter +
                '}';
    }

}
