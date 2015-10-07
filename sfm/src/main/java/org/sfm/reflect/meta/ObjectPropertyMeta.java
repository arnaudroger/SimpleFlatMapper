package org.sfm.reflect.meta;

import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterHelper;
import org.sfm.reflect.impl.FieldGetter;
import org.sfm.reflect.impl.FieldSetter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class ObjectPropertyMeta<T, P> extends PropertyMeta<T, P> {

	private final Setter<T, P> setter;
    private final Getter<T, P> getter;
	private final Type type;

	public ObjectPropertyMeta(String name, ReflectionService reflectService, Type type, Getter<T, P> getter, Setter<T, P> setter) {
		super(name, reflectService);
		this.type = type;
        this.getter = getter;
        this.setter = setter;
	}


    public PropertyMeta<T, P> getterSetter(Getter<T, P> getter, Setter<T, P> setter) {
        Setter<T, P> newSetter = this.setter;
        if (this.setter == null || (this.setter instanceof FieldSetter && setter != null)) {
            newSetter = setter;
        }

        Getter<T, P> newGetter = this.getter;
        if (this.getter == null || (this.getter instanceof FieldGetter && getter != null)) {
            newGetter = getter;
        }

        return new ObjectPropertyMeta<T, P>(getName(), reflectService, type, newGetter, newSetter);
    }

	@Override
	public Setter<T, P> getSetter() {
		return setter;
	}

    @Override
    public Getter<T, P> getGetter() {
        return getter;
    }

    @Override
	public Type getPropertyType() {
		return type;
	}

	@Override
	public String getPath() {
		return getName();
	}

    @Override
    public String toString() {
        return "ObjectPropertyMeta{" +
                "name="+ getName() +
                ", type=" + type +
                ", setter=" + setter +
                ", getter=" + getter +
                '}';
    }

}
