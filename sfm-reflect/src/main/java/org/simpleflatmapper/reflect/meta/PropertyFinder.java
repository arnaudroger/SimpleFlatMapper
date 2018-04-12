package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PropertyFinder<T> {
	protected final Predicate<PropertyMeta<?, ?>> propertyFilter;
	private final boolean selfScoreFullName;

	protected PropertyFinder(Predicate<PropertyMeta<?, ?>> propertyFilter, boolean selfScoreFullName) {
		this.propertyFilter = propertyFilter;
		this.selfScoreFullName = selfScoreFullName;
	}

	public final <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher, Object[] properties, TypeAffinity typeAffinity) {
		return findProperty(propertyNameMatcher, properties, toTypeAffinityScorer(typeAffinity));
	}
	public final <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher, Object[] properties, TypeAffinityScorer typeAffinity) {
		return findProperty(propertyNameMatcher, properties, typeAffinity, new DefaultPropertyFinderProbe(propertyNameMatcher));
	}

	public final <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher, Object[] properties, TypeAffinity typeAffinity, PropertyFinderProbe propertyFinderProbe) {
		return findProperty(propertyNameMatcher, properties, toTypeAffinityScorer(typeAffinity), propertyFinderProbe);
	}
		@SuppressWarnings("unchecked")
	public final <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher, Object[] properties, TypeAffinityScorer typeAffinity, PropertyFinderProbe propertyFinderProbe) {
		MatchingProperties matchingProperties = new MatchingProperties(propertyFilter, propertyFinderProbe);
		lookForProperties(propertyNameMatcher, properties, matchingProperties, PropertyMatchingScore.newInstance(selfScoreFullName), true, IDENTITY_TRANSFORMER,  typeAffinity);
		return (PropertyMeta<T, E>)matchingProperties.selectBestMatch();
	}

	private TypeAffinityScorer toTypeAffinityScorer(TypeAffinity typeAffinity) {
		return new TypeAffinityScorer(typeAffinity);
	}

	public abstract void lookForProperties(
			PropertyNameMatcher propertyNameMatcher,
			Object[] properties,
			FoundProperty<T> matchingProperties,
			PropertyMatchingScore score,
			boolean allowSelfReference,
			PropertyFinderTransformer propertyFinderTransformer, 
			TypeAffinityScorer typeAffinityScorer);


	public abstract List<InstantiatorDefinition> getEligibleInstantiatorDefinitions();

    public abstract PropertyFinder<?> getSubPropertyFinder(PropertyMeta<?, ?> owner);
	public abstract PropertyFinder<?> getOrCreateSubPropertyFinder(SubPropertyMeta<?, ?, ?> subPropertyMeta);

	public Predicate<PropertyMeta<?, ?>> getPropertyFilter() {
		return propertyFilter;
	}

	public abstract Type getOwnerType();

	public boolean selfScoreFullName() {
		return selfScoreFullName;
	}

	protected static class MatchingProperties<T> implements FoundProperty<T> {
		private final List<MatchedProperty<T, ?>> matchedProperties = new ArrayList<MatchedProperty<T, ?>>();
		private final Predicate<PropertyMeta<?, ?>> propertyFilter;
		private final PropertyFinderProbe propertyFinderProbe;

		public MatchingProperties(Predicate<PropertyMeta<?, ?>> propertyFilter, PropertyFinderProbe propertyFinderProbe) {
			this.propertyFilter = propertyFilter;
			this.propertyFinderProbe = propertyFinderProbe;
		}

		@Override
		public <P extends  PropertyMeta<T, ?>> void found(P propertyMeta,
														  Runnable selectionCallback,
														  PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {
			if (propertyFilter.test(propertyMeta)) {
				propertyFinderProbe.found(propertyMeta, score);
				matchedProperties.add(new MatchedProperty<T, P>(propertyMeta, selectionCallback, score, typeAffinityScorer.score(propertyMeta.getPropertyType())));
			}
		}

		public PropertyMeta<T, ?> selectBestMatch() {
			if (matchedProperties.isEmpty()) return null;
			Collections.sort(matchedProperties);
			MatchedProperty<T, ?> selectedMatchedProperty = matchedProperties.get(0);
			selectedMatchedProperty.select();
			
			propertyFinderProbe.select(selectedMatchedProperty.propertyMeta);
			return selectedMatchedProperty.propertyMeta;
			
		}
	}

	private static class MatchedProperty<T, P extends PropertyMeta<T, ?>> implements Comparable<MatchedProperty<T, ?>>{
		private final P propertyMeta;
		private final Runnable selectionCallback;
		private final PropertyMatchingScore score;
		private final int typeAffinityScore;

		private MatchedProperty(P propertyMeta, Runnable selectionCallback, PropertyMatchingScore score, int typeAffinityScore) {
			this.propertyMeta = propertyMeta;
			this.selectionCallback = selectionCallback;
			this.score = score;
			this.typeAffinityScore = typeAffinityScore;
		}


		@Override
		public int compareTo(MatchedProperty<T, ?> o) {
			int i =  this.score.compareTo(o.score);
			
			if (i == 0) {
				i = (typeAffinityScore > o.typeAffinityScore) ? -1 : ((typeAffinityScore == o.typeAffinityScore) ? 0 : 1);
				if (i == 0) {
					return compare(this.propertyMeta, o.propertyMeta);
				} else {
					return i;
				}
			} else {
				return i;
			}
		}

		private int compare(PropertyMeta<?, ?> p1, PropertyMeta<?, ?> p2) {
			if (p1.isConstructorProperty()) {
				if (!p2.isConstructorProperty()) {
					return -1;
				}
			} else if (p2.isConstructorProperty()) {
				return 1;
			} else if (!p1.isSelf()) {
				if (p2.isSelf()) {
					return -1;
				}
			} else if (!p2.isSelf()) {
				return 1;
			} else if (!p1.isSubProperty()) {
				if (p2.isSubProperty()) {
					return -1;
				}
			} else if (!p2.isSubProperty()) {
				return 1;
			}
			return getterSetterCompare(p1, p2);
		}

		private int getterSetterCompare(PropertyMeta<?, ?> p1, PropertyMeta<?, ?> p2) {
			return nbGetterSetter(p2) - nbGetterSetter(p1);
		}

		private int nbGetterSetter(PropertyMeta<?, ?> p) {
			int c = 0;
			if (!NullGetter.isNull(p.getGetter())) {
				c++;
			}
			if (!NullSetter.isNull(p.getSetter())) {
				c++;
			}
			return c;
		}

		public void select() {
			if (selectionCallback != null) selectionCallback.run();
		}

		@Override
		public String toString() {
			return "MatchedProperty{" +
					"propertyMeta=" + propertyMeta.getPath() +
					", score=" + score + ":" + nbGetterSetter(propertyMeta) +
					", getter=" + propertyMeta.getGetter() +
					", setter=" + propertyMeta.getSetter() +
					'}';
		}
	}

	public static class TypeAffinityScorer {

		private Class<?>[] affinities;
		public TypeAffinityScorer(TypeAffinity typeAffinity) {
			if (typeAffinity == null || typeAffinity.getAffinities() == null) {
				affinities = new Class[0];
			} else {
				affinities = typeAffinity.getAffinities();
			}
		}

		public TypeAffinityScorer(Class<?>[] affinities) {
			this.affinities = affinities;
		}

		public int score(Type type) {
			for(int i = 0; i < affinities.length; i++) {
				if (TypeHelper.isAssignable(type, affinities[i])) {
					return affinities.length - i;
				}
			}
			return -1;
		}
	}
	
    public interface FoundProperty<T> {
        <P extends  PropertyMeta<T, ?>> void found(P propertyMeta,
												   Runnable selectionCallback,
												   PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer);
    }


	public void manualMatch(PropertyMeta<?, ?> prop) {
		if (prop.isSubProperty()) {
			SubPropertyMeta subPropertyMeta = (SubPropertyMeta) prop;
			PropertyMeta ownerProperty = subPropertyMeta.getOwnerProperty();
			
			manualMatch(ownerProperty);
			
			PropertyFinder<?> subPropertyFinder = getOrCreateSubPropertyFinder(subPropertyMeta);
			subPropertyFinder.manualMatch(subPropertyMeta.getSubProperty());
		}
	}

    public interface PropertyFinderTransformer {
		<T> PropertyFinder<T> apply(PropertyFinder<T> propertyFinder);
	}

	public static PropertyFinderTransformer IDENTITY_TRANSFORMER = new PropertyFinderTransformer() {
		@Override
		public <T> PropertyFinder<T> apply(PropertyFinder<T> propertyFinder) {
			return propertyFinder;
		}

		@Override
		public String toString() {
			return "IDENTITY_TRANSFORMER";
		}
	};
	
	
	public interface PropertyFinderProbe {

		void found(PropertyMeta propertyMeta, PropertyMatchingScore score);

		void select(PropertyMeta propertyMeta);
	}
	
	public static class DefaultPropertyFinderProbe implements PropertyFinderProbe {
		private static final boolean DEBUG = Boolean.getBoolean("org.simpleflatmapper.probe.propertyFinder");
		
		private final PropertyNameMatcher propertyNameMatcher;

		public DefaultPropertyFinderProbe(PropertyNameMatcher propertyNameMatcher) {
			this.propertyNameMatcher = propertyNameMatcher;
		}

		@Override
		public void found(PropertyMeta propertyMeta, PropertyMatchingScore score) {
			if (DEBUG) {
				System.out.println("PropertyFinder for '" + propertyNameMatcher + "' - found " + score + " " + propertyMeta.getPath());
			}
		}

		@Override
		public void select(PropertyMeta propertyMeta) {
			if (DEBUG) {
				System.out.println("PropertyFinder for '" + propertyNameMatcher + "' - select " + propertyMeta.getPath());
			}
		}
	}
}