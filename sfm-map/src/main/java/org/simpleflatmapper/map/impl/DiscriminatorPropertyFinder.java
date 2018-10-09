package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMatchingScore;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscriminatorPropertyFinder<T> extends PropertyFinder<T> {
    private final Map<PropertyMeta<?, ?>, PropertyFinder<?>> subPropertyFinders = new HashMap<PropertyMeta<?, ?>, PropertyFinder<?>>();
    private final Type ownerType;
    private final List<PropertyFinder<? extends T>> implementationPropertyFinders;
    private final ReflectionService reflectionService;

    protected DiscriminatorPropertyFinder(Predicate<PropertyMeta<?, ?>> propertyFilter, boolean selfScoreFullName, Type ownerType, List<ClassMeta<?>> implemetations, ReflectionService reflectionService) {
        super(propertyFilter, selfScoreFullName);
        this.ownerType = ownerType;
        this.reflectionService = reflectionService;
        implementationPropertyFinders = new ArrayList<PropertyFinder<? extends T>>();
        for(ClassMeta<?> cm : implemetations) {
            implementationPropertyFinders.add((PropertyFinder<? extends T>) cm.newPropertyFinder(propertyFilter));
        }
    }

    @Override
    public void lookForProperties(PropertyNameMatcher propertyNameMatcher, Object[] properties, final FoundProperty<T> matchingProperties, PropertyMatchingScore score, boolean allowSelfReference, PropertyFinderTransformer propertyFinderTransformer, TypeAffinityScorer typeAffinityScorer) {
        
        final List<DiscriminatorMatch> matches = new ArrayList<DiscriminatorMatch>();

        PropertyMatchingScore bestScore = null;
        for(PropertyFinder<?> propertyFinder : implementationPropertyFinders) {
            List<MatchedProperty> matchedProperties = new ArrayList<MatchedProperty>();            
            
            propertyFinder.lookForProperties(propertyNameMatcher, properties,
                    new FoundProperty() {
                        @Override
                        public void found(PropertyMeta propertyMeta, Runnable selectionCallback, PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {
                            matchedProperties.add(new MatchedProperty(propertyMeta, selectionCallback, score, typeAffinityScorer.score(propertyMeta.getPropertyType())));  
                        }
                    }
                    , score, false, propertyFinderTransformer, typeAffinityScorer);
            
            if (!matchedProperties.isEmpty()) {
                Collections.sort(matchedProperties);
                MatchedProperty selectedMatchedProperty = matchedProperties.get(0);
                matches.add(new DiscriminatorMatch(propertyFinder.getOwnerType(), selectedMatchedProperty));
                
                if (bestScore == null || bestScore.compareTo(selectedMatchedProperty.getScore()) > 0) {
                    bestScore = selectedMatchedProperty.getScore();
                }
            }
        }
        
        if (!matches.isEmpty()) {
            
            DiscriminatorPropertyMeta<T, ?> discriminatorPropertyMeta = new DiscriminatorPropertyMeta("unknown", ownerType,  reflectionService, matches);
            Runnable selectionCallback = new Runnable() {
                @Override
                public void run() {
                    for(DiscriminatorMatch dm : matches) {
                        dm.matchedProperty.select();
                    }
                }
            };
            
            matchingProperties.found(discriminatorPropertyMeta, selectionCallback, bestScore, typeAffinityScorer);
        }
        
    }

    public <T> PropertyFinder<T> getImplementationPropertyFinder(Type implementationType) {
        for(PropertyFinder<?> pf : implementationPropertyFinders) {
            if(TypeHelper.areEquals(pf.getOwnerType(), implementationType)) {
                return (PropertyFinder<T>) pf;
            }
        }
        throw new IllegalArgumentException("Could not find implementation propertyfinder for " + implementationType);
    }

    private static class DiscriminatorMatch {
        private final Type type;
        private final MatchedProperty<?, ?> matchedProperty;

        private DiscriminatorMatch(Type type, MatchedProperty<?, ?> matchedProperty) {
            this.type = type;
            this.matchedProperty = matchedProperty;
        }
    }

    @Override
    public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
        return null;
    }

    @Override
    public PropertyFinder<?> getSubPropertyFinder(PropertyMeta<?, ?> owner) {
        return subPropertyFinders.get(owner);
    }

    @Override
    public PropertyFinder<?> getOrCreateSubPropertyFinder(SubPropertyMeta<?, ?, ?> subPropertyMeta) {
        PropertyFinder<?> propertyFinder = subPropertyFinders.get(subPropertyMeta.getOwnerProperty());

        if (propertyFinder == null) {
            propertyFinder = subPropertyMeta.getSubProperty().getPropertyClassMeta().newPropertyFinder(propertyFilter);
            subPropertyFinders.put(subPropertyMeta.getOwnerProperty(), propertyFinder);
        }

        return propertyFinder;
    }

    public void manualMatch(PropertyMeta<?, ?> prop) {
        if (!(prop instanceof DiscriminatorPropertyMeta)) {
            super.manualMatch(prop);
        }
        DiscriminatorPropertyMeta<?,?> dpm = (DiscriminatorPropertyMeta<?, ?>) prop;
        
        dpm.forEachProperty(new BiConsumer<Type, PropertyMeta<?, ?>>() {
            @Override
            public void accept(Type type, PropertyMeta<?, ?> propertyMeta) {
                PropertyFinder<?> pf = getImplementationPropertyFinder(type);
                pf.manualMatch(propertyMeta);
            }
        });
    }


    @Override
    public Type getOwnerType() {
        return ownerType;
    }
    
    
    public static class DiscriminatorPropertyMeta<O, P> extends PropertyMeta<O, P> {

        private final List<DiscriminatorMatch> matches;

        public DiscriminatorPropertyMeta(String name, Type ownerType, ReflectionService reflectService, List<DiscriminatorMatch> matches) {
            super(name, ownerType, reflectService);
            this.matches = matches;
        }
        
        public void forEachProperty(BiConsumer<Type, PropertyMeta<?, ?>> consumer) {
            for(DiscriminatorMatch dm : matches) {
                consumer.accept(dm.type, dm.matchedProperty.getPropertyMeta());
            }
        }

        @Override
        public Setter<? super O, ? super P> getSetter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Getter<? super O, ? extends P> getGetter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isConstructorProperty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSubProperty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Type getPropertyType() {
            return matches.get(0).matchedProperty.getPropertyMeta().getPropertyType();
        }

        @Override
        public String getPath() {
            return matches.get(0).matchedProperty.getPropertyMeta().getPath();
        }

        @Override
        public PropertyMeta<O, P> withReflectionService(ReflectionService reflectionService) {
            throw new UnsupportedOperationException();
        }
    }
}
