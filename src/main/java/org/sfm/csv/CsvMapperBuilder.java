package org.sfm.csv;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sfm.csv.impl.CellSetter;
import org.sfm.csv.impl.CellSetterFactory;
import org.sfm.csv.impl.CsvMapperImpl;
import org.sfm.csv.impl.DelayedCellSetter;
import org.sfm.csv.impl.DelayedCellSetterFactory;
import org.sfm.csv.impl.DelayedGetter;
import org.sfm.csv.impl.DelegateMarkerDelayedCellSetter;
import org.sfm.csv.impl.DelegateMarkerSetter;
import org.sfm.csv.impl.ParsingContextFactory;
import org.sfm.csv.impl.primitive.BooleanDelayedGetter;
import org.sfm.csv.impl.primitive.ByteDelayedGetter;
import org.sfm.csv.impl.primitive.CharDelayedGetter;
import org.sfm.csv.impl.primitive.DoubleDelayedGetter;
import org.sfm.csv.impl.primitive.FloatDelayedGetter;
import org.sfm.csv.impl.primitive.IntDelayedGetter;
import org.sfm.csv.impl.primitive.LongDelayedGetter;
import org.sfm.csv.impl.primitive.ShortDelayedGetter;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.*;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.ConstructorParameter;
import org.sfm.reflect.meta.*;
import org.sfm.utils.ForEachCallBack;

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
	
	public final CsvMapperBuilder<T> addMapping(final String columnKey, int columnIndex) {
		return addMapping(new CsvColumnKey(columnKey, columnIndex), CsvColumnDefinition.IDENTITY);
	}
	
	public final CsvMapperBuilder<T> addMapping(final CsvColumnKey key, final CsvColumnDefinition columnDefinition) {
		final CsvColumnDefinition composedDefinition = CsvColumnDefinition.compose(columnDefinition, getColumnDefintion(key));
		final CsvColumnKey mappedColumnKey = composedDefinition.rename(key);
		
		if (!propertyMappingsBuilder.addProperty(mappedColumnKey, composedDefinition)) {
			mapperBuilderErrorHandler.propertyNotFound(target, key.getName());
		}
		
		return this;
	}
	
	private <P> void addMapping(PropertyMeta<T, P> propertyMeta, final CsvColumnKey key, final CsvColumnDefinition columnDefinition) {
		propertyMappingsBuilder.addProperty(key, columnDefinition, propertyMeta);
	}
	
	private CsvColumnDefinition getColumnDefintion(CsvColumnKey key) {
		CsvColumnDefinition definition = columnDefinitions.get(key.getName());

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
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping, int i) {
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
						pcf.setDateFormat(i, propMapping.getColumnDefinition().dateFormat(defaultDateFormat));
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
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping, int index) {
				if(propMapping == null) return;
				
				PropertyMeta<T, ?> meta = propMapping.getPropertyMeta();
				
				if (meta == null) return;
				
				final CsvColumnKey key = propMapping.getColumnKey();
				if (meta.isConstructorProperty()) {
					syncSetterStart = index + 1;
					constructorInjections.put(((ConstructorPropertyMeta<T, ?>) meta).getConstructorParameter(), cellSetterFactory.newDelayedGetter(key, meta.getType()));
				} else if (meta.isSubProperty()) {
					SubPropertyMeta<T, ?> subMeta = (SubPropertyMeta<T, ?>) meta;
					if  (subMeta.getOwnerProperty().isConstructorProperty()) {
						ConstructorPropertyMeta<?, ?> constPropMeta = (ConstructorPropertyMeta<?, ?>) subMeta.getOwnerProperty();
						if (!constructorInjections.containsKey(constPropMeta.getConstructorParameter())) {
							constructorInjections.put(constPropMeta.getConstructorParameter(), cellSetterFactory.newDelayedGetter(key, constPropMeta.getType()));
						}
						syncSetterStart = index + 1;
					}
				}				
			}
		});
		
		return constructorInjections;
	}
	
	@SuppressWarnings({ "unchecked" })
	private DelayedCellSetterFactory<T, ?>[] buildDelayedSetters() {
		
		final Map<String, CsvMapperBuilder<?>> delegateMapperBuilders = new HashMap<String, CsvMapperBuilder<?>>();

		final List<DelayedCellSetterFactory<T, ?>> delayedSetters = new ArrayList<DelayedCellSetterFactory<T, ?>>(syncSetterStart);
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {
			CellSetterFactory cellSetterFactory = new CellSetterFactory();

			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping, int index) {
				if (propMapping != null) {
					PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();
					CsvColumnKey key = propMapping.getColumnKey();
					if (prop != null) {
						if (prop.isConstructorProperty()) {
							delayedSetters.add((DelayedCellSetterFactory<T, ?>)cellSetterFactory.getDelayedCellSetter(prop.getType(), index, propMapping.getColumnDefinition()));
						}  else if (prop.isSubProperty()) {
							addSubProperty(delegateMapperBuilders, delayedSetters, prop, key, propMapping.getColumnDefinition());
						}else {
							delayedSetters.add(cellSetterFactory.getDelayedCellSetter(prop.getType(), prop.getSetter(), index, propMapping.getColumnDefinition()));
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
					PropertyMeta<T, P> prop, CsvColumnKey key, CsvColumnDefinition columnDefinition) {
				SubPropertyMeta<T, P> subPropertyMeta = (SubPropertyMeta<T, P>)prop;
				
				final PropertyMeta<T, P> propOwner = subPropertyMeta.getOwnerProperty();
				CsvMapperBuilder<P> delegateMapperBuilder = (CsvMapperBuilder<P>) delegateMapperBuilders .get(propOwner.getName());
				
				if (delegateMapperBuilder == null) {
					delegateMapperBuilder = new CsvMapperBuilder<P>(propOwner.getType(), propOwner.getClassMeta(), columnDefinitions, propertyNameMatcherFactory);
					delegateMapperBuilders.put(propOwner.getName(), delegateMapperBuilder);
				}
				
				delegateMapperBuilder.addMapping(subPropertyMeta.getSubProperty(), key, columnDefinition);
				
				delayedSetters.add(null);
			}
		}, 0, syncSetterStart);
		
		
		final Map<String, CsvMapper<?>> mappers = new HashMap<String, CsvMapper<?>>();
		
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {
			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping, int index) {
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

		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {
			CellSetterFactory cellSetterFactory = new CellSetterFactory();
			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping, int index) {
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
								delegateMapperBuilder = new CsvMapperBuilder(powner.getType(), powner.getClassMeta(), columnDefinitions, propertyNameMatcherFactory);
								delegateMapperBuilders.put(powner.getName(), delegateMapperBuilder);
							}
							
							delegateMapperBuilder.addMapping(((SubPropertyMeta) prop).getSubProperty(), key, propMapping.getColumnDefinition());
							
							setters.add(null);
							
						} else {
							setters.add(cellSetterFactory.getCellSetter(prop.getType(), prop.getSetter(), index, propMapping.getColumnDefinition()));
						}
					} else {
						setters.add(null);
					}
				} else {
					setters.add(null);
				}
			}
		}, syncSetterStart);
		
		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {
			final Map<String, CsvMapper<?>> mappers = new HashMap<String, CsvMapper<?>>();
			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping, int index) {
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