package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.getter.GetterHelper;
import org.simpleflatmapper.reflect.getter.NullGetter;
import org.simpleflatmapper.reflect.impl.ParamNameDeductor;
import org.simpleflatmapper.reflect.*;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.reflect.setter.SetterHelper;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.*;
import java.util.*;

public final class ObjectClassMeta<T> implements ClassMeta<T> {

	private final List<PropertyMeta<T, ?>> properties;
	private final List<ConstructorPropertyMeta<T, ?>> constructorProperties;
	private final List<InstantiatorDefinition> instantiatorDefinitions;

	
	private final ReflectionService reflectService;
	private final Type target;

	private final Map<String, String> fieldAliases;
	public ObjectClassMeta(Type target, ReflectionService reflectService) {
		this(target, null, reflectService);
	}

	public ObjectClassMeta(Type target, Member builderInstantiator, ReflectionService reflectService) {
		try {
			this.target = target;
			this.reflectService = reflectService;
			this.instantiatorDefinitions = reflectService.extractInstantiator(target, builderInstantiator);
			this.constructorProperties = listConstructorProperties(instantiatorDefinitions);
			this.fieldAliases = Collections.unmodifiableMap(aliases(reflectService, TypeHelper.<T>toClass(target)));
			this.properties = Collections.unmodifiableList(listProperties(reflectService, target));
		} catch(Exception e) {
			ErrorHelper.rethrow(e);
			throw new IllegalStateException();
		}
	}

    public ObjectClassMeta(Type target,
                           List<InstantiatorDefinition> instantiatorDefinitions,
                           List<ConstructorPropertyMeta<T, ?>> constructorProperties,
						   Map<String, String> fieldAliases,
                           List<PropertyMeta<T, ?>> properties,
                           ReflectionService reflectService) {
        this.target = target;
		this.reflectService = reflectService;
		this.instantiatorDefinitions = instantiatorDefinitions;
		this.constructorProperties = constructorProperties;
		this.fieldAliases = fieldAliases;
		this.properties = properties;
	}

    private Map<String, String> aliases(final ReflectionService reflectService, Class<T> target) {
		final Map<String, String> map = new HashMap<String, String>();
		
		ClassVisitor.visit(target, new FieldAndMethodCallBack() {
			@Override
			public void method(Method method) {
				String alias = reflectService.getColumnName(method);
				if (alias != null) {
					final String name;
					if (SetterHelper.isSetter(method)) {
						name = SetterHelper.getPropertyNameFromMethodName(method.getName());
					} else if (GetterHelper.isGetter(method)) {
						name = GetterHelper.getPropertyNameFromMethodName(method.getName());
					} else {
						throw new IllegalArgumentException("Annotation on non accessor method " + method);
					}
					map.put(name, alias);
				}
			}
			
			@Override
			public void field(Field field) {
				String alias = reflectService.getColumnName(field);
				if (alias != null) {
					map.put(field.getName(), alias);
				}
			}
		});
		
		return map;
	}

	private List<ConstructorPropertyMeta<T, ?>> listConstructorProperties(List<InstantiatorDefinition> instantiatorDefinitions) {
		if (instantiatorDefinitions == null) return null;
		
		List<ConstructorPropertyMeta<T, ?>> constructorProperties = new ArrayList<ConstructorPropertyMeta<T, ?>>();

		ParamNameDeductor<T> paramNameDeductor = null;
		for(InstantiatorDefinition cd : instantiatorDefinitions) {
			for(org.simpleflatmapper.reflect.Parameter param : cd.getParameters()) {
				String paramName = param.getName();

				if (paramName == null) {
					if (paramNameDeductor == null) {
						paramNameDeductor = new ParamNameDeductor<T>(TypeHelper.<T>toClass(target));
					}
					paramName = paramNameDeductor.findParamName(cd, param);
				}
				constructorProperties.add(constructorMeta(param, paramName, cd));
			}
		}
		return constructorProperties;
	}

    private <P> ConstructorPropertyMeta<T, P> constructorMeta(org.simpleflatmapper.reflect.Parameter param, String paramName, InstantiatorDefinition instantiatorDefinition) {
        return new ConstructorPropertyMeta<T, P>(paramName, target, reflectService, param,  instantiatorDefinition);
    }

    private List<PropertyMeta<T, ?>> listProperties(final ReflectionService reflectService, final Type targetType) {
		final Class<T> target = TypeHelper.<T>toClass(targetType);
		final List<PropertyMeta<T, ?>> properties = new ArrayList<PropertyMeta<T, ?>>();
		final Map<TypeVariable<?>, Type> typeVariableTypeMap = TypeHelper.getTypesMap(targetType);

		ClassVisitor.visit(target, new FieldAndMethodCallBack() {
			@Override
			public void method(Method method) {
				final String name = method.getName();
				if (SetterHelper.isSetter(method)) {
					final String propertyName = SetterHelper.getPropertyNameFromMethodName(name);

					Setter<T, Object> methodSetter = reflectService.getObjectSetterFactory().getMethodSetter(method);
					register(propertyName,
							method.getGenericParameterTypes()[0],
							ScoredGetter.<T, Object>nullGetter(),
							ScoredSetter.ofMethod(method, methodSetter));
				} else if (GetterHelper.isGetter(method)) {
					final String propertyName = GetterHelper.getPropertyNameFromMethodName(name);

					Getter<T, Object> methodGetter = reflectService.getObjectGetterFactory().getMethodGetter(method);
					register(propertyName,
							method.getGenericReturnType(),
							ScoredGetter.ofMethod(method, methodGetter),
							ScoredSetter.<T, Object>nullSetter());
				}
			}

			@SuppressWarnings("unchecked")
			private <P> void register(String propertyName, Type type, ScoredGetter<T, P> getter, ScoredSetter<T, P> setter) {

				if (type instanceof TypeVariable) {
					Type mappedType = typeVariableTypeMap.get(type);
					if (mappedType != null) {
						type = mappedType;
					}
				}


				int indexOfProperty = findProperty(constructorProperties, propertyName);

				if (indexOfProperty != -1) {
					ConstructorPropertyMeta<T, P> constructorPropertyMeta = (ConstructorPropertyMeta<T, P>) constructorProperties.get(indexOfProperty);
					if (getter != null && GetterHelper.isCompatible(constructorPropertyMeta.getPropertyType(), type)) {
						constructorPropertyMeta = constructorPropertyMeta.getter(getter);
						constructorProperties.set(indexOfProperty, constructorPropertyMeta);
					}
					if (setter != null && SetterHelper.isCompatible(constructorPropertyMeta.getPropertyType(), type)) {
						constructorPropertyMeta = constructorPropertyMeta.setter(setter);
						constructorProperties.set(indexOfProperty, constructorPropertyMeta);
					}
				} else {
					indexOfProperty = findProperty(properties, propertyName);
					if (indexOfProperty == -1) {
						properties.add(new ObjectPropertyMeta<T, P>(propertyName, targetType, reflectService, type, getter, setter));
					} else {
						ObjectPropertyMeta<T, P> meta = (ObjectPropertyMeta<T, P>) properties.get(indexOfProperty);

						ScoredGetter<T, P> compatibleGetter = GetterHelper.isCompatible(meta.getPropertyType(), type) ? getter : ScoredGetter.<T, P>nullGetter();
						ScoredSetter<T, P> compatibleSetter = SetterHelper.isCompatible(meta.getPropertyType(), type) ? setter : ScoredSetter.<T, P>nullSetter();
						properties.set(indexOfProperty,
								meta.getterSetter(compatibleGetter, compatibleSetter));
					}
				}
			}

			@Override
			public void field(Field field) {
				final String name = field.getName();
				if (!Modifier.isStatic(field.getModifiers())) {

					if (Modifier.isPublic(field.getModifiers())) {
						ScoredGetter<T, Object> getter = ScoredGetter.<T, Object>ofField(field, reflectService.getObjectGetterFactory().<T, Object>getFieldGetter(field));
						ScoredSetter<T, Object> setter;

						if (!Modifier.isFinal(field.getModifiers())) {
							setter = ScoredSetter.<T, Object>ofField(field, reflectService.getObjectSetterFactory().<T, Object>getFieldSetter(field));
						} else {
							setter = ScoredSetter.<T, Object>nullSetter();
						}


						register(name,
								field.getGenericType(),
								getter,
								setter);
					} else {
						register(name, field.getGenericType(), ScoredGetter.<T, Object>nullGetter(), ScoredSetter.<T, Object>nullSetter());
					}
				}
			}

            private int findProperty(List<? extends PropertyMeta<T, ?>> properties, String name) {
                for(int i = 0; i < properties.size(); i++) {
					PropertyMeta<T, ?> propertyMeta = properties.get(i);
					String propertyMetaName = propertyMeta.getName();
					if (propertyMetaName != null && propertyMetaName.equals(name)) {
                        return i;
                    }
                }
                return -1;
            }
        });
		// filter out private field only;
		for(Iterator<PropertyMeta<T, ?>> it = properties.iterator(); it.hasNext();) {
			PropertyMeta<T, ?> propertyMeta = it.next();
			if (NullSetter.isNull(propertyMeta.getSetter()) && NullGetter.isNull(propertyMeta.getGetter())) {
				it.remove();
			}
		}
		return properties;
	}


	protected String getAlias(String propertyName) {
		String columnName = this.fieldAliases.get(propertyName);
		if (columnName == null) {
			columnName = propertyName;
		}
		return columnName;
	}

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		return instantiatorDefinitions;
	}

	@Override
	public void forEachProperties(Consumer<? super PropertyMeta<T, ?>> consumer) {
		for(ConstructorPropertyMeta<T, ?> prop : constructorProperties) {
			consumer.accept(prop);
		}

		for(PropertyMeta<T, ?> prop : properties) {
			consumer.accept(prop);
		}
	}

	List<PropertyMeta<T, ?>> getProperties() {
		return properties;
	}

	List<ConstructorPropertyMeta<T, ?>> getConstructorProperties() {
		return constructorProperties;
	}

	@Override
	public ReflectionService getReflectionService() {
		return reflectService;
	}

	@Override
	public PropertyFinder<T> newPropertyFinder(Predicate<PropertyMeta<?, ?>> propertyFilter) {
		return new ObjectPropertyFinder<T>(this, propertyFilter);
	}

	@Override
	public Type getType() {
		return target;
	}

	public int getNumberOfProperties() {
		return constructorProperties.size() + properties.size();
	}

	public PropertyMeta<T, ?> getFirstProperty() {
		if (!constructorProperties.isEmpty()) {
			return constructorProperties.get(0);
		}
		if (!properties.isEmpty()) {
			return properties.get(0);
		}
		return null;
	}
}
