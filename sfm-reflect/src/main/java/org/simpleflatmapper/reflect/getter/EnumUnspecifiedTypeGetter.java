package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;

public final class EnumUnspecifiedTypeGetter<R, E extends Enum<E>> implements Getter<R, E> {

	private final Getter<R, ?> getter;
	private final Class<E> enumType;
	private final E[] values;
	
	public EnumUnspecifiedTypeGetter(Getter<R, ?> getter, final Class<E> enumType)  {
		this.getter = getter;
		this.enumType = enumType;
		this.values = enumType.getEnumConstants();
	}

	@Override
	public E get(final R target) throws Exception {
		final Object o = getter.get(target);
		if (o instanceof Number) {
			return values[((Number) o).intValue()];
		} else {
			return (E) Enum.valueOf(enumType, String.valueOf(o));
		}
	}

    @Override
    public String toString() {
        return "EnumUnspeficiedTypeGetter{" +
                "getter=" + getter +
                ", enumType=" + enumType +
                '}';
    }
}
