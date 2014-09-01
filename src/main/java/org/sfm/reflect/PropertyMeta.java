package org.sfm.reflect;


public abstract class PropertyMeta<T, P> {
	private final String name;
	private volatile Setter<T, P> setter;
	private volatile ClassMeta<T> classMeta;
 	
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

	public abstract Class<T> getType();

	public final ClassMeta<T> getClassMeta(SetterFactory setterFactory, boolean asmPresent) {
		ClassMeta<T> meta = classMeta;
		if (meta == null) {
			meta = new ClassMeta<>(name, getType(), setterFactory, asmPresent);
			classMeta = meta;
		}
		return meta;
	}
	
	
}
