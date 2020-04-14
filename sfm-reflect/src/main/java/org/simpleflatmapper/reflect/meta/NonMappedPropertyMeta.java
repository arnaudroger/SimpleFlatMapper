package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.*;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.setter.NullSetter;

import java.lang.reflect.Type;

public class NonMappedPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final Type type;
	private final Object[] defineProperties;

	public NonMappedPropertyMeta(
			String name,
			Type ownerType,
			ReflectionService reflectService,
			Object[] defineProperties) {

		super(cleanUpName(name), ownerType, reflectService);
		this.type = Object.class;
		this.defineProperties = defineProperties;
	}

	private static String cleanUpName(String name) {
		int i = 0;
		while(i < name.length() && DefaultPropertyNameMatcher._isSeparatorChar(name.charAt(i)))
			i++;

		return i > 0 ? name.substring(i) : name;
	}

	@Override
	public PropertyMeta<T, P> withReflectionService(ReflectionService reflectionService) {
		return new NonMappedPropertyMeta<T, P>(getName(), getOwnerType(), reflectionService, defineProperties);
	}

	@Override
	public Setter<? super T, ? super P> getSetter() {
		return NullSetter.NULL_SETTER;
	}

    @Override
    public Getter<? super T, ? extends P> getGetter() {
        return NullGetter.getter();
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

	public boolean isNonMapped() {
		return true;
	}

	@Override
	public PropertyMeta<T, P> toNonMapped() {
		throw new UnsupportedOperationException();
	}

	@Override
    public String toString() {
        return "NonMappedPropertyMeta{" +
                "name="+ getName() +
                '}';
    }

}
