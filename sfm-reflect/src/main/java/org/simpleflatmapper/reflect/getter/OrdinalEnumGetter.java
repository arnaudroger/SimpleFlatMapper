package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.IntGetter;

import java.util.Arrays;

public final class OrdinalEnumGetter<R, E extends Enum<E>> implements Getter<R, E> {

	private final IntGetter<R> getter;
	private final E[] values;
	
	public OrdinalEnumGetter(IntGetter<R> getter, final Class<E> enumType)  {
		this.getter = getter;
		this.values = enumType.getEnumConstants();
	}

	@Override
	public E get(final R target) throws Exception {
		return values[getter.getInt(target)];
	}

    @Override
    public String toString() {
        return "OrdinalEnumResultSetGetter{" +
                "getter=" + getter +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}
