package org.sfm.reflect.meta;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;


public abstract class PropertyMeta<T, P> {
	private final String name;
	protected final ReflectionService reflectService;
	
	private volatile Setter<T, P> setter;
	private volatile ClassMeta<T> classMeta;
	public PropertyMeta(String name, ReflectionService reflectService) {
		this.name = name;
		this.reflectService = reflectService;
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

	public final ClassMeta<T> getClassMeta() {
		ClassMeta<T> meta = classMeta;
		if (meta == null) {
			meta = reflectService.getClassMeta(name, getType());
			classMeta = meta;
		}
		return meta;
	}
	
}
