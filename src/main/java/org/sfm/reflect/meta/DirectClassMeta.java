package org.sfm.reflect.meta;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;

import java.lang.reflect.Type;
import java.util.List;

public final class DirectClassMeta<T> implements ClassMeta<T> {


	private final ReflectionService reflectService;
	private final Type target;

	public DirectClassMeta(Type target, ReflectionService reflectService) throws MapperBuildingException {
		this.target = target;
		this.reflectService = reflectService;
	}


    @Override
    public ReflectionService getReflectionService() {
        return reflectService;
    }

    @Override
    public PropertyFinder<T> newPropertyFinder() {
        return new DirectPropertyFinder();
    }

    @Override
    public Type getType() {
        return target;
    }

    @Override
    public String[] generateHeaders() {
        throw new UnsupportedOperationException();
    }


    public class DirectPropertyFinder implements PropertyFinder<T> {

        @Override
        public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {
            return new DirectPropertyMeta<E>("direct", "direct", reflectService, target);
        }

        @Override
        public List<ConstructorDefinition<T>> getEligibleConstructorDefinitions() {
            return null;
        }
    }

    public class DirectPropertyMeta<E> extends PropertyMeta<T, E> {
        private final Type type;

        public DirectPropertyMeta(String name, String column, ReflectionService reflectService, Type type) {
            super(name, column, reflectService);
            this.type = type;
        }


        @Override
        protected Setter<T, E> newSetter() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Getter<T, E> newGetter() {
            throw new UnsupportedOperationException();

        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public String getPath() {
            return getName();
        }
    }
}
