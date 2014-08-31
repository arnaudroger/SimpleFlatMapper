package org.sfm.reflect;


public abstract class PropertyMeta<T, P> {
	private final String name;
	private volatile Setter<T, P> setter;
	
	public PropertyMeta(String name) {
		this.name = name;
	}

	public final Setter<T, P> getSetter() {
		Setter<T, P> lsetter = setter;
		if (lsetter == null) {
			lsetter = newSetter();
			setter = lsetter;
		}
		return lsetter;
	}

	protected abstract Setter<T, P> newSetter();

	public final String getName() {
		return name;
	}
	
	
}
