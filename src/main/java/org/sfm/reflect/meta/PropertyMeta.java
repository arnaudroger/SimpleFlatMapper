package org.sfm.reflect.meta;

import java.lang.reflect.Type;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;


public abstract class PropertyMeta<O, P> {
	private final String name;
	protected final ReflectionService reflectService;
	
	private volatile Setter<O, P> setter;
	private volatile ClassMeta<P> classMeta;
	public PropertyMeta(String name, ReflectionService reflectService) {
		this.name = name;
		this.reflectService = reflectService;
	}

	public final Setter<O, P> getSetter() {
		Setter<O, P> lsetter = setter;
		if (lsetter == null) {
			lsetter = newSetter();
			setter = lsetter;
		}
		return lsetter;
	}

	protected abstract Setter<O, P> newSetter();

	public final String getName() {
		return name;
	}

	public abstract Type getType();

	public final ClassMeta<P> getClassMeta() {
		ClassMeta<P> meta = classMeta;
		if (meta == null) {
			meta = newClassMeta();
			classMeta = meta;
		}
		return meta;
	}

	protected ClassMeta<P> newClassMeta() {
		return reflectService.getClassMeta(getType());
	}

	public boolean isPrimitive() {
		return TypeHelper.isPrimitive(getType());
	}

	public boolean isConstructorProperty() {
		return false;
	}

	public boolean isSubProperty() {
		return false;
	}
	
}
