package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.util.BooleanProvider;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.util.*;

final class ObjectPropertyFinder<T> extends PropertyFinder<T> {


	enum State {
		NONE, SELF, PROPERTIES
	}
	private final List<InstantiatorDefinition> eligibleInstantiatorDefinitions;
	private final ObjectClassMeta<T> classMeta;
	private final Map<PropertyMeta<?, ?>, PropertyFinder<?>> subPropertyFinders = new HashMap<PropertyMeta<?, ?>, PropertyFinder<?>>();
	private State state = State.NONE;
	private String selfName;



    ObjectPropertyFinder(ObjectClassMeta<T> classMeta, Predicate<PropertyMeta<?, ?>> propertyFilter, boolean selfScoreFullName) {
        super(propertyFilter, selfScoreFullName);
        this.classMeta = classMeta;
		this.eligibleInstantiatorDefinitions = classMeta.getInstantiatorDefinitions() != null ? new ArrayList<InstantiatorDefinition>(classMeta.getInstantiatorDefinitions()) : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void lookForProperties(final PropertyNameMatcher propertyNameMatcher,
								  Object[] properties, FoundProperty<T> matchingProperties,
								  PropertyMatchingScore score,
								  boolean allowSelfReference,
								  PropertyFinderTransformer propertyFinderTransform, TypeAffinityScorer typeAffinityScorer) {
		lookForConstructor(propertyNameMatcher, properties, matchingProperties, score, propertyFinderTransform, typeAffinityScorer);
		lookForProperty(propertyNameMatcher, properties, matchingProperties, score, propertyFinderTransform, typeAffinityScorer);

		final String propName = propertyNameMatcher.toString();
		if (allowSelfReference 
				&& !disallowSelfReference(properties) 
				&& (state == State.NONE || (state == State.SELF && propName.equals(selfName)))) {
			matchingProperties.found(new SelfPropertyMeta(classMeta.getReflectionService(), classMeta.getType(), new BooleanProvider() {
						@Override
						public boolean getBoolean() {
							return state != State.PROPERTIES;
						}
					}),
					selfPropertySelectionCallback(propName),
					score.self(classMeta.getNumberOfProperties(), propName), typeAffinityScorer);
		}
	}

	private boolean disallowSelfReference(Object[] properties) {
    	if (classMeta.getNumberOfProperties() <= 1) return false;
    	for(Object p : properties) {
    		if (p instanceof DisallowSelfReference) {
    			return true;
			}
		}
		return false;
	}

	private void lookForConstructor(final PropertyNameMatcher propertyNameMatcher, Object[] properties, final FoundProperty<T> matchingProperties, final PropertyMatchingScore score, final PropertyFinderTransformer propertyFinderTransformer, TypeAffinityScorer typeAffinityScorer) {
		if (classMeta.getConstructorProperties() != null) {
			for (final ConstructorPropertyMeta<T, ?> prop : classMeta.getConstructorProperties()) {
				final String columnName = getColumnName(prop);
				if (propertyNameMatcher.matches(columnName)
						&& hasConstructorMatching(prop.getParameter())) {
					matchingProperties.found(prop, propertiesRemoveNonMatchingCallBack(prop), score.matches(propertyNameMatcher), typeAffinityScorer);
				}

				PropertyNameMatch partialMatch = propertyNameMatcher.partialMatch(columnName);
				if (partialMatch != null && hasConstructorMatching(prop.getParameter())) {
					PropertyNameMatcher subPropMatcher = partialMatch.getLeftOverMatcher();
					lookForSubProperty(subPropMatcher, properties, prop, new FoundProperty() {
						@Override
						public void found(final PropertyMeta propertyMeta, final Runnable selectionCallback, final PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {
							matchingProperties.found(
									new SubPropertyMeta(classMeta.getReflectionService(), prop, propertyMeta),
									propertiesDelegateAndRemoveNonMatchingCallBack(selectionCallback, prop), score, typeAffinityScorer);
						}
					}, score.matches(partialMatch.getProperty()), propertyFinderTransformer, typeAffinityScorer);
				}
			}
		}
	}


	private void lookForProperty(final PropertyNameMatcher propertyNameMatcher, Object[] properties, final FoundProperty<T> matchingProperties, final PropertyMatchingScore score, final PropertyFinderTransformer propertyFinderTransformer, TypeAffinityScorer typeAffinityScorer) {
		for (final PropertyMeta<T, ?> prop : classMeta.getProperties()) {
			final String columnName = getColumnName(prop);
			if (propertyNameMatcher.matches(columnName)) {
				matchingProperties.found(prop, propertiesCallBack(), score.matches(propertyNameMatcher.toString()), typeAffinityScorer);
			}
			final PropertyNameMatch subPropMatch = propertyNameMatcher.partialMatch(columnName);
			if (subPropMatch != null) {
				final PropertyNameMatcher subPropMatcher = subPropMatch.getLeftOverMatcher();
				lookForSubProperty(subPropMatcher, properties, prop, new FoundProperty() {
					@Override
					public void found(final PropertyMeta propertyMeta, final Runnable selectionCallback, final PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {
						matchingProperties.found(new SubPropertyMeta(classMeta.getReflectionService(), prop, propertyMeta),
								propertiesDelegateCallBack(selectionCallback), score, typeAffinityScorer);
					}
				}, score.matches(subPropMatch.getProperty()),
				propertyFinderTransformer, typeAffinityScorer);
			}
		}
	}

	private void lookForSubProperty(
			final PropertyNameMatcher propertyNameMatcher,
			Object[] properties, final PropertyMeta<T, ?> prop,
			final FoundProperty foundProperty,
			final PropertyMatchingScore score,
			final PropertyFinderTransformer propertyFinderTransformer, TypeAffinityScorer typeAffinityScorer) {

    	PropertyFinder<?> subPropertyFinder = subPropertyFinders.get(prop);

    	final PropertyFinder<?> newPropertyFinder;

		if (subPropertyFinder == null) {
			subPropertyFinder = prop.getPropertyClassMeta().newPropertyFinder(propertyFilter);
			newPropertyFinder = subPropertyFinder;
		} else {
			newPropertyFinder = null;
		}

		propertyFinderTransformer
				.apply(subPropertyFinder)
				.lookForProperties(propertyNameMatcher, properties, new FoundProperty() {
					@Override
					public void found(final PropertyMeta propertyMeta, final Runnable selectionCallback, final PropertyMatchingScore score, TypeAffinityScorer typeAffinityScorer) {
						if (newPropertyFinder != null) {
							subPropertyFinders.put(prop, newPropertyFinder);
						}
						foundProperty.found(propertyMeta, selectionCallback, score, typeAffinityScorer);
					}
				}, score, false, propertyFinderTransformer, typeAffinityScorer);
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

	private Runnable compose(final Runnable r1, final Runnable r2) {
		return new Runnable() {
			@Override
			public void run() {
				r1.run();
				r2.run();
			}
		};
	}

	private Runnable propertiesDelegateAndRemoveNonMatchingCallBack(final Runnable selectionCallback, final ConstructorPropertyMeta<T, ?> prop) {
		return compose(selectionCallback, propertiesRemoveNonMatchingCallBack(prop));
	}

	private Runnable propertiesRemoveNonMatchingCallBack(final ConstructorPropertyMeta<T, ?> prop) {
		return compose(removeNonMatchingCallBack(prop), propertiesCallBack());
	}

	private Runnable removeNonMatchingCallBack(final ConstructorPropertyMeta<T, ?> prop) {
		return new Runnable() {
			@Override
			public void run() {
				removeNonMatching(prop.getParameter());
			}
		};
	}

	private Runnable propertiesDelegateCallBack(final Runnable selectionCallback) {
		return compose(selectionCallback, propertiesCallBack());
	}


	private Runnable propertiesCallBack() {
		return new Runnable() {
			@Override
			public void run() {
				state = State.PROPERTIES;
			}
		};
	}

	private Runnable selfPropertySelectionCallback(final String propName) {
		return new Runnable() {
			@Override
			public void run() {
				state = State.SELF;
				selfName = propName;
			}
		};
	}

	@Override
	public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
		return eligibleInstantiatorDefinitions;
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

	@Override
	public Type getOwnerType() {
		return classMeta.getType();
	}

	@Override
	public void manualMatch(PropertyMeta<?, ?> prop) {
    	if (prop.isConstructorProperty()) {
			removeNonMatching(((ConstructorPropertyMeta) prop).getParameter());
		}
		super.manualMatch(prop);
	}


}
