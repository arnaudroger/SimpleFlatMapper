package org.sfm.map;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sfm.builder.FieldMapperFactory;
import org.sfm.builder.GetterFactory;
import org.sfm.jdbc.MapperBuilder;
import org.sfm.jdbc.MapperGetterAdapter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.asm.ConstructorParameter;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.ConstructorPropertyMeta;
import org.sfm.reflect.meta.PropertyFinder;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.meta.SubProperty;
import org.sfm.reflect.meta.SubPropertyMeta;

public abstract class AbstractMapperBuilderImpl<S, T, K, M extends Mapper<S, T>,  B extends MapperBuilder<S, T, K, M, B>> 
	implements MapperBuilder<S, T, K, M, B> {

	private FieldMapperErrorHandler<K> fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler<K> ();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	private final Type target;
	private final Type source;
	private final PropertyFinder<T> propertyFinder;
	private final List<FieldMapper<S, T>> fields = new ArrayList<FieldMapper<S, T>>();
	private final Map<ConstructorParameter, Getter<S, ?>> constructorInjections;
	private final List<SubProperty<S, T, K>> subProperties = new ArrayList<SubProperty<S, T, K>>();
	private final FieldMapperFactory<S, K> fieldMapperFactory;
	private final GetterFactory<S, K> getterFactory;

	protected final ReflectionService reflectionService;
	
	public AbstractMapperBuilderImpl(final Type target, final Type source, final ClassMeta<T> classMeta,   
			GetterFactory<S, K> getterFactory, FieldMapperFactory<S, K> fieldMapperFactory) throws MapperBuildingException {
		this.target = target;
		this.source = source;
		this.reflectionService = classMeta.getReflectionService();
		this.constructorInjections = new HashMap<ConstructorParameter, Getter<S,?>>();
		this.propertyFinder = classMeta.newPropertyFinder();
		this.getterFactory = getterFactory;
		this.fieldMapperFactory = fieldMapperFactory;
	}

	@SuppressWarnings("unchecked")
	public final B fieldMapperErrorHandler(final FieldMapperErrorHandler<K> errorHandler) {
		if (!fields.isEmpty()) {
			throw new IllegalStateException(
					"Error Handler need to be set before adding fields");
		}
		fieldMapperErrorHandler = errorHandler;
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public final B mapperBuilderErrorHandler(final MapperBuilderErrorHandler errorHandler) {
		mapperBuilderErrorHandler = errorHandler;
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public final B addMapping(final String propertyName, final K columnInformation) {
		FieldMapper<S, T> fieldMapper = getCustomMapper(columnInformation);
		if (fieldMapper != null) {
			fields.add(fieldMapper);
		} else {
			final PropertyMeta<T, ?> property = propertyFinder.findProperty(propertyName);
			if (property == null) {
					mapperBuilderErrorHandler.setterNotFound(target, propertyName);
			} else {
				addMapping(property, columnInformation);
			}
		}
		return (B) this;
	}

	protected FieldMapper<S, T> getCustomMapper(K columnInformation) {
		return null;
	}


	protected Class<T> getTargetClass() {
		return TypeHelper.toClass(target);
	}

	@SuppressWarnings("unchecked")
	protected Instantiator<S, T> getInstantiator() throws MapperBuildingException {
		InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();
		if (!reflectionService.isAsmPresent()) {
			try {
				return (Instantiator<S, T>) instantiatorFactory.getInstantiator(TypeHelper.toClass(source), propertyFinder.getClassToInstantiate());
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		} else {
			try {
				return instantiatorFactory.getInstantiator(TypeHelper.toClass(source), propertyFinder.getEligibleConstructorDefinitions(), constructorInjections());
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings({  "rawtypes" })
	private Map<ConstructorParameter, Getter<S, ?>> constructorInjections() {
		Map<ConstructorParameter, Getter<S, ?>> injections = new HashMap<ConstructorParameter, Getter<S, ?>>(constructorInjections);
		
		for(SubProperty<S, T, K> subProp : subProperties) {
			PropertyMeta<T, ?> prop = subProp.getSubProperty().getProperty();
			if (prop instanceof ConstructorPropertyMeta) {
				MapperBuilder<S, T, K, ?, ?> builder = subProp.getMapperBuilder();
				
				final Mapper<S, T> mapper = builder.mapper();
				
				Getter<S, T> getter = new MapperGetterAdapter<S, T>(mapper); 
				
				injections.put(((ConstructorPropertyMeta) prop).getConstructorParameter(), getter);
			}
			
		}
		
		return injections;
	}

	public abstract M mapper();
	
	@SuppressWarnings("unchecked")
	public final FieldMapper<S, T>[] fields() {
		List<FieldMapper<S, T>> fields = new ArrayList<FieldMapper<S, T>>(this.fields);
		
		for(SubProperty<S, T, K> subProp : subProperties) {
			PropertyMeta<T, ?> prop = subProp.getSubProperty().getProperty();
			
			if (!(prop instanceof ConstructorPropertyMeta)) {
				Setter<T, Object> setter = (Setter<T, Object>) prop.getSetter();
				Mapper<S, T> mapper = (Mapper<S,T>) subProp.getMapperBuilder().mapper();
				Getter<S, T> getter = new MapperGetterAdapter<S, T>(mapper);
				
				fields.add(new FieldMapperImpl<S, T, Object, K>(null, getter, setter, fieldMapperErrorHandler));
			}
			
		}
		
		return fields.toArray(new FieldMapper[fields.size()]);
	}

	private SubProperty<S, T, K> getOrAddSubPropertyMapperBuilder(SubPropertyMeta<T, ?> property) {
		
		for(SubProperty<S, T, K> subProp : subProperties) {
			if (subProp.getSubProperty().getName().equals(property.getName())) {
				return subProp;
			}
		}
		
		MapperBuilder<S, T, K, ?, ?> builder = newMapperBuilder(property.getType(), property.getClassMeta());
		SubProperty<S, T, K> subProp = new SubProperty<S, T, K>(builder, property);
		
		subProperties.add(subProp);
		
		return subProp;
	}

	protected abstract MapperBuilder<S, T, K, ?, ?> newMapperBuilder(Type type, ClassMeta<T> classMeta);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addMapping(PropertyMeta<T, ?> property, K key) {
		if (property instanceof ConstructorPropertyMeta) {
			ConstructorParameter constructorParameter = ((ConstructorPropertyMeta) property).getConstructorParameter();
			constructorInjections.put(constructorParameter, getterFactory.newGetter(constructorParameter.getType(), key));
		} else if (property instanceof SubPropertyMeta) {
			SubProperty<S, T, K> subProp = getOrAddSubPropertyMapperBuilder((SubPropertyMeta)property);
			MapperBuilder<S, T, K, ?, ?> mapperBuilder = subProp.getMapperBuilder();
			mapperBuilder.addMapping(((SubPropertyMeta) property).getSubProperty(), key);
		} else {
			fields.add(fieldMapperFactory.newFieldMapper(property.getSetter(), key, fieldMapperErrorHandler, mapperBuilderErrorHandler));
		}
	}

	@SuppressWarnings("unchecked")
	public B addMapper(FieldMapper<S, T> mapper) {
		fields.add(mapper);
		return (B) this;
	}
}