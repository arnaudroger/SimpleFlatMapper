package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.util.BooleanProvider;

import java.util.*;

final class ObjectPropertyFinder<T> extends PropertyFinder<T> {


	enum State {
		NONE, SELF, PROPERTIES
	}
	private final List<InstantiatorDefinition> eligibleInstantiatorDefinitions;
	private final ObjectClassMeta<T> classMeta;
	private final Map<String, PropertyFinder<?>> subPropertyFinders = new HashMap<String, PropertyFinder<?>>();
	private State state = State.NONE;
	private String selfName;



    ObjectPropertyFinder(ObjectClassMeta<T> classMeta) {
		this.classMeta = classMeta;
		this.eligibleInstantiatorDefinitions = classMeta.getInstantiatorDefinitions() != null ? new ArrayList<InstantiatorDefinition>(classMeta.getInstantiatorDefinitions()) : null;
	}

	@Override
	protected void lookForProperties(final PropertyNameMatcher propertyNameMatcher, FoundProperty<T> matchingProperties, PropertyMatchingScore score) {
		lookForConstructor(propertyNameMatcher, matchingProperties, score);
		lookForProperty(propertyNameMatcher, matchingProperties, score);
		if (state != State.PROPERTIES) {
			matchingProperties.found(new SelfPropertyMeta(classMeta.getReflectionService(), classMeta.getType(), new BooleanProvider() {
						@Override
						public boolean getBoolean() {
							return state != State.PROPERTIES;
						}
					}),
					new Runnable() {
						@Override
						public void run() {
							state = State.SELF;
							selfName = propertyNameMatcher.toString();
						}
					}, PropertyMatchingScore.MINIMUM);
		}
	}

	private void lookForConstructor(final PropertyNameMatcher propertyNameMatcher, final FoundProperty<T> matchingProperties, final PropertyMatchingScore score) {
		if (classMeta.getConstructorProperties() != null) {
			for (final ConstructorPropertyMeta<T, ?> prop : classMeta.getConstructorProperties()) {
				final String columnName = getColumnName(prop);
				if (propertyNameMatcher.matches(columnName)
						&& hasConstructorMatching(prop.getParameter())) {
					matchingProperties.found(prop, new Runnable() {
						@Override
						public void run() {
							removeNonMatching(prop.getParameter());
							state = State.PROPERTIES;
						}
					}, score);
				}

				PropertyNameMatcher subPropMatcher = propertyNameMatcher.partialMatch(columnName);
				if (subPropMatcher != null && hasConstructorMatching(prop.getParameter())) {
					lookForSubProperty(subPropMatcher, prop, new FoundProperty() {
						@Override
						public void found(final PropertyMeta propertyMeta, final Runnable selectionCallback, final PropertyMatchingScore score) {
							matchingProperties.found(
									new SubPropertyMeta(classMeta.getReflectionService(), prop, propertyMeta),
									new Runnable() {
										@Override
										public void run() {
											selectionCallback.run();
											removeNonMatching(prop.getParameter());
											state = State.PROPERTIES;
										}
									}, score.shift());
						}
					}, score.shift());
				}
			}
		}
	}


	private void lookForProperty(final PropertyNameMatcher propertyNameMatcher, final FoundProperty<T> matchingProperties, final PropertyMatchingScore score) {
		for (final PropertyMeta<T, ?> prop : classMeta.getProperties()) {
			final String columnName = getColumnName(prop);
			if (propertyNameMatcher.matches(columnName)) {
				matchingProperties.found(prop, new Runnable() {
					@Override
					public void run() {
						state = State.PROPERTIES;
					}
				}, score.decrease(1));
			}
			final PropertyNameMatcher subPropMatcher = propertyNameMatcher.partialMatch(columnName);
			if (subPropMatcher != null) {
				lookForSubProperty(subPropMatcher, prop, new FoundProperty() {
					@Override
					public void found(final PropertyMeta propertyMeta, final Runnable selectionCallback, final PropertyMatchingScore score) {
						matchingProperties.found(new SubPropertyMeta(classMeta.getReflectionService(), prop, propertyMeta),
								new Runnable() {
									@Override
									public void run() {
										selectionCallback.run();
										state = State.PROPERTIES;

									}
								}, score);
					}
				}, score.shift().decrease( -1));
			}
		}
	}

	private void lookForSubProperty(
			final PropertyNameMatcher propertyNameMatcher,
			final PropertyMeta<T, ?> prop,
			final FoundProperty foundProperty,
			final PropertyMatchingScore score) {
		PropertyFinder<?> subPropertyFinder = subPropertyFinders.get(getColumnName(prop));
		if (subPropertyFinder == null) {
			subPropertyFinder = prop.getPropertyClassMeta().newPropertyFinder();
			subPropertyFinders.put(prop.getName(), subPropertyFinder);
		}

		subPropertyFinder.lookForProperties(propertyNameMatcher, foundProperty, score);
	}

    private String getColumnName(PropertyMeta<T, ?> prop) {
        return this.classMeta.getAlias(prop.getName());
    }


    private void removeNonMatching(Parameter param) {
		ListIterator<InstantiatorDefinition> li = eligibleInstantiatorDefinitions.listIterator();
		while(li.hasNext()){
			InstantiatorDefinition cd = li.next();
			if (!cd.hasParam(param)) {
				li.remove();
			}
		}
	}
	
	private boolean hasConstructorMatching(Parameter param) {
		for(InstantiatorDefinition cd : eligibleInstantiatorDefinitions) {
			if (cd.hasParam(param)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
		return eligibleInstantiatorDefinitions;
	}

	@Override
	public PropertyFinder<?> getSubPropertyFinder(String name) {
		return subPropertyFinders.get(name);
	}


}
