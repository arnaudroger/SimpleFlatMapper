package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMatchingScore;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.List;

public class ExtendPropertyFinder<T> extends PropertyFinder<T> {
    private static final Runnable EMPTY_CALLBACK = new Runnable() { @Override public void run() { } };
    private static final Function<PropertyFinderTransformer, PropertyFinderTransformer> IDENTITY =
            new Function<PropertyFinderTransformer, PropertyFinderTransformer>() {
                @Override
                public PropertyFinderTransformer apply(PropertyFinderTransformer propertyFinderTransformer) {
                    return propertyFinderTransformer;
                }
            };
    private final PropertyFinder<T> delegate;
    private final List<CustomProperty<?, ?>> customProperties;
    private final Function<PropertyFinderTransformer, PropertyFinderTransformer> transformerFunction;

    public ExtendPropertyFinder(PropertyFinder<T> delegate,
                                List<CustomProperty<?, ?>> customProperties) {
        this(delegate, customProperties, IDENTITY);
    }

    public ExtendPropertyFinder(PropertyFinder<T> delegate,
                                List<CustomProperty<?, ?>> customProperties,
                                Function<PropertyFinderTransformer, 
                                PropertyFinderTransformer> transformerFunction) {
        super(delegate.getPropertyFilter(), delegate.selfScoreFullName());
        this.delegate = delegate;
        this.customProperties = customProperties;
        this.transformerFunction = transformerFunction;
    }

    @Override
    public void lookForProperties(final PropertyNameMatcher propertyNameMatcher, Object[] properties, final FoundProperty<T> matchingProperties, final PropertyMatchingScore score, final boolean allowSelfReference, final PropertyFinderTransformer propertyFinderTransformer, TypeAffinityScorer typeAffinityScorer) {
        for (CustomProperty<?, ?> property : customProperties) {
            if (property.isApplicable(delegate.getOwnerType()) && propertyNameMatcher.matches(property.getName())) {
                matchingProperties.found((CustomProperty<T, ?>) property, EMPTY_CALLBACK, score.matches(propertyNameMatcher), typeAffinityScorer);
            }
        }

        PropertyFinderTransformer newTransformer = transformerFunction.apply(propertyFinderTransformer);

        delegate
                .lookForProperties(
                        propertyNameMatcher,
                        properties, matchingProperties,
                        score.tupleIndex(1),
                        allowSelfReference,
                        newTransformer, typeAffinityScorer);
    }

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return delegate.getEligibleInstantiatorDefinitions();
    }

    @SuppressWarnings("unchecked")
    @Override
    public PropertyFinder<?> getSubPropertyFinder(PropertyMeta<?, ?> owner) {
        return delegate.getSubPropertyFinder(owner);
    }

    @Override
    public PropertyFinder<?> getOrCreateSubPropertyFinder(SubPropertyMeta<?, ?, ?> subPropertyMeta) {
        return delegate.getOrCreateSubPropertyFinder(subPropertyMeta);
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
