package org.sfm.csv;

import org.sfm.csv.impl.*;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.*;
import org.sfm.reflect.*;
import org.sfm.reflect.meta.*;
import org.sfm.utils.ForEachCallBack;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CsvMapperBuilder<T> {

	private final Class<?> SOURCE_UNTYPE = DelayedCellSetter[].class;
	@SuppressWarnings("unchecked")
	private final Class<DelayedCellSetter<T, ?>[]> SOURCE = (Class<DelayedCellSetter<T, ?>[]>) SOURCE_UNTYPE;
	private FieldMapperErrorHandler<CsvColumnKey> fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler<CsvColumnKey>();

	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	private RowHandlerErrorHandler rowHandlerErrorHandler = new RethrowRowHandlerErrorHandler();
	private final PropertyNameMatcherFactory propertyNameMatcherFactory;
	private final Type target;
	private final ReflectionService reflectionService;
	private final Map<String, CsvColumnDefinition> columnDefinitions;

	private final PropertyMappingsBuilder<T, CsvColumnKey, CsvColumnDefinition> propertyMappingsBuilder;
	
	private int syncSetterStart;

	private String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";

	public CsvMapperBuilder(final Type target) {
		this(target, ReflectionService.newInstance());
	}
	
	public CsvMapperBuilder(final Type target, ReflectionService reflectionService) {
		this(target, (ClassMeta<T>)reflectionService.getClassMeta(target));
	}

	public CsvMapperBuilder(final Type target, final ClassMeta<T> classMeta) {
		this(target, classMeta, new HashMap<String, CsvColumnDefinition>(),new DefaultPropertyNameMatcherFactory());
	}

	public CsvMapperBuilder(final Type target, final ClassMeta<T> classMeta,
			Map<String, CsvColumnDefinition> columnDefinitions, PropertyNameMatcherFactory propertyNameMatcherFactory) throws MapperBuildingException {
		this.target = target;
		this.reflectionService = classMeta.getReflectionService();
		this.propertyMappingsBuilder = new PropertyMappingsBuilder<T, CsvColumnKey, CsvColumnDefinition>(classMeta, propertyNameMatcherFactory);
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		this.columnDefinitions = columnDefinitions;
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey) {
		return addMapping(columnKey, propertyMappingsBuilder.size());
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey, final CsvColumnDefinition columnDefinition) {
		return addMapping(columnKey, propertyMappingsBuilder.size(), columnDefinition);
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey, int columnIndex, CsvColumnDefinition columnDefinition) {
		return addMapping(new CsvColumnKey(columnKey, columnIndex), columnDefinition);
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey, int columnIndex) {
		return addMapping(new CsvColumnKey(columnKey, columnIndex), CsvColumnDefinition.IDENTITY);
	}
	
	public final CsvMapperBuilder<T> addMapping(final CsvColumnKey key, final CsvColumnDefinition columnDefinition) {
		final CsvColumnDefinition composedDefinition = CsvColumnDefinition.compose(columnDefinition, getColumnDefintion(key));
		final CsvColumnKey mappedColumnKey = composedDefinition.rename(key);

		PropertyMeta<T, ?> propertyMeta = propertyMappingsBuilder.addProperty(mappedColumnKey, composedDefinition);
		if (propertyMeta == null) {
			mapperBuilderErrorHandler.propertyNotFound(target, key.getName());
		}
		if (composedDefinition.hasCustomReader()) {
			checkCompatibility(key, propertyMeta, composedDefinition);
		}

		return this;
	}

	private void checkCompatibility(CsvColumnKey key, PropertyMeta<T, ?> propertyMeta, CsvColumnDefinition composedDefinition) {
		CellValueReader<?> customReader = composedDefinition.getCustomReader();

		Class<?> readerClass = getCellReaderType(customReader.getClass());

		if (readerClass == null) {
			mapperBuilderErrorHandler.customFieldError(key, "Could not find reader return type " + customReader);
		} else if(!areCompatible(propertyMeta.getType(), readerClass)) {
			mapperBuilderErrorHandler.customFieldError(key, "Incompatible customreader type " + customReader + " returns" + readerClass + " for " + propertyMeta.getType());
		}
	}

	private Class<?> getCellReaderType(Class<?> currentclass) {
		if (currentclass == null) {
			return null;
		}
		Type[] genericInterfaces = currentclass.getGenericInterfaces();
		for(Type t : genericInterfaces) {
			if (t instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) t;
				if (pt.getRawType().equals(CellValueReader.class)) {
					return TypeHelper.toClass(pt.getActualTypeArguments()[0]);
				}
			} else if (t instanceof Class) {
				Class<?> readerClass = getCellReaderType((Class)t);
				if (readerClass != null) {
					return readerClass;
				}
			}
		}
		return getCellReaderType(currentclass.getSuperclass());
	}

	private boolean areCompatible(Type type, Class<?> readerClass) {
		Class<?> aClass = TypeHelper.toBoxClass(TypeHelper.toClass(type));
		return aClass.isAssignableFrom(TypeHelper.toBoxClass(readerClass));
	}

	private <P> void addMapping(PropertyMeta<T, P> propertyMeta, final CsvColumnKey key, final CsvColumnDefinition columnDefinition) {
		propertyMappingsBuilder.addProperty(key, columnDefinition, propertyMeta);
	}
	
	private CsvColumnDefinition getColumnDefintion(CsvColumnKey key) {
		CsvColumnDefinition definition = columnDefinitions.get(key.getName().toLowerCase());

		if (definition == null) {
			return CsvColumnDefinition.IDENTITY;
		} else {
			return definition;
		}
	}


	public void setDefaultDateFormat(String defaultDateFormat) {
		this.defaultDateFormat = defaultDateFormat;
	}

	public final CsvMapper<T> mapper() {
		return new CsvMapperImpl<T>(getInstantiator(), buildDelayedSetters(), getSetters(), getKeys(), getParserContextFactory(), fieldMapperErrorHandler, rowHandlerErrorHandler);
	}


	private CsvColumnKey[] getKeys() {
		return propertyMappingsBuilder.getKeys().toArray(new CsvColumnKey[propertyMappingsBuilder.size()]);
	}


	
	private ParsingContextFactory getParserContextFactory() {
		final ParsingContextFactory pcf = new ParsingContextFactory(propertyMappingsBuilder.size());
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {

			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping) {
				if (propMapping == null) return;
				
				PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();
				if (prop != null) {
					Class<?> target;
					if (prop instanceof SubPropertyMeta) {
						target = TypeHelper.toClass(((SubPropertyMeta<?, ?>) prop)
								.getFinalType());
					} else {
						target = TypeHelper.toClass(prop.getType());
					}
					if (Date.class.equals(target)) {
						pcf.setDateFormat(propMapping.getColumnKey().getIndex(), propMapping.getColumnDefinition().dateFormat(defaultDateFormat));
					}
				}
			}
		});
		
		return pcf;
	}

	private  Instantiator<DelayedCellSetter<T, ?>[], T>  getInstantiator() throws MapperBuildingException {
		InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();

		try {
			return instantiatorFactory.getInstantiator(SOURCE, target, propertyMappingsBuilder, buildConstructorParametersDelayedCellSetter());
		} catch(Exception e) {
			throw new MapperBuildingException(e.getMessage(), e);
		}
	}

	private Map<ConstructorParameter, Getter<DelayedCellSetter<T, ?>[], ?>> buildConstructorParametersDelayedCellSetter() {
		final Map<ConstructorParameter, Getter<DelayedCellSetter<T, ?>[], ?>> constructorInjections = new HashMap<ConstructorParameter, Getter<DelayedCellSetter<T, ?>[], ?>>();

		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {
			final CellSetterFactory cellSetterFactory = new CellSetterFactory();

			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping) {
				if(propMapping == null) return;
				
				PropertyMeta<T, ?> meta = propMapping.getPropertyMeta();
				
				if (meta == null) return;
				
				final CsvColumnKey key = propMapping.getColumnKey();
				if (meta.isConstructorProperty()) {
					syncSetterStart = Math.max(syncSetterStart, key.getIndex() + 1);
					Getter<DelayedCellSetter<T, ?>[], ?> delayedGetter = cellSetterFactory.newDelayedGetter(key, meta.getType());
					constructorInjections.put(((ConstructorPropertyMeta<T, ?>) meta).getConstructorParameter(), delayedGetter);
				} else if (meta.isSubProperty()) {
					SubPropertyMeta<T, ?> subMeta = (SubPropertyMeta<T, ?>) meta;
					if  (subMeta.getOwnerProperty().isConstructorProperty()) {
						ConstructorPropertyMeta<?, ?> constPropMeta = (ConstructorPropertyMeta<?, ?>) subMeta.getOwnerProperty();
						if (!constructorInjections.containsKey(constPropMeta.getConstructorParameter())) {
							Getter<DelayedCellSetter<T, ?>[], ?> delayedGetter = cellSetterFactory.newDelayedGetter(key, constPropMeta.getType());
							constructorInjections.put(constPropMeta.getConstructorParameter(), delayedGetter);
						}
						syncSetterStart = Math.max(syncSetterStart, key.getIndex() + 1);
					}
				}				
			}
		});
		
		return constructorInjections;
	}
	
	@SuppressWarnings({ "unchecked" })
	private DelayedCellSetterFactory<T, ?>[] buildDelayedSetters() {
		
		final Map<String, CsvMapperBuilder<?>> delegateMapperBuilders = new HashMap<String, CsvMapperBuilder<?>>();

		final DelayedCellSetterFactory<T, ?>[] delayedSetters = new DelayedCellSetterFactory[syncSetterStart];
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {
			CellSetterFactory cellSetterFactory = new CellSetterFactory();

			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping) {
				if (propMapping != null) {
					PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();
					CsvColumnKey key = propMapping.getColumnKey();
					if (prop != null) {
						if (prop.isConstructorProperty()) {
							delayedSetters[propMapping.getColumnKey().getIndex()] = cellSetterFactory.getDelayedCellSetter(prop.getType(), key.getIndex(), propMapping.getColumnDefinition());
						}  else if (prop.isSubProperty()) {
							addSubProperty(delegateMapperBuilders, prop, key, propMapping.getColumnDefinition());
						}else {
							delayedSetters[propMapping.getColumnKey().getIndex()] =  cellSetterFactory.getDelayedCellSetter(prop.getType(), prop.getSetter(), key.getIndex(), propMapping.getColumnDefinition());
						}
					}
				}
			}
			
			private <P> void addSubProperty(
					Map<String, CsvMapperBuilder<?>> delegateMapperBuilders,
					PropertyMeta<T, P> prop, CsvColumnKey key, CsvColumnDefinition columnDefinition) {
				SubPropertyMeta<T, P> subPropertyMeta = (SubPropertyMeta<T, P>)prop;
				
				final PropertyMeta<T, P> propOwner = subPropertyMeta.getOwnerProperty();
				CsvMapperBuilder<P> delegateMapperBuilder = (CsvMapperBuilder<P>) delegateMapperBuilders .get(propOwner.getName());
				
				if (delegateMapperBuilder == null) {
					delegateMapperBuilder = new CsvMapperBuilder<P>(propOwner.getType(), propOwner.getClassMeta(), columnDefinitions, propertyNameMatcherFactory);
					delegateMapperBuilders.put(propOwner.getName(), delegateMapperBuilder);
				}
				
				delegateMapperBuilder.addMapping(subPropertyMeta.getSubProperty(), key, columnDefinition);
				
			}
		}, 0, syncSetterStart);
		
		
		final Map<String, CsvMapper<?>> mappers = new HashMap<String, CsvMapper<?>>();
		
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {
			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping) {
				if (propMapping == null) return;
				
				PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();
				
				if (prop.isSubProperty()) {
					addSubPropertyDelayedSetter(delegateMapperBuilders,	delayedSetters, mappers, propMapping.getColumnKey().getIndex(), prop);
				}
			}
			
			private <P> void addSubPropertyDelayedSetter(
					Map<String, CsvMapperBuilder<?>> delegateMapperBuilders,
					DelayedCellSetterFactory<T, ?>[] delayedSetters,
					Map<String, CsvMapper<?>> mappers, int setterIndex,
					PropertyMeta<T, P> prop) {
				PropertyMeta<T, P> subProp = ((SubPropertyMeta<T, P>) prop).getOwnerProperty();
				
				final String propName = subProp.getName();
				
				CsvMapper<P> mapper = (CsvMapper<P>) mappers.get(propName);
				if (mapper == null) {
					CsvMapperBuilder<P> delegateMapperBuilder = (CsvMapperBuilder<P>) delegateMapperBuilders.get(propName);
					mapper = delegateMapperBuilder.mapper();
					mappers.put(propName, mapper);
				}
				
				if (subProp instanceof ConstructorPropertyMeta) {
					delayedSetters[setterIndex] = new DelegateMarkerDelayedCellSetter<T, P>(mapper);
				} else {
					delayedSetters[setterIndex] = new DelegateMarkerDelayedCellSetter<T, P>(mapper, subProp.getSetter());
				}
			}
		}, 0, syncSetterStart);
		
		
		return delayedSetters;
	}


	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private CellSetter<T>[] getSetters() {
		final Map<String, CsvMapperBuilder<?>> delegateMapperBuilders = new HashMap<String, CsvMapperBuilder<?>>();
		
		

		int maxIndex = propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {

			int maxIndex = syncSetterStart;
			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping) {
				if (propMapping != null) {
					maxIndex = Math.max(propMapping.getColumnKey().getIndex(), maxIndex);
					PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();
					if (prop != null) {
						CsvColumnKey key = propMapping.getColumnKey();


						if (prop.isConstructorProperty()) {
							throw new IllegalStateException("Unexpected ConstructorPropertyMeta at " + key.getIndex());
						} else if (prop.isSubProperty()) {
							final PropertyMeta<?, ?> powner = ((SubPropertyMeta)prop).getOwnerProperty();
							CsvMapperBuilder<?> delegateMapperBuilder = delegateMapperBuilders .get(powner.getName());
							
							if (delegateMapperBuilder == null) {
								delegateMapperBuilder = new CsvMapperBuilder(powner.getType(), powner.getClassMeta(), columnDefinitions, propertyNameMatcherFactory);
								delegateMapperBuilders.put(powner.getName(), delegateMapperBuilder);
							}
							
							delegateMapperBuilder.addMapping(((SubPropertyMeta) prop).getSubProperty(), key, propMapping.getColumnDefinition());
							
						}
					}
				}
			}
		}, syncSetterStart).maxIndex;


		final CellSetter<T>[] setters = new CellSetter[maxIndex + 1 - syncSetterStart];


		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {
			final Map<String, CsvMapper<?>> mappers = new HashMap<String, CsvMapper<?>>();
			final CellSetterFactory cellSetterFactory = new CellSetterFactory();

			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping) {
				if (propMapping == null) {
					return;
				}
				
				PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();

				if (prop == null) {
					return;
				}

				if (prop instanceof SubPropertyMeta) {
					final String propName = ((SubPropertyMeta)prop).getOwnerProperty().getName();
					
					CsvMapper<?> mapper = mappers.get(propName);
					if (mapper == null) {
						CsvMapperBuilder<?> delegateMapperBuilder = delegateMapperBuilders .get(propName);
						mapper = delegateMapperBuilder.mapper();
						mappers.put(propName, mapper);
					}
					setters[propMapping.getColumnKey().getIndex()- syncSetterStart] = new DelegateMarkerSetter(mapper, ((SubPropertyMeta) prop).getOwnerProperty().getSetter());
				} else {
					setters[propMapping.getColumnKey().getIndex()- syncSetterStart] = cellSetterFactory.getCellSetter(prop.getType(), prop.getSetter(), propMapping.getColumnKey().getIndex(), propMapping.getColumnDefinition());
				}
			}
		}, syncSetterStart);
		
		
		return setters;
	}

	public final CsvMapperBuilder<T> fieldMapperErrorHandler(final FieldMapperErrorHandler<CsvColumnKey> errorHandler) {
		fieldMapperErrorHandler = errorHandler;
		return this;
	}

	public final CsvMapperBuilder<T> mapperBuilderErrorHandler(final MapperBuilderErrorHandler errorHandler) {
		mapperBuilderErrorHandler = errorHandler;
		return this;
	}
}