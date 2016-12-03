package org.simpleflatmapper.map.impl;


import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMatchingScore;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyNameMatcher;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.util.List;

public class ExtendClassMeta<T> implements ClassMeta<T> {
	private final ClassMeta<T> delegate;
	private final List<CustomProperty<?, ?>> customProperties;

	public ExtendClassMeta(ClassMeta<T> delegate, List<CustomProperty<?, ?>> customProperties) {
		this.delegate = delegate;
		this.customProperties = customProperties;
	}

	@Override
	public ReflectionService getReflectionService() {
		return delegate.getReflectionService();
	}

	@Override
	public PropertyFinder<T> newPropertyFinder(Predicate<PropertyMeta<?, ?>> propertyFilter) {
		return new ExtendPropertyFinder<T>(delegate.newPropertyFinder(propertyFilter), delegate.getType(), customProperties);
	}

	@Override
	public Type getType() {
		return delegate.getType();
	}

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		return delegate.getInstantiatorDefinitions();
	}

	@Override
	public void forEachProperties(Consumer<? super PropertyMeta<T, ?>> consumer) {
		delegate.forEachProperties(consumer);
	}

	private static class WrappedPropertyMeta<T, P> extends PropertyMeta<T, P> {
		private final PropertyMeta<T, P> delegate;
		private final List<CustomProperty<?, ?>> customProperties;

		public WrappedPropertyMeta(PropertyMeta<T, P> delegate, List<CustomProperty<?, ?>> customProperties) {
			super(delegate.getName(), delegate.getOwnerType(), delegate.getReflectService());
			this.delegate = delegate;
			this.customProperties = customProperties;
		}

		@Override
		public Setter<? super T, ? super P> getSetter() {
			return delegate.getSetter();
		}

		@Override
		public Getter<? super T, ? extends P> getGetter() {
			return delegate.getGetter();
		}

		@Override
		public Type getPropertyType() {
			return delegate.getPropertyType();
		}

		@Override
		public Type getOwnerType() {
			return delegate.getOwnerType();
		}

		@Override
		public ClassMeta<P> newPropertyClassMeta() {
			return new ExtendClassMeta<P>(delegate.getPropertyClassMeta(), customProperties);
		}

		@Override
		public boolean isConstructorProperty() {
			return delegate.isConstructorProperty();
		}

		@Override
		public String getPath() {
			return delegate.getPath();
		}

		@Override
		public boolean isSubProperty() {
			return delegate.isSubProperty();
		}

		@Override
		public boolean isSelf() {
			return delegate.isSelf();
		}

		@Override
		public boolean isValid() {
			return delegate.isValid();
		}

	}

	public static class ExtendPropertyFinder<T> extends PropertyFinder<T> {
		private final PropertyFinder<T> delegate;
		private final Type ownerType;
		private final List<CustomProperty<?, ?>> customProperties;

		public ExtendPropertyFinder(PropertyFinder<T> delegate,
									   Type ownerType,
									   List<CustomProperty<?, ?>> customProperties) {
			super(delegate.getPropertyFilter());
			this.delegate = delegate;
			this.ownerType = ownerType;
			this.customProperties = customProperties;
		}

		@Override
		public void lookForProperties(final PropertyNameMatcher propertyNameMatcher, final FoundProperty<T> matchingProperties, final PropertyMatchingScore score, final boolean allowSelfReference) {
			for(CustomProperty<?, ?> property : customProperties) {
				if (property.isApplicable(ownerType) &&  propertyNameMatcher.matches(property.getName())) {
					matchingProperties.found((CustomProperty<T, ?>)property, null, score);
				}
			}
			delegate.lookForProperties(propertyNameMatcher, new FoundProperty<T>() {
				@Override
				public <P extends PropertyMeta<T, ?>> void found(P propertyMeta, Runnable selectionCallback, PropertyMatchingScore score) {
					matchingProperties.found(new WrappedPropertyMeta(propertyMeta, customProperties), selectionCallback, score);
				}
			}, score, allowSelfReference);
		}

		@Override
		public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions() {
			return delegate.getEligibleInstantiatorDefinitions();
		}

		@SuppressWarnings("unchecked")
		@Override
		public PropertyFinder<?> getSubPropertyFinder(String name) {
			return new ExtendPropertyFinder(delegate.getSubPropertyFinder(name), ownerType, this.customProperties);
		}
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
	}
}
