package org.sfm.csv;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sfm.csv.primitive.BooleanDelayedGetter;
import org.sfm.csv.primitive.ByteDelayedGetter;
import org.sfm.csv.primitive.CharDelayedGetter;
import org.sfm.csv.primitive.DoubleDelayedGetter;
import org.sfm.csv.primitive.FloatDelayedGetter;
import org.sfm.csv.primitive.IntDelayedGetter;
import org.sfm.csv.primitive.LongDelayedGetter;
import org.sfm.csv.primitive.ShortDelayedGetter;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.PropertyMapping;
import org.sfm.map.PropertyMappingsBuilder;
import org.sfm.map.RethrowFieldMapperErrorHandler;
import org.sfm.map.RethrowMapperBuilderErrorHandler;
import org.sfm.map.RethrowRowHandlerErrorHandler;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.asm.ConstructorParameter;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.ConstructorPropertyMeta;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.meta.SubPropertyMeta;
import org.sfm.utils.ForEachCallBack;

public class CsvMapperBuilder<T> {

	private final Class<?> SOURCE_UNTYPE = DelayedCellSetter[].class;
	@SuppressWarnings("unchecked")
	private final Class<DelayedCellSetter<T, ?>[]> SOURCE = (Class<DelayedCellSetter<T, ?>[]>) SOURCE_UNTYPE;
	
	private FieldMapperErrorHandler<CsvColumnKey> fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler<CsvColumnKey>();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	private RowHandlerErrorHandler rowHandlerErrorHandler = new RethrowRowHandlerErrorHandler();
	private final Type target;
	private final ReflectionService reflectionService;
	private final Map<String, String> aliases;
	private final Map<String, CellValueReader<?>> customReaders;
	
	private final PropertyMappingsBuilder<T, CsvColumnKey> propertyMappingsBuilder;
	
	private int syncSetterStart;

	private String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";

	public CsvMapperBuilder(final Type target) {
		this(target, new ReflectionService());
	}
	
	public CsvMapperBuilder(final Type target, ReflectionService reflectionService) {
		this(target, reflectionService,
				new HashMap<String, String>(), 
				new HashMap<String, CellValueReader<?>>());
	}	
	
	@SuppressWarnings("unchecked")
	public CsvMapperBuilder(final Type target, ReflectionService reflectionService,
			Map<String, String> aliases, Map<String, CellValueReader<?>> customReaders) throws MapperBuildingException {
		this(target, (ClassMeta<T>) reflectionService.getClassMeta(target), aliases, customReaders);
	}
	
	public CsvMapperBuilder(final Type target, final ClassMeta<T> classMeta,
			Map<String, String> aliases, Map<String, CellValueReader<?>> customReaders) throws MapperBuildingException {
		this.target = target;
		this.reflectionService = classMeta.getReflectionService();
		this.propertyMappingsBuilder = new PropertyMappingsBuilder<T, CsvColumnKey>(classMeta);
		this.aliases = aliases;
		this.customReaders = customReaders;
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey) {
		return addMapping(columnKey, propertyMappingsBuilder.size());
	}
	
	public final CsvMapperBuilder<T> addMapping(final String columnKey, int columnIndex) {
		return addMapping(new CsvColumnKey(columnKey, columnIndex));
	}
	
	public final CsvMapperBuilder<T> addMapping(final CsvColumnKey key) {
		final CsvColumnKey mappedColumnKey = alias(key);
		
		if (!propertyMappingsBuilder.addProperty(mappedColumnKey)) {
			mapperBuilderErrorHandler.propertyNotFound(target, key.getName());
		}
		
		return this;
	}
	
	private <P> void addMapping(PropertyMeta<T, P> propertyMeta, final CsvColumnKey key) {
		propertyMappingsBuilder.addProperty(key, propertyMeta);
	}
	
	private CsvColumnKey alias(CsvColumnKey key) {
		if (aliases == null || aliases.isEmpty()) {
			return key;
		} 
		String alias = aliases.get(key.getName().toUpperCase());
		if (alias == null) {
			return key;
		}
		return key.alias(alias);
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
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey>>() {

			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey> propMapping, int i) {
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
						pcf.setDateFormat(i, getDateFormat(i));
					}
				}
			}
		});
		
		return pcf;
	}

	private String getDateFormat(int i) {
		return defaultDateFormat;
	}
	
	private  Instantiator<DelayedCellSetter<T, ?>[], T>  getInstantiator() throws MapperBuildingException {
		InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();
		try {
			Instantiator<DelayedCellSetter<T, ?>[], T> instatiator;
			if (!reflectionService.isAsmPresent()) {
				instatiator = instantiatorFactory.getInstantiator(SOURCE, propertyMappingsBuilder.getPropertyFinder().getClassToInstantiate());
			} else {
				Map<ConstructorParameter, Getter<DelayedCellSetter<T, ?>[], ?>> constructorInjections = buildConstructorParametersDelayedCellSetter();
				instatiator = instantiatorFactory.getInstantiator(SOURCE, propertyMappingsBuilder.getPropertyFinder().getEligibleConstructorDefinitions(), constructorInjections, customReaders.isEmpty());
			}
			return instatiator;
		} catch(Exception e) {
			throw new MapperBuildingException(e.getMessage(), e);
		}
	}

	private Map<ConstructorParameter, Getter<DelayedCellSetter<T, ?>[], ?>> buildConstructorParametersDelayedCellSetter() {
		final Map<ConstructorParameter, Getter<DelayedCellSetter<T, ?>[], ?>> constructorInjections = new HashMap<ConstructorParameter, Getter<DelayedCellSetter<T, ?>[], ?>>();
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey>>() {

			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey> propMapping, int index) {
				if(propMapping == null) return;
				
				PropertyMeta<T, ?> meta = propMapping.getPropertyMeta();
				
				if (meta == null) return;
				
				CsvColumnKey key = propMapping.getColumnKey();
				if (meta.isConstructorProperty()) {
					syncSetterStart = index + 1;
					constructorInjections.put(((ConstructorPropertyMeta<T, ?>) meta).getConstructorParameter(), newDelayedGetter(key, meta.getType()));
				} else if (meta.isSubProperty()) {
					SubPropertyMeta<T, ?> subMeta = (SubPropertyMeta<T, ?>) meta;
					if  (subMeta.getOwnerProperty().isConstructorProperty()) {
						ConstructorPropertyMeta<?, ?> constPropMeta = (ConstructorPropertyMeta<?, ?>) subMeta.getOwnerProperty();
						if (!constructorInjections.containsKey(constPropMeta.getConstructorParameter())) {
							constructorInjections.put(constPropMeta.getConstructorParameter(), newDelayedGetter(key, constPropMeta.getType()));
						}
						syncSetterStart = index + 1;
					}
				}				
			}
		});
		
		return constructorInjections;
	}
	
	private Getter<DelayedCellSetter<T, ?>[], ?> newDelayedGetter(CsvColumnKey key, Type type) {
		Class<?> clazz = TypeHelper.toClass(type);
		Getter<DelayedCellSetter<T, ?>[], ?> getter;
		int columnIndex = key.getIndex();
		if (clazz.isPrimitive() && ! customReaders.containsKey(key.getName())) {
			if (boolean.class.equals(clazz)) {
				getter = new BooleanDelayedGetter<T>(columnIndex);
			} else if (byte.class.equals(clazz)) {
				getter = new ByteDelayedGetter<T>(columnIndex);
			} else if (char.class.equals(clazz)) {
				getter = new CharDelayedGetter<T>(columnIndex);
			} else if (short.class.equals(clazz)) {
				getter = new ShortDelayedGetter<T>(columnIndex);
			} else if (int.class.equals(clazz)) {
				getter = new IntDelayedGetter<T>(columnIndex);
			} else if (long.class.equals(clazz)) {
				getter = new LongDelayedGetter<T>(columnIndex);
			} else if (float.class.equals(clazz)) {
				getter = new FloatDelayedGetter<T>(columnIndex);
			} else if (double.class.equals(clazz)) {
				getter = new DoubleDelayedGetter<T>(columnIndex);
			} else {
				throw new IllegalArgumentException("Unexpected primitive " + clazz);
			}
		} else {
			getter = new DelayedGetter<T>(columnIndex);
		}
		return getter;
	}
	@SuppressWarnings({ "unchecked" })
	private DelayedCellSetterFactory<T, ?>[] buildDelayedSetters() {
		
		final Map<String, CsvMapperBuilder<?>> delegateMapperBuilders = new HashMap<String, CsvMapperBuilder<?>>();

		final List<DelayedCellSetterFactory<T, ?>> delayedSetters = new ArrayList<DelayedCellSetterFactory<T, ?>>(syncSetterStart);
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey>>() {
			CellSetterFactory cellSetterFactory = new CellSetterFactory(customReaders);

			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey> propMapping, int index) {
				if (propMapping != null) {
					PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();
					CsvColumnKey key = propMapping.getColumnKey();
					if (prop != null) {
						if (prop.isConstructorProperty()) {
							delayedSetters.add((DelayedCellSetterFactory<T, ?>)cellSetterFactory.getDelayedCellSetter(prop.getType(), index, key.getName()));
						}  else if (prop.isSubProperty()) {
							addSubProperty(delegateMapperBuilders, delayedSetters, prop, key);
						}else {
							delayedSetters.add(cellSetterFactory.getDelayedCellSetter(prop.getSetter(), index, key.getName()));
						}
					} else {
						delayedSetters.add(null);
					}
				} else {
					delayedSetters.add(null);
				}
				
			}
			
			private <P> void addSubProperty(
					Map<String, CsvMapperBuilder<?>> delegateMapperBuilders,
					List<DelayedCellSetterFactory<T, ?>> delayedSetters,
					PropertyMeta<T, P> prop, CsvColumnKey key) {
				SubPropertyMeta<T, P> subPropertyMeta = (SubPropertyMeta<T, P>)prop;
				
				final PropertyMeta<T, P> propOwner = subPropertyMeta.getOwnerProperty();
				CsvMapperBuilder<P> delegateMapperBuilder = (CsvMapperBuilder<P>) delegateMapperBuilders .get(propOwner.getName());
				
				if (delegateMapperBuilder == null) {
					delegateMapperBuilder = new CsvMapperBuilder<P>(propOwner.getType(), propOwner.getClassMeta(), aliases, customReaders);
					delegateMapperBuilders.put(propOwner.getName(), delegateMapperBuilder);
				}
				
				delegateMapperBuilder.addMapping(subPropertyMeta.getSubProperty(), key);
				
				delayedSetters.add(null);
			}
		}, 0, syncSetterStart);
		
		
		final Map<String, CsvMapper<?>> mappers = new HashMap<String, CsvMapper<?>>();
		
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey>>() {
			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey> propMapping, int index) {
				if (propMapping == null) return;
				
				PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();
				
				if (prop.isSubProperty()) {
					addSubPropertyDelayedSetter(delegateMapperBuilders,	delayedSetters, mappers, index, prop);
				}
			}
			
			private <P> void addSubPropertyDelayedSetter(
					Map<String, CsvMapperBuilder<?>> delegateMapperBuilders,
					List<DelayedCellSetterFactory<T, ?>> delayedSetters,
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
					delayedSetters.set(setterIndex , new DelegateMarkerDelayedCellSetter<T, P>(mapper));
				} else {
					delayedSetters.set(setterIndex , new DelegateMarkerDelayedCellSetter<T, P>(mapper, subProp.getSetter()));
				}
			}
		}, 0, syncSetterStart);
		
		
		return delayedSetters.toArray(new DelayedCellSetterFactory[syncSetterStart]);
	}


	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private CellSetter<T>[] getSetters() {
		final Map<String, CsvMapperBuilder<?>> delegateMapperBuilders = new HashMap<String, CsvMapperBuilder<?>>();
		
		
		final List<CellSetter<T>> setters = new ArrayList<CellSetter<T>>();

		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey>>() {
			CellSetterFactory cellSetterFactory = new CellSetterFactory(customReaders);
			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey> propMapping, int index) {
				if (propMapping != null) {
					PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();
					if (prop != null) {
						CsvColumnKey key = propMapping.getColumnKey();
						if (prop.isConstructorProperty()) {
							throw new IllegalStateException("Unexpected ConstructorPropertyMeta at " + index);
						} else if (prop.isSubProperty()) {
							final PropertyMeta<?, ?> powner = ((SubPropertyMeta)prop).getOwnerProperty();
							CsvMapperBuilder<?> delegateMapperBuilder = delegateMapperBuilders .get(powner.getName());
							
							if (delegateMapperBuilder == null) {
								delegateMapperBuilder = new CsvMapperBuilder(powner.getType(), powner.getClassMeta(), aliases, customReaders);
								delegateMapperBuilders.put(powner.getName(), delegateMapperBuilder);
							}
							
							delegateMapperBuilder.addMapping(((SubPropertyMeta) prop).getSubProperty(), key);
							
							setters.add(null);
							
						} else {
							setters.add(cellSetterFactory.getCellSetter(prop.getSetter(), index, key.getName()));
						}
					} else {
						setters.add(null);
					}
				} else {
					setters.add(null);
				}
			}
		}, syncSetterStart);
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey>>() {
			final Map<String, CsvMapper<?>> mappers = new HashMap<String, CsvMapper<?>>();
			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey> propMapping, int index) {
				if (propMapping == null) return;
				
				PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();
				if (prop instanceof SubPropertyMeta) {
					final String propName = ((SubPropertyMeta)prop).getOwnerProperty().getName();
					
					CsvMapper<?> mapper = mappers.get(propName);
					if (mapper == null) {
						CsvMapperBuilder<?> delegateMapperBuilder = delegateMapperBuilders .get(propName);
						mapper = delegateMapperBuilder.mapper();
						mappers.put(propName, mapper);
					}
					setters.set(index , new DelegateMarkerSetter(mapper, ((SubPropertyMeta) prop).getOwnerProperty().getSetter()));
				} 
			}
		}, syncSetterStart);
		
		
		return setters.toArray(new CellSetter[setters.size()]);
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