package org.simpleflatmapper.core.reflect.meta;

import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.ReflectionService;
import org.simpleflatmapper.core.reflect.Setter;

import java.lang.reflect.Type;

/**
 *
 * @param <O> the type of the property owner
 * @param <P> the type of the property
 */
public abstract class PropertyMeta<O, P> {
	private final String name;

	protected final ReflectionService reflectService;
	
	private volatile ClassMeta<P> classMeta;

	public PropertyMeta(String name, ReflectionService reflectService) {
		this.name = name;
		this.reflectService = reflectService;
	}

	public abstract Setter<O, P> getSetter();

    public abstract Getter<O, P> getGetter();

	public final String getName() {
		return name;
	}

	public abstract Type getPropertyType();

	public final ClassMeta<P> getPropertyClassMeta() {
		ClassMeta<P> meta = classMeta;
		if (meta == null) {
			meta = newPropertyClassMeta();
			classMeta = meta;
		}
		return meta;
	}

	protected ClassMeta<P> newPropertyClassMeta() {
		return reflectService.getClassMeta(getPropertyType());
	}

	public boolean isConstructorProperty() {
		return false;
	}

	public abstract String getPath();

	public boolean isSubProperty() {
		return false;
	}


	public boolean isDirect() {
		return false;
	}
}
