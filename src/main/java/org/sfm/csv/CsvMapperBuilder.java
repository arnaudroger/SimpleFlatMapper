package org.sfm.csv;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private List<PropertyMeta<T, ?>> properties = new ArrayList<PropertyMeta<T, ?>>();
	private int syncSetterStart;


	public CsvMapperBuilder(final Type target) {
		this(target, new ReflectionService());
	}
	@SuppressWarnings("unchecked")
	public CsvMapperBuilder(final Type target, ReflectionService reflectionService) throws MapperBuildingException {
		this(target, (ClassMeta<T>) reflectionService.getClassMeta(target));
	}
	public CsvMapperBuilder(final Type target, final ClassMeta<T> classMeta) throws MapperBuildingException {
		this.target = target;
		this.reflectionService = classMeta.getReflectionService();
		this.propertyFinder = classMeta.newPropertyFinder();
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey) {
		
		final PropertyMeta<T, ?> prop = propertyFinder.findProperty(columnKey);
		
		if (prop != null) {
			properties.add(prop);
		} else {
			mapperBuilderErrorHandler.propertyNotFound(target, columnKey);
		}
		
		return this;
	}
	
	private void addMapping(PropertyMeta<T, ?> subProperty, int i) {
		while (i >= properties.size()) {
			properties.add(null);
		}
		properties.set(i, subProperty);
	}

	public final CsvMapper<T> mapper() {
		return new CsvMapperImpl<T>(getInstantiator(), getDelayedSetters(), getSetters(), fieldMapperErrorHandler, rowHandlerErrorHandler);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private CellSetter<T>[] getSetters() {
		
		Map<String, CsvMapperBuilder<?>> delegateMapperBuilders = new HashMap<String, CsvMapperBuilder<?>>();
		CellSetterFactory cellSetterFactory = new CellSetterFactory();
		
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
						delegateMapperBuilder = new CsvMapperBuilder(powner.getType(), powner.getClassMeta());
						delegateMapperBuilders.put(powner.getName(), delegateMapperBuilder);
					}
					
					delegateMapperBuilder.addMapping(((SubPropertyMeta) prop).getSubProperty(), i);
					
					setters.add(null);
					
				} else {
					setters.add(cellSetterFactory.getCellSetter(prop.getSetter()));
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private  Instantiator<DelayedCellSetter[], T>  getInstantiator() throws MapperBuildingException {
		
		int lastConstructorArg = -1;
		Map<ConstructorParameter, Getter<DelayedCellSetter[], ?>> constructorInjections = new HashMap<ConstructorParameter, Getter<DelayedCellSetter[], ?>>();
		for(int i = 0; i < properties.size(); i++) {
			PropertyMeta<T, ?> meta = properties.get(i);
			if (meta instanceof ConstructorPropertyMeta) {
				lastConstructorArg = i;
				constructorInjections.put(((ConstructorPropertyMeta) meta).getConstructorParameter(), new DelayedGetter(i));
			} else if (meta instanceof SubPropertyMeta) {
				SubPropertyMeta subMeta = (SubPropertyMeta) meta;
				if  (subMeta.getProperty() instanceof ConstructorPropertyMeta) {
					ConstructorPropertyMeta constPropMeta = (ConstructorPropertyMeta) subMeta.getProperty();
					if (!constructorInjections.containsKey(constPropMeta.getConstructorParameter())) {
						constructorInjections.put(constPropMeta.getConstructorParameter(), new DelayedGetter(i));
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private DelayedCellSetterFactory<T, ?>[] getDelayedSetters() {
		
		Map<String, CsvMapperBuilder<?>> delegateMapperBuilders = new HashMap<String, CsvMapperBuilder<?>>();
		List<DelayedCellSetterFactory<T, ?>> delayedSetters = new ArrayList<DelayedCellSetterFactory<T, ?>>(syncSetterStart);
		CellSetterFactory cellSetterFactory = new CellSetterFactory();

		for(int i = 0; i < syncSetterStart; i++) {
			PropertyMeta<T, ?> prop = properties.get(i);
			if (prop != null) {
				if (prop instanceof ConstructorPropertyMeta) {
					delayedSetters.add((DelayedCellSetterFactory<T, ?>)cellSetterFactory.getDelayedCellSetter(prop.getType()));
				}  else if (prop instanceof SubPropertyMeta) {
					final PropertyMeta<?, ?> powner = ((SubPropertyMeta)prop).getProperty();
					CsvMapperBuilder<?> delegateMapperBuilder = delegateMapperBuilders .get(powner.getName());
					
					if (delegateMapperBuilder == null) {
						delegateMapperBuilder = new CsvMapperBuilder(powner.getType(), powner.getClassMeta());
						delegateMapperBuilders.put(powner.getName(), delegateMapperBuilder);
					}
					
					delegateMapperBuilder.addMapping(((SubPropertyMeta) prop).getSubProperty(), i);
					
					delayedSetters.add(null);
				}else {
					delayedSetters.add(cellSetterFactory.getDelayedCellSetter(prop.getSetter()));
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
					delayedSetters.set(i , new DelegateMarkerDelayedCellSetter(mapper, subProp.getType()));
				} else {
					delayedSetters.set(i , new DelegateMarkerDelayedCellSetter(mapper, subProp.getSetter()));
				}
				
			}
		}
		
		return delayedSetters.toArray(new DelayedCellSetterFactory[syncSetterStart]);
	}
}