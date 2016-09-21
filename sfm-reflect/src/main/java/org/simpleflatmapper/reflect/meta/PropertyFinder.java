package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.util.Consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class PropertyFinder<T> {
	@SuppressWarnings("unchecked")
	public final <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher) {
		MatchingProperties matchingProperties = new MatchingProperties();
		lookForProperties(propertyNameMatcher, matchingProperties, PropertyMatchingScore.INITIAL);
		return (PropertyMeta<T, E>)matchingProperties.selectBestMatch();
	}

	protected abstract void lookForProperties(
			PropertyNameMatcher propertyNameMatcher,
			MatchingProperties matchingProperties,
			PropertyMatchingScore score);


	public abstract List<InstantiatorDefinition> getEligibleInstantiatorDefinitions();
    public abstract PropertyFinder<?> getSubPropertyFinder(String name);



	protected static class MatchingProperties<T> {
		private final List<MatchedProperty<T, ?>> matchedProperties = new ArrayList<MatchedProperty<T, ?>>();
		<P extends  PropertyMeta<T, ?>> void found(P propertyMeta,
				   Consumer<? super P> selectionCallback,
				   PropertyMatchingScore score) {
			matchedProperties.add(new MatchedProperty<T, P>(propertyMeta, selectionCallback, score));
		}

		public PropertyMeta<T, ?> selectBestMatch() {
			if (matchedProperties.isEmpty()) return null;
			Collections.sort(matchedProperties);
			MatchedProperty<T, ?> selectedMatchedProperty = matchedProperties.get(0);
			selectedMatchedProperty.select();
			return selectedMatchedProperty.propertyMeta;
		}
	}

	private static class MatchedProperty<T, P extends PropertyMeta<T, ?>> implements Comparable<MatchedProperty<T, ?>>{
		private final P propertyMeta;
		private final Consumer<? super P> selectionCallback;
		private final PropertyMatchingScore score;

		private MatchedProperty(P propertyMeta, Consumer<? super P> selectionCallback, PropertyMatchingScore score) {
			this.propertyMeta = propertyMeta;
			this.selectionCallback = selectionCallback;
			this.score = score;
		}


		@Override
		public int compareTo(MatchedProperty<T, ?> o) {
			return this.score.compareTo(o.score);
		}

		public void select() {
			if (selectionCallback != null) selectionCallback.accept(propertyMeta);
		}
	}

}