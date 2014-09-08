package org.sfm.reflect.meta;

import java.lang.reflect.Type;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;


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

	public abstract Type getType();

	public final ClassMeta<T> getClassMeta() {
		ClassMeta<T> meta = classMeta;
		if (meta == null) {
			meta = newClassMeta();
			classMeta = meta;
		}
		return meta;
	}

	protected ClassMeta<T> newClassMeta() {
		return reflectService.getClassMeta(getType());
	}

	public boolean isPrimitive() {
		return TypeHelper.isPrimitive(getType());
	}
	
}
