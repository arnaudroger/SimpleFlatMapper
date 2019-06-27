package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.Supplier;

import java.lang.reflect.Type;

/**
 *
 * @param <O> the type of the property owner
 * @param <P> the type of the property
 */
public abstract class PropertyMeta<O, P> {
	private final String name;
	private final Type ownerType;

	protected final ReflectionService reflectService;

	private volatile ClassMeta<P> classMeta;

	public PropertyMeta(String name, Type ownerType, ReflectionService reflectService) {
		this.name = name;
		this.ownerType = ownerType;
		this.reflectService = reflectService;
	}

	public abstract Setter<? super O, ? super P> getSetter();

    public abstract Getter<? super O, ? extends P> getGetter();

	public final String getName() {
		return name;
	}

	public abstract Type getPropertyType();

	public Type getOwnerType() {
		return ownerType;
	}

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


	public boolean isSelf() {
		return false;
	}

	public boolean isValid() {
		return true;
	}

	public final ReflectionService getReflectService () {
		return reflectService;
	}

	public Supplier<ClassMeta<P>> getPropertyClassMetaSupplier() {
		return new Supplier<ClassMeta<P>>() {
			@Override
			public ClassMeta<P> get() {
				return getPropertyClassMeta();
			}
		};
	}

	public Object[] getDefinedProperties() {
		return new Object[0];
	}

	public abstract PropertyMeta<O,P> withReflectionService(ReflectionService reflectionService);

	public int typeAffinityScore(PropertyFinder.TypeAffinityScorer typeAffinityScorer) {
		return typeAffinityScorer.score(getPropertyType());
	}

	public boolean isNonMapped() {
		return false;
	}

	public abstract PropertyMeta<O, P> toNonMapped();

	public PropertyMeta<O, P> compressSubSelf() {
		return this;
	}
}
