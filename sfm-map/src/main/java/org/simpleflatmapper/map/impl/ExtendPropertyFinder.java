package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.Predicate;
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
        super();
        this.delegate = delegate;
        this.customProperties = customProperties;
        this.transformerFunction = transformerFunction;
    }

    @Override
    public void lookForProperties(final PropertyNameMatcher propertyNameMatcher, Object[] properties, final FoundProperty<T> matchingProperties, final PropertyMatchingScore score, final boolean allowSelfReference, final PropertyFinderTransformer propertyFinderTransformer, TypeAffinityScorer typeAffinityScorer, PropertyFilter propertyFilter, ShortCircuiter shortCircuiter) {
        for (CustomProperty<?, ?> property : customProperties) {
            if (property.isApplicable(delegate.getOwnerType()) && propertyNameMatcher.matches(property.getName()) != null) {
                matchingProperties.found((CustomProperty<T, ?>) property, EMPTY_CALLBACK, score.matches(null, propertyNameMatcher, new PropertyNameMatch(property.getName(), property.getName(), null, propertyNameMatcher.asScore(), 0)), typeAffinityScorer);
            }
        }

        PropertyFinderTransformer newTransformer = transformerFunction.apply(propertyFinderTransformer);

        delegate
                .lookForProperties(
                        propertyNameMatcher,
                        properties, matchingProperties,
                        score.arrayIndex(new IndexedColumn(1, propertyNameMatcher, 0), false), // move laterally ?,
                        allowSelfReference,
                        newTransformer, typeAffinityScorer, propertyFilter, shortCircuiter);
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

        @Override
        public PropertyMeta<T, P> withReflectionService(ReflectionService reflectionService) {
            return new CustomProperty<T, P>(getOwnerType(), reflectionService, getName(), type, setter, getter);
        }

        @Override
        public PropertyMeta<T, P> toNonMapped() {
            throw new UnsupportedOperationException();
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
