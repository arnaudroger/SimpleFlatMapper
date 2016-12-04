package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMatchingScore;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyNameMatcher;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.List;

public class ExtendPropertyFinder<T> extends PropertyFinder<T> {
    private final PropertyFinder<T> delegate;
    private final List<CustomProperty<?, ?>> customProperties;

    public ExtendPropertyFinder(PropertyFinder<T> delegate,
                                List<CustomProperty<?, ?>> customProperties) {
        super(delegate.getPropertyFilter());
        this.delegate = delegate;
        this.customProperties = customProperties;
    }

    @Override
    public void lookForProperties(final PropertyNameMatcher propertyNameMatcher, final FoundProperty<T> matchingProperties, final PropertyMatchingScore score, final boolean allowSelfReference, final PropertyFinderTransformer propertyFinderTransformer) {
        for (CustomProperty<?, ?> property : customProperties) {
            if (property.isApplicable(delegate.getOwnerType()) && propertyNameMatcher.matches(property.getName())) {
                matchingProperties.found((CustomProperty<T, ?>) property, new Runnable() {
                    @Override
                    public void run() {
                    }
                }, score);
            }
        }


        PropertyFinderTransformer newTransformer;

        if (propertyFinderTransformer == IDENTITY_TRANSFORMER) {
            newTransformer = new ExtendPropertyFinderTransformer(propertyFinderTransformer, customProperties);
        } else {
            newTransformer = propertyFinderTransformer;
        }

        PropertyFinder<T> effectivePropertyFinder = delegate;
        //propertyFinderTransformer.apply(delegate);

        effectivePropertyFinder
                .lookForProperties(
                        propertyNameMatcher,
                        matchingProperties,
                        score,
                        allowSelfReference,
                        newTransformer);
    }

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return delegate.getEligibleInstantiatorDefinitions();
    }

    @SuppressWarnings("unchecked")
    @Override
    public PropertyFinder<?> getSubPropertyFinder(String name) {
        return delegate.getSubPropertyFinder(name);
    }

    @Override
    public Type getOwnerType() {
        return delegate.getOwnerType();
    }

    public static class CustomProperty<T, P> extends PropertyMeta<T, P> {
        private final Type type;
        private final Setter<? super T, ? super P> setter;
        private final Getter<? super T, ? extends P> getter;

        public CustomProperty(
                Type ownerType,
                ReflectionService reflectService,
                String name,
                Type type,
                Setter<? super T, ? super P> setter,
                Getter<? super T, ? extends P> getter) {
            super(name, ownerType, reflectService);
            this.type = type;
            this.setter = setter;
            this.getter = getter;
        }

        @Override
        public Setter<? super T, ? super P> getSetter() {
            return setter;
        }

        @Override
        public Getter<? super T, ? extends P> getGetter() {
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

        public boolean isApplicable(Type ownerType) {
            return TypeHelper.isAssignable(getOwnerType(), ownerType);
        }

        @Override
        public boolean isConstructorProperty() {
            return false;
        }

        @Override
        public boolean isSubProperty() {
            return false;
        }

        @Override
        public boolean isSelf() {
            return false;
        }

        @Override
        public boolean isValid() {
            return true;
        }
    }


    public static class ExtendPropertyFinderTransformer implements PropertyFinder.PropertyFinderTransformer {
        private final PropertyFinder.PropertyFinderTransformer propertyFinderTransformer;
        private final List<CustomProperty<?, ?>> customProperties;

        public ExtendPropertyFinderTransformer(PropertyFinder.PropertyFinderTransformer propertyFinderTransformer, List<CustomProperty<?, ?>> customProperties) {
            this.propertyFinderTransformer = propertyFinderTransformer;
            this.customProperties = customProperties;
        }

        @Override
        public <T> PropertyFinder<T> apply(PropertyFinder<T> propertyFinder) {
            if (propertyFinder instanceof ExtendPropertyFinder)  {
                throw new IllegalStateException();
            }
            return new ExtendPropertyFinder<T>(propertyFinderTransformer.apply(propertyFinder), customProperties);
        }
    }
}
