package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ScoredSetter;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.ScoredGetter;
import org.simpleflatmapper.reflect.Setter;

import java.lang.reflect.Type;

public class ConstructorPropertyMeta<T, P> extends PropertyMeta<T, P> {

    private final ScoredSetter<T, P> scoredSetter;
    private final ScoredGetter<T, P> scoredGetter;
    private final Parameter parameter;
    private final InstantiatorDefinition instantiatorDefinition;
    private final Object[] defineProperties;

    public ConstructorPropertyMeta(String name,
                                   Type ownerType,
                                   ReflectionService reflectService,
                                   Parameter parameter,
                                   InstantiatorDefinition instantiatorDefinition,
                                   Object[] defineProperties) {
        this(name, ownerType, reflectService, parameter, ScoredGetter.<T, P>nullGetter(), ScoredSetter.<T, P>nullSetter(), instantiatorDefinition, defineProperties);
    }

    public ConstructorPropertyMeta(String name,
                                   Type ownerType,
                                   ReflectionService reflectService,
                                   Parameter parameter,
                                   ScoredGetter<T, P> scoredGetter,
                                   ScoredSetter<T, P> scoredSetter,
                                   InstantiatorDefinition instantiatorDefinition, Object[] defineProperties) {
		super(name, ownerType, reflectService);
		this.parameter = parameter;
        this.scoredGetter = scoredGetter;
        this.scoredSetter = scoredSetter;
        this.instantiatorDefinition = instantiatorDefinition;
        this.defineProperties = defineProperties;
    }

	@Override
	public Setter<? super T, ? super P> getSetter() {
        return scoredSetter.getSetter();
	}

    @Override
    public Getter<? super T, ? extends P> getGetter() {
        return scoredGetter.getGetter();
    }

    public ConstructorPropertyMeta<T, P> defineProperties(Object[] defineProperties) {
        if (defineProperties != null) {
            return new ConstructorPropertyMeta<T, P>(getName(), getOwnerType(), reflectService, parameter, scoredGetter, scoredSetter, instantiatorDefinition, ObjectPropertyMeta.concatenate(this.defineProperties, defineProperties));
        } else {
            return this;
        }
    }


    public ConstructorPropertyMeta<T, P> getter(ScoredGetter<T, P> getter) {
        if (getter.isBetterThan(this.scoredGetter)) {
            return new ConstructorPropertyMeta<T, P>(getName(), getOwnerType(), reflectService, parameter, getter, scoredSetter, instantiatorDefinition, defineProperties);
        }  else {
            return this;
        }
    }

    public ConstructorPropertyMeta<T, P> setter(ScoredSetter<T, P> setter) {
        if (setter.isBetterThan(this.scoredSetter)) {
            return new ConstructorPropertyMeta<T, P>(getName(), getOwnerType(), reflectService, parameter, scoredGetter, setter, instantiatorDefinition, defineProperties);
        } else {
            return this;
        }
    }

    @Override
	public Type getPropertyType() {
		return parameter.getGenericType();
	}

	public Parameter getParameter() {
		return parameter;
	}

	public boolean isConstructorProperty() {
		return true;
	}

    @Override
    public Object[] getDefinedProperties() {
        return defineProperties;
    }

    @Override
	public String getPath() {
		return getName();
	}

    @Override
    public String toString() {
        return "ConstructorPropertyMeta{" +
                "owner=" + getOwnerType() +
                ", constructorParameter=" + parameter +
                '}';
    }

    public ConstructorPropertyMeta<T,P> withReflectionService(ReflectionService reflectionService) {
        return new ConstructorPropertyMeta<T, P>(getName(), getOwnerType(), reflectionService, parameter, scoredGetter, scoredSetter, instantiatorDefinition, defineProperties);
    }

    @Override
    public PropertyMeta<T, P> toNonMapped() {
        throw new UnsupportedOperationException();
    }


}
