package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ScoredSetter;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.ScoredGetter;
import org.simpleflatmapper.reflect.Setter;

import java.lang.reflect.Type;

public class ConstructorPropertyMeta<T, P> extends PropertyMeta<T, P> {

    private final Class<T> owner;
    private final ScoredSetter<T, P> scoredSetter;
    private final ScoredGetter<T, P> scoredGetter;
    private final Parameter parameter;
    private final InstantiatorDefinition instantiatorDefinition;

    public ConstructorPropertyMeta(String name,
                                   ReflectionService reflectService,
                                   Parameter parameter,
                                   Class<T> owner, InstantiatorDefinition instantiatorDefinition) {
        this(name, reflectService, parameter, owner, ScoredGetter.<T, P>nullGetter(), ScoredSetter.<T, P>nullSetter(), instantiatorDefinition);
    }

    public ConstructorPropertyMeta(String name,
                                   ReflectionService reflectService,
                                   Parameter parameter,
                                   Class<T> owner,
                                    ScoredGetter<T, P> scoredGetter,
                                    ScoredSetter<T, P> scoredSetter,
                                   InstantiatorDefinition instantiatorDefinition) {
		super(name, reflectService);
		this.parameter = parameter;
        this.owner = owner;
        this.scoredGetter = scoredGetter;
        this.scoredSetter = scoredSetter;
        this.instantiatorDefinition = instantiatorDefinition;
    }

    public int getConstructorParameterSize() {
        return instantiatorDefinition.getParameters().length;
    }

	@Override
	public Setter<? super T, ? super P> getSetter() {
        return scoredSetter.getSetter();
	}

    @Override
    public Getter<? super T, ? extends P> getGetter() {
        return scoredGetter.getGetter();
    }

    public ConstructorPropertyMeta<T, P> getter(ScoredGetter<T, P> getter) {
        if (getter.isBetterThan(this.scoredGetter)) {
            return new ConstructorPropertyMeta<T, P>(getName(), reflectService, parameter, owner, getter, scoredSetter, instantiatorDefinition);
        } else {
            return this;
        }
    }

    public ConstructorPropertyMeta<T, P> setter(ScoredSetter<T, P> setter) {
        if (setter.isBetterThan(this.scoredSetter)) {
            return new ConstructorPropertyMeta<T, P>(getName(), reflectService, parameter, owner, scoredGetter, setter, instantiatorDefinition);
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
	public String getPath() {
		return getName();
	}

    @Override
    public String toString() {
        return "ConstructorPropertyMeta{" +
                "owner=" + owner +
                ", constructorParameter=" + parameter +
                '}';
    }
}
