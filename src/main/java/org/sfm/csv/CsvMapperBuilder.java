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
import org.sfm.reflect.meta.PropertyFinder;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.meta.SubPropertyMeta;

public class CsvMapperBuilder<T> {

	@SuppressWarnings("rawtypes")
	private static final Class<DelayedCellSetter[]> SOURCE = DelayedCellSetter[].class;
	
	private FieldMapperErrorHandler<Integer> fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler<Integer>();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	private RowHandlerErrorHandler rowHandlerErrorHandler = new RethrowRowHandlerErrorHandler();
	private final Type target;
	private final PropertyFinder<T> propertyFinder;
	private final ReflectionService reflectionService;
	private final Map<String, String> aliases;
	private final Map<String, CellValueReader<?>> customReaders;
	private final List<String> columns = new ArrayList<String>();

	
	private List<PropertyMeta<T, ?>> properties = new ArrayList<PropertyMeta<T, ?>>();
	private int syncSetterStart;

	private String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";

	public CsvMapperBuilder(final Type target) {
		this(target, new ReflectionService(),
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
		this.propertyFinder = classMeta.newPropertyFinder();
		this.aliases = aliases;
		this.customReaders = customReaders;
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey) {
		return addMapping(columnKey, properties.size());
	}
	
	public final CsvMapperBuilder<T> addMapping(final String columnKey, int columnIndex) {
		final String mappedColumnKey = columnToPropertyName(columnKey);
		
		final PropertyMeta<T, ?> prop = propertyFinder.findProperty(mappedColumnKey);
		
		if (prop != null) {
			while(properties.size() < columnIndex) {
				properties.add(null);
				columns.add(null);
			}
			properties.add(prop);
			columns.add(columnKey);
		} else {
			mapperBuilderErrorHandler.propertyNotFound(target, mappedColumnKey);
		}
		
		return this;
	}
	
	private void addMapping(PropertyMeta<T, ?> subProperty, int i, String column) {
		while (i >= properties.size()) {
			properties.add(null);
			columns.add(null);
		}
		properties.set(i, subProperty);
		columns.set(i, column);
		
	}
	
	public void setDefaultDateFormat(String defaultDateFormat) {
		this.defaultDateFormat = defaultDateFormat;
	}

	public final CsvMapper<T> mapper() {
		return new CsvMapperImpl<T>(getInstantiator(), getDelayedSetters(), getSetters(), getParserContextFactory(), fieldMapperErrorHandler, rowHandlerErrorHandler);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private CellSetter<T>[] getSetters() {
		Map<String, CsvMapperBuilder<?>> delegateMapperBuilders = new HashMap<String, CsvMapperBuilder<?>>();
		
		CellSetterFactory cellSetterFactory = new CellSetterFactory(customReaders);
		
		List<CellSetter<T>> setters = new ArrayList<CellSetter<T>>();

		for(int i = syncSetterStart; i < properties.size(); i++) {
			PropertyMeta<T, ?> prop = properties.get(i);
			if (prop != null) {
				if (prop instanceof ConstructorPropertyMeta) {
					throw new IllegalStateException("Unexpected ConstructorPropertyMeta at " + i);
				} else if (prop instanceof SubPropertyMeta) {
					final PropertyMeta<?, ?> powner = ((SubPropertyMeta)prop).getProperty();
					CsvMapperBuilder<?> delegateMapperBuilder = delegateMapperBuilders .get(powner.getName());
					
					if (delegateMapperBuilder == null) {
						delegateMapperBuilder = new CsvMapperBuilder(powner.getType(), powner.getClassMeta(), aliases, customReaders);
						delegateMapperBuilders.put(powner.getName(), delegateMapperBuilder);
					}
					
					delegateMapperBuilder.addMapping(((SubPropertyMeta) prop).getSubProperty(), i, columns.get(i));
					
					setters.add(null);
					
				} else {
					setters.add(cellSetterFactory.getCellSetter(prop.getSetter(), i, columns.get(i)));
				}
			} else {
				setters.add(null);
			}
		}
		
		Map<String, CsvMapper<?>> mappers = new HashMap<String, CsvMapper<?>>();
		for(int i = syncSetterStart; i < properties.size(); i++) {
			PropertyMeta<T, ?> prop = properties.get(i);
			if (prop instanceof SubPropertyMeta) {
				final String propName = ((SubPropertyMeta)prop).getProperty().getName();
				
				CsvMapper<?> mapper = mappers.get(propName);
				if (mapper == null) {
					CsvMapperBuilder<?> delegateMapperBuilder = delegateMapperBuilders .get(propName);
					mapper = delegateMapperBuilder.mapper();
					mappers.put(propName, mapper);
				}
				setters.set(i , new DelegateMarkerSetter(mapper, ((SubPropertyMeta) prop).getProperty().getSetter()));
			} 
		}
		
		return setters.toArray(new CellSetter[0]);
	}
	
	@SuppressWarnings("rawtypes")
	private ParsingContextFactory getParserContextFactory() {
		ParsingContextFactory pcf = new ParsingContextFactory(properties.size());
		
		for(int i = 0; i < properties.size(); i++) {
			PropertyMeta<T, ?> prop = properties.get(i);
			if (prop != null) {
				Class<?> target;
				if (prop instanceof SubPropertyMeta) {
					target = TypeHelper.toClass(((SubPropertyMeta) prop)
							.getFinalType());
				} else {
					target = TypeHelper.toClass(prop.getType());
				}
				if (Date.class.equals(target)) {
					pcf.setDateFormat(i, getDateFormat(i));
				}
			}
		}
		
		return pcf;
	}

	private String getDateFormat(int i) {
		return defaultDateFormat;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private  Instantiator<DelayedCellSetter[], T>  getInstantiator() throws MapperBuildingException {

		int lastConstructorArg = -1;
		Map<ConstructorParameter, Getter<DelayedCellSetter[], ?>> constructorInjections = new HashMap<ConstructorParameter, Getter<DelayedCellSetter[], ?>>();
		for(int i = 0; i < properties.size(); i++) {
			PropertyMeta<T, ?> meta = properties.get(i);
			if (meta instanceof ConstructorPropertyMeta) {
				lastConstructorArg = i;
				constructorInjections.put(((ConstructorPropertyMeta) meta).getConstructorParameter(), newDelayedGetter(i, meta.getType()));
			} else if (meta instanceof SubPropertyMeta) {
				SubPropertyMeta subMeta = (SubPropertyMeta) meta;
				if  (subMeta.getProperty() instanceof ConstructorPropertyMeta) {
					ConstructorPropertyMeta constPropMeta = (ConstructorPropertyMeta) subMeta.getProperty();
					if (!constructorInjections.containsKey(constPropMeta.getConstructorParameter())) {
						constructorInjections.put(constPropMeta.getConstructorParameter(), newDelayedGetter(i, constPropMeta.getType()));
					}
					lastConstructorArg = i;
				}
			}
		}
		
		if (lastConstructorArg != -1) {
			syncSetterStart = lastConstructorArg + 1;
		}
		
		InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();
		if (!reflectionService.isAsmPresent()) {
			try {
				return ( Instantiator<DelayedCellSetter[], T> ) instantiatorFactory.getInstantiator(SOURCE, propertyFinder.getClassToInstantiate());
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		} else {
			try {
				return instantiatorFactory.getInstantiator(SOURCE, propertyFinder.getEligibleConstructorDefinitions(), constructorInjections);
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	private Getter<DelayedCellSetter[], ?> newDelayedGetter(int i, Type type) {
		Class<?> clazz = TypeHelper.toClass(type);
		if (clazz.isPrimitive()) {
			if (boolean.class.equals(clazz)) {
				return new BooleanDelayedGetter(i);
			} else if (byte.class.equals(clazz)) {
				return new ByteDelayedGetter(i);
			} else if (char.class.equals(clazz)) {
				return new CharDelayedGetter(i);
			} else if (short.class.equals(clazz)) {
				return new ShortDelayedGetter(i);
			} else if (int.class.equals(clazz)) {
				return new IntDelayedGetter(i);
			} else if (long.class.equals(clazz)) {
				return new LongDelayedGetter(i);
			} else if (float.class.equals(clazz)) {
				return new FloatDelayedGetter(i);
			} else if (double.class.equals(clazz)) {
				return new DoubleDelayedGetter(i);
			} else {
				throw new IllegalArgumentException("Unexpected primitive " + clazz);
			}
		} else {
			return new DelayedGetter<T>(i);
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private DelayedCellSetterFactory<T, ?>[] getDelayedSetters() {
		
		Map<String, CsvMapperBuilder<?>> delegateMapperBuilders = new HashMap<String, CsvMapperBuilder<?>>();
		List<DelayedCellSetterFactory<T, ?>> delayedSetters = new ArrayList<DelayedCellSetterFactory<T, ?>>(syncSetterStart);
		CellSetterFactory cellSetterFactory = new CellSetterFactory(customReaders);

		for(int i = 0; i < syncSetterStart; i++) {
			PropertyMeta<T, ?> prop = properties.get(i);
			if (prop != null) {
				if (prop instanceof ConstructorPropertyMeta) {
					delayedSetters.add((DelayedCellSetterFactory<T, ?>)cellSetterFactory.getDelayedCellSetter(prop.getType(), i, columns.get(i)));
				}  else if (prop instanceof SubPropertyMeta) {
					final PropertyMeta<?, ?> powner = ((SubPropertyMeta)prop).getProperty();
					CsvMapperBuilder<?> delegateMapperBuilder = delegateMapperBuilders .get(powner.getName());
					
					if (delegateMapperBuilder == null) {
						delegateMapperBuilder = new CsvMapperBuilder(powner.getType(), powner.getClassMeta(), aliases, customReaders);
						delegateMapperBuilders.put(powner.getName(), delegateMapperBuilder);
					}
					
					delegateMapperBuilder.addMapping(((SubPropertyMeta) prop).getSubProperty(), i, columns.get(i));
					
					delayedSetters.add(null);
				}else {
					delayedSetters.add(cellSetterFactory.getDelayedCellSetter(prop.getSetter(), i, columns.get(i)));
				}
			} else {
				delayedSetters.add(null);
			}
		}
		
		Map<String, CsvMapper<?>> mappers = new HashMap<String, CsvMapper<?>>();
		for(int i = 0; i < syncSetterStart; i++) {
			PropertyMeta<T, ?> prop = properties.get(i);
			if (prop instanceof SubPropertyMeta) {
				PropertyMeta<?, ?> subProp = ((SubPropertyMeta) prop).getProperty();
				
				final String propName = subProp.getName();
				
				CsvMapper<?> mapper = mappers.get(propName);
				if (mapper == null) {
					CsvMapperBuilder<?> delegateMapperBuilder = delegateMapperBuilders .get(propName);
					mapper = delegateMapperBuilder.mapper();
					mappers.put(propName, mapper);
				}
				
				if (subProp instanceof ConstructorPropertyMeta) {
					delayedSetters.set(i , new DelegateMarkerDelayedCellSetter(mapper));
				} else {
					delayedSetters.set(i , new DelegateMarkerDelayedCellSetter(mapper, subProp.getSetter()));
				}
				
			}
		}
		
		return delayedSetters.toArray(new DelayedCellSetterFactory[syncSetterStart]);
	}
	
	private String columnToPropertyName(String column) {
		if (aliases == null || aliases.isEmpty()) {
			return column;
		} 
		String alias = aliases.get(column.toUpperCase());
		if (alias == null) {
			return column;
		}
		return alias;
	}
	
	public final CsvMapperBuilder<T> fieldMapperErrorHandler(final FieldMapperErrorHandler<Integer> errorHandler) {
		if (!properties.isEmpty()) {
			throw new IllegalStateException(
					"Error Handler need to be set before adding fields");
		}
		fieldMapperErrorHandler = errorHandler;
		return this;
	}

	public final CsvMapperBuilder<T> mapperBuilderErrorHandler(final MapperBuilderErrorHandler errorHandler) {
		mapperBuilderErrorHandler = errorHandler;
		return this;
	}
}