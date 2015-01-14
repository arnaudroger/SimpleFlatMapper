package org.sfm.reflect.meta;

import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;


public abstract class PropertyMeta<O, P> {
	private final String name;
	private final String column;
	
	protected final ReflectionService reflectService;
	
	private volatile Setter<O, P> setter;
	private volatile ClassMeta<P> classMeta;
	private String path;

	public PropertyMeta(String name, String column, ReflectionService reflectService) {
		this.name = name;
		this.column = column;
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

	public final String getColumn() {
		return column;
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
		return reflectService.getClassMeta(getType(), false);
	}

	public boolean isPrimitive() {
		return TypeHelper.isPrimitive(getType());
	}

	public boolean isConstructorProperty() {
		return false;
	}

	public abstract String getPath();

	public boolean isSubProperty() {
		return false;
	}


}
