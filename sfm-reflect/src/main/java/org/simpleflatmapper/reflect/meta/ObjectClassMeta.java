package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.impl.ParamNameDeductor;
import org.simpleflatmapper.reflect.*;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.*;
import java.util.*;

public final class ObjectClassMeta<T> implements ClassMeta<T> {

	public static final String[] EMPTY_STRING_ARRAY = new String[0];
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
		this.target = target;
		this.reflectService = reflectService;
		try {
			this.instantiatorDefinitions = reflectService.extractInstantiator(target, builderInstantiator);
			this.constructorProperties = listConstructorProperties(instantiatorDefinitions);
		} catch(Exception e) {
			ErrorHelper.rethrow(e);
			throw new IllegalStateException();
		}
		this.fieldAliases = Collections.unmodifiableMap(aliases(reflectService, TypeHelper.<T>toClass(target)));
		this.properties = Collections.unmodifiableList(listProperties(reflectService, target));
	}

    public ObjectClassMeta(Type target,
                           List<InstantiatorDefinition> instantiatorDefinitions,
                           List<ConstructorPropertyMeta<T, ?>> constructorProperties,
                           List<PropertyMeta<T, ?>> properties,
                           ReflectionService reflectService) {
        this.target = target;
        this.properties = properties;
        this.constructorProperties = constructorProperties;
        this.instantiatorDefinitions = instantiatorDefinitions;
        this.fieldAliases = Collections.unmodifiableMap(aliases(reflectService, TypeHelper.<T>toClass(target)));
        this.reflectService = reflectService;
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
        Class<T> tClass = TypeHelper.toClass(this.target);
        return new ConstructorPropertyMeta<T, P>(paramName, reflectService, param, tClass, instantiatorDefinition);
    }

    private List<PropertyMeta<T, ?>> listProperties(final ReflectionService reflectService, Type targetType) {
		final Class<T> target = TypeHelper.<T>toClass(targetType);
		final List<PropertyMeta<T, ?>> properties = new ArrayList<PropertyMeta<T, ?>>();
		final Map<TypeVariable<?>, Type> typeVariableTypeMap = TypeHelper.getTypesMap(targetType, target);

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
							method.getReturnType(),
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
						constructorProperties.set(indexOfProperty, constructorPropertyMeta.getter(getter));
					}
				} else {
					indexOfProperty = findProperty(properties, propertyName);
					if (indexOfProperty == -1) {
						properties.add(new ObjectPropertyMeta<T, P>(propertyName, reflectService, type, getter, setter));
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
	public PropertyFinder<T> newPropertyFinder() {
		return new ObjectPropertyFinder<T>(this);
	}

	@Override
	public Type getType() {
		return target;
	}

	@Override
	public String[] generateHeaders() {
		List<String> strings = new ArrayList<String>();

		for(PropertyMeta<T, ?> cpm : constructorProperties) {
			// ignore constructor properties of same type with 1 arg, copy constructor
			if (((ConstructorPropertyMeta)cpm ).getConstructorParameterSize() > 1
					|| cpm.getPropertyClassMeta() != this) {
				extractProperties(strings, cpm);
			}
		}

        for(PropertyMeta<T, ?> cpm : properties) {
            extractProperties(strings, cpm);
        }

        return strings.toArray(EMPTY_STRING_ARRAY);
	}

    private void extractProperties(List<String> properties, PropertyMeta<T, ?> cpm) {
        String prefix = cpm.getName();
        ClassMeta<?> classMeta = cpm.getPropertyClassMeta();

        if (classMeta != null) {
            for(String prop : classMeta.generateHeaders()) {
                String name = prop.length() == 0 ? prefix : prefix + "_" + prop;
                if (!properties.contains(name)) {
                    properties.add(formatName(name));
                }
            }
        } else {
            if (!properties.contains(prefix)) {
                properties.add(formatName(prefix));
            }
        }
    }

	private String formatName(String name) {
		StringBuilder sb = new StringBuilder(name.length());
		boolean lastWasUpperCase = false;
		for(int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);

			if (Character.isUpperCase(c)) {
				if (!lastWasUpperCase) {
					sb.append('_');
				}
				sb.append(Character.toLowerCase(c));
			} else {
				lastWasUpperCase = false;
				sb.append(c);
			}
		}
		return sb.toString();
	}

	@Override
	public boolean isLeaf() {
		return false;
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
