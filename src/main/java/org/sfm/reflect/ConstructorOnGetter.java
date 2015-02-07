package org.sfm.reflect;

import java.lang.reflect.Constructor;

public class ConstructorOnGetter<T, P> implements Getter<T, P> {
	private final Constructor<? extends P> constructor;
	private final Getter<T, ?> getter;
	
	public ConstructorOnGetter(Constructor<? extends  P> constructor, Getter<T, ?> getter) {
		super();
		this.constructor = constructor;
		this.getter = getter;
	}

	@Override
	public P get(T target) throws Exception {
		return constructor.newInstance(getter.get(target));
	}

}
