package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class PropertyFinder<T> {

	public final <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher, Object[] properties, TypeAffinityScorer typeAffinity, PropertyFilter propertyFilter, ShortCircuiter shortCircuiter) {
		MatchingProperties matchingProperties = new MatchingProperties(new DefaultPropertyFinderProbe(propertyNameMatcher));
		lookForProperties(propertyNameMatcher, properties, matchingProperties, PropertyMatchingScore.newInstance(), true, IDENTITY_TRANSFORMER,  typeAffinity, propertyFilter, shortCircuiter);
		return (PropertyMeta<T, E>)matchingProperties.selectBestMatch();
	}

	public final <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher, Object[] properties, TypeAffinity typeAffinity, PropertyFilter propertyFilter) {
		return findProperty(propertyNameMatcher, properties, toTypeAffinityScorer(typeAffinity), propertyFilter);
	}
	public final <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher, Object[] properties, TypeAffinityScorer typeAffinity, PropertyFilter propertyFilter) {
		return findProperty(propertyNameMatcher, properties, typeAffinity, new DefaultPropertyFinderProbe(propertyNameMatcher), propertyFilter);
	}

	public final <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher, Object[] properties, TypeAffinity typeAffinity, PropertyFinderProbe propertyFinderProbe, PropertyFilter propertyFilter) {
		return findProperty(propertyNameMatcher, properties, toTypeAffinityScorer(typeAffinity), propertyFinderProbe, propertyFilter);
	}
		@SuppressWarnings("unchecked")
	public final <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher, Object[] properties, TypeAffinityScorer typeAffinity, PropertyFinderProbe propertyFinderProbe, PropertyFilter propertyFilter) {
		MatchingProperties matchingProperties = new MatchingProperties(propertyFinderProbe);
		lookForProperties(propertyNameMatcher, properties, matchingProperties, PropertyMatchingScore.newInstance(), true, IDENTITY_TRANSFORMER,  typeAffinity, propertyFilter);
		return (PropertyMeta<T, E>)matchingProperties.selectBestMatch();
	}

	private TypeAffinityScorer toTypeAffinityScorer(TypeAffinity typeAffinity) {
		return new TypeAffinityScorer(typeAffinity);
	}

	public final void lookForProperties(
            PropertyNameMatcher propertyNameMatcher,
            Object[] properties,
            FoundProperty<T> matchingProperties,
            PropertyMatchingScore score,
            boolean allowSelfReference,
            PropertyFinderTransformer propertyFinderTransformer,
            TypeAffinityScorer typeAffinityScorer,
			PropertyFilter propertyFilter) {
		lookForProperties(propertyNameMatcher, properties, matchingProperties, score, allowSelfReference, propertyFinderTransformer, typeAffinityScorer, propertyFilter, new ShortCircuiter());
	}


	public abstract void lookForProperties(
			PropertyNameMatcher propertyNameMatcher,
			Object[] properties,
			FoundProperty<T> matchingProperties,
			PropertyMatchingScore score,
			boolean allowSelfReference,
			PropertyFinderTransformer propertyFinderTransformer,
			TypeAffinityScorer typeAffinityScorer,
			PropertyFilter propertyFilter,
			ShortCircuiter shortcircuiter);


	public abstract List<InstantiatorDefinition> getEligibleInstantiatorDefinitions();

    public abstract PropertyFinder<?> getSubPropertyFinder(PropertyMeta<?, ?> owner);
	public abstract PropertyFinder<?> getOrCreateSubPropertyFinder(SubPropertyMeta<?, ?, ?> subPropertyMeta);

	public abstract Type getOwnerType();

	@Deprecated
	public boolean selfScoreFullName() {
		return false;
	}

	protected static class MatchingProperties<T> implements FoundProperty<T> {
		private final List<MatchedProperty<T, ?>> matchedProperties = new ArrayList<MatchedProperty<T, ?>>();
		private final PropertyFinderProbe propertyFinderProbe;

		public MatchingProperties(PropertyFinderProbe propertyFinderProbe) {
			this.propertyFinderProbe = propertyFinderProbe;
		}

		@Override
		public <P extends  PropertyMeta<T, ?>> void found(P propertyMeta,
														  Runnable selectionCallback,
														  PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {
			propertyFinderProbe.found(propertyMeta, score);
			matchedProperties.add(new MatchedProperty<T, P>(propertyMeta, selectionCallback, score, propertyMeta.typeAffinityScore(typeAffinityScorer)));
		}

		public PropertyMeta<T, ?> selectBestMatch() {
			if (matchedProperties.isEmpty()) return null;

			Iterator<MatchedProperty<T, ?>> iterator = matchedProperties.iterator();

			MatchedProperty<T, ?> highestCore = iterator.next();

			while(iterator.hasNext()) {
				MatchedProperty<T, ?> next = iterator.next();
				if (next.compareTo(highestCore) < 0) {
					highestCore = next;
				}
			}

			highestCore.select();
			
			propertyFinderProbe.select(highestCore.propertyMeta);
			return highestCore.propertyMeta;
			
		}
	}

	public static class MatchedProperty<T, P extends PropertyMeta<T, ?>> implements Comparable<MatchedProperty<T, ?>>{
		private final P propertyMeta;
		private final Runnable selectionCallback;
		private final PropertyMatchingScore score;
		private final int typeAffinityScore;

		public MatchedProperty(P propertyMeta, Runnable selectionCallback, PropertyMatchingScore score, int typeAffinityScore) {
			this.propertyMeta = propertyMeta;
			this.selectionCallback = selectionCallback;
			this.score = score;
			this.typeAffinityScore = typeAffinityScore;
		}

		public PropertyMatchingScore getScore() {
			return score;
		}

		public P getPropertyMeta() {
			return propertyMeta;
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

			try {
				if (!NullGetter.isNull(p.getGetter())) {
					c++;
				}
			} catch (UnsupportedOperationException e) {}

			try {
				if (!NullSetter.isNull(p.getSetter())) {
					c++;
				}
			} catch (UnsupportedOperationException e) {

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
												   PropertyMatchingScore score,
												   TypeAffinityScorer typeAffinityScorer);
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

	public static final class PropertyFilter {
		private final Predicate<PropertyMeta<?, ?>> propertyMetaPredicate;
		private final Predicate<PropertyMeta<?, ?>> pathMetaPredicate;

		private static final PropertyFilter TRUE_FILTER = new PropertyFilter(ConstantPredicate.<PropertyMeta<?, ?>>truePredicate(), ConstantPredicate.<PropertyMeta<?, ?>>truePredicate());

		public PropertyFilter(Predicate<PropertyMeta<?, ?>> predicate) {
			this.propertyMetaPredicate = predicate;
			this.pathMetaPredicate = predicate;
		}

		public PropertyFilter(Predicate<PropertyMeta<?, ?>> propertyMetaPredicate, Predicate<PropertyMeta<?, ?>> pathMetaPredicate) {
			this.propertyMetaPredicate = propertyMetaPredicate;
			this.pathMetaPredicate = pathMetaPredicate;
		}

		public static PropertyFilter trueFilter() {
			return TRUE_FILTER;
		}

		public boolean testProperty(PropertyMeta<?, ?> propertyMeta) {
			return propertyMetaPredicate.test(propertyMeta);
		}
		public boolean testPath(PropertyMeta<?, ?> propertyMeta) {
			return pathMetaPredicate.test(propertyMeta);
		}
	}
}