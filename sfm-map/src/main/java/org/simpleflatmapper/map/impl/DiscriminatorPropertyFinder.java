package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.ClassMetaWithDiscriminatorId;
import org.simpleflatmapper.reflect.meta.*;
import org.simpleflatmapper.util.Consumer;
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
    private final List<PropertyFinderWithDiscriminatorId<T>> implementationPropertyFinders;
    private final ReflectionService reflectionService;

    protected DiscriminatorPropertyFinder(Type ownerType, List<ClassMetaWithDiscriminatorId<?>> implementations, ReflectionService reflectionService) {
        super( );
        this.ownerType = ownerType;
        this.reflectionService = reflectionService;
        implementationPropertyFinders = new ArrayList<PropertyFinderWithDiscriminatorId<T>>();
        for(ClassMetaWithDiscriminatorId<?> cm : implementations) {
            implementationPropertyFinders.add(new PropertyFinderWithDiscriminatorId<T>((PropertyFinder<? extends T>) cm.classMeta.newPropertyFinder(), cm.discriminatorId));
        }
    }

    @Override
    public void lookForProperties(PropertyNameMatcher propertyNameMatcher, Object[] properties, final FoundProperty<T> matchingProperties, PropertyMatchingScore score, boolean allowSelfReference, PropertyFinderTransformer propertyFinderTransformer, TypeAffinityScorer typeAffinityScorer, PropertyFilter propertyFilter, ShortCircuiter shortCircuiter) {
        
        final List<DiscriminatorMatch> matches = new ArrayList<DiscriminatorMatch>();

        PropertyMatchingScore bestScore = null;
        for(PropertyFinderWithDiscriminatorId pfwd : implementationPropertyFinders) {

            final List<MatchedProperty> matchedProperties = new ArrayList<MatchedProperty>();            
            
            pfwd.propertyFinder.lookForProperties(propertyNameMatcher, properties,
                    new FoundProperty() {
                        @Override
                        public void found(PropertyMeta propertyMeta, Runnable selectionCallback, PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {
                            matchedProperties.add(new MatchedProperty(propertyMeta, selectionCallback, score, typeAffinityScorer.score(propertyMeta.getPropertyType())));  
                        }
                    }
                    , score, false, propertyFinderTransformer, typeAffinityScorer, propertyFilter, shortCircuiter);
            
            if (!matchedProperties.isEmpty()) {
                Collections.sort(matchedProperties);
                MatchedProperty selectedMatchedProperty = matchedProperties.get(0);
                matches.add(new DiscriminatorMatch(pfwd.propertyFinder.getOwnerType(), selectedMatchedProperty, pfwd.discriminatorId));
                
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

    public  PropertyFinder<? extends T> getImplementationPropertyFinder(Type implementationType, Object discriminatorId) {
        for(PropertyFinderWithDiscriminatorId<T> pf : implementationPropertyFinders) {
            if(TypeHelper.areEquals(pf.propertyFinder.getOwnerType(), implementationType) && MapperConfig.sameDiscriminatorId(discriminatorId, pf.discriminatorId)) {
                return  pf.propertyFinder;
            }
        }
        throw new IllegalArgumentException("Could not find implementation propertyfinder for " + implementationType);
    }

    public static class DiscriminatorMatch {
        public final Type type;
        public final MatchedProperty<?, ?> matchedProperty;
        public final Object discriminatorId;

        private DiscriminatorMatch(Type type, MatchedProperty<?, ?> matchedProperty, Object discriminatorId) {
            this.type = type;
            this.matchedProperty = matchedProperty;
            this.discriminatorId = discriminatorId;
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
            propertyFinder = subPropertyMeta.getSubProperty().getPropertyClassMeta().newPropertyFinder();
            subPropertyFinders.put(subPropertyMeta.getOwnerProperty(), propertyFinder);
        }

        return propertyFinder;
    }

    public void manualMatch(PropertyMeta<?, ?> prop) {
        if (!(prop instanceof DiscriminatorPropertyMeta)) {
            super.manualMatch(prop);
        }
        DiscriminatorPropertyMeta<?,?> dpm = (DiscriminatorPropertyMeta<?, ?>) prop;
        
        dpm.forEachProperty(new Consumer<DiscriminatorMatch>() {
            @Override
            public void accept(DiscriminatorMatch dm) {
                Type type = dm.type;
                PropertyMeta<?, ?> propertyMeta = dm.matchedProperty.getPropertyMeta();
                PropertyFinder<?> pf = getImplementationPropertyFinder(type, dm.discriminatorId);
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

        public <C extends Consumer<DiscriminatorMatch>> C forEachProperty(C consumer) {
            for(DiscriminatorMatch dm : matches) {
                consumer.accept(dm);
            }
            return consumer;
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
            Type t = null;

            int i = 0;
            while (i < matches.size()){
                Type te = matches.get(0).matchedProperty.getPropertyMeta().getPropertyType();
                if (t == null) {
                    t = te;
                } else if (!t.equals(te)) {
                    throw new UnsupportedOperationException();
                }
                i++;
            }

            return t;
        }

        @Override
        public String getPath() {
            return matches.get(0).matchedProperty.getPropertyMeta().getPath();
        }

        @Override
        public PropertyMeta<O, P> withReflectionService(ReflectionService reflectionService) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int typeAffinityScore(TypeAffinityScorer typeAffinityScorer) {
            int bestScore = -1;
            for(DiscriminatorMatch dm : matches) {
                bestScore = Math.max(bestScore,  dm.matchedProperty.getPropertyMeta().typeAffinityScore(typeAffinityScorer));
            }
            return bestScore;
        }

        @Override
        public PropertyMeta<O, P> toNonMapped() {
            throw new UnsupportedOperationException();
        }
    }

    private static class PropertyFinderWithDiscriminatorId<T> {
        private final PropertyFinder<? extends T> propertyFinder;
        private final Object discriminatorId;

        private PropertyFinderWithDiscriminatorId(PropertyFinder<? extends T> propertyFinder, Object discriminatorId) {
            this.propertyFinder = propertyFinder;
            this.discriminatorId = discriminatorId;
        }
    }
}
