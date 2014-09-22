package org.sfm.csv.cell;

import org.sfm.csv.DelayedSetter;
import org.sfm.reflect.Setter;

public class DelayedSetterImpl<T, P> implements DelayedSetter<T, P> {

	private final P value;
	private final Setter<T, P> setter;
	public DelayedSetterImpl(P value, Setter<T, P> setter) {
		this.value = value;
		this.setter = setter;
	}

	@Override
	public P getValue() {
		return value;
	}

	@Override
	public void set(T t) throws Exception {
		setter.set(t, value);
	}

	@Override
	public boolean isSettable() {
		return setter != null;
	}
}
