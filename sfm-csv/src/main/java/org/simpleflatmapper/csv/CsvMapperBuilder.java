package org.simpleflatmapper.csv;

import org.simpleflatmapper.csv.property.MandatoryColumnProperty;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.mapper.ColumnDefinitionProvider;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.mapper.PropertyMappingsBuilder;
import org.simpleflatmapper.map.mapper.PropertyWithSetterOrConstructor;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ConstructorPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.meta.SelfPropertyMeta;
import org.simpleflatmapper.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.util.UnaryFactory;
import org.simpleflatmapper.csv.property.CustomReaderProperty;
import org.simpleflatmapper.csv.impl.*;
import org.simpleflatmapper.csv.impl.asm.CsvAsmFactory;
import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandlerFactory;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.map.property.DefaultDateFormatProperty;
import org.simpleflatmapper.map.property.DefaultValueProperty;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.Named;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvMapperBuilder<T> {

	public static final CustomReaderProperty NONE_READER_PROPERTY = new CustomReaderProperty(new CellValueReader() {
		@Override
		public Object read(char[] chars, int offset, int length, ParsingContext parsingContext) {
			throw new UnsupportedOperationException("Default value should not try to read");
		}
	});
	private final Type target;
	private final ReflectionService reflectionService;
	private final MapperConfig<CsvColumnKey, CsvColumnDefinition> mapperConfig;

    private final int minDelayedSetter;

	private final CellValueReaderFactory cellValueReaderFactory;
	private final PropertyMappingsBuilder<T, CsvColumnKey, CsvColumnDefinition> propertyMappingsBuilder;

	private String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";

    public CsvMapperBuilder(final Type target) {
		this(target, ReflectionService.newInstance());
	}
	
	@SuppressWarnings("unchecked")
	public CsvMapperBuilder(final Type target, ReflectionService reflectionService) {
		this(target, (ClassMeta<T>)reflectionService.getClassMeta(target));
	}

	public CsvMapperBuilder(final Type target, final ClassMeta<T> classMeta) {
		this(target, classMeta, new IdentityCsvColumnDefinitionProvider());
	}

	public CsvMapperBuilder(final Type target, final ClassMeta<T> classMeta, ColumnDefinitionProvider<CsvColumnDefinition, CsvColumnKey> columnDefinitionProvider) {
		this(target, classMeta, 0,
                new CellValueReaderFactoryImpl(), MapperConfig.<CsvColumnKey, CsvColumnDefinition>config(columnDefinitionProvider));
	}

	public CsvMapperBuilder(final Type target,
							final ClassMeta<T> classMeta,
							int minDelayedSetter,
							CellValueReaderFactory cellValueReaderFactory,
							MapperConfig<CsvColumnKey, CsvColumnDefinition> mapperConfig
	) throws MapperBuildingException {
		this.target = target;
        this.minDelayedSetter = minDelayedSetter;
        this.reflectionService = classMeta.getReflectionService();
		this.propertyMappingsBuilder =
				PropertyMappingsBuilder.<T, CsvColumnKey, CsvColumnDefinition>of(classMeta, mapperConfig, PropertyWithSetterOrConstructor.INSTANCE);
		this.cellValueReaderFactory = cellValueReaderFactory;
		this.mapperConfig = mapperConfig;
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey) {
		return addMapping(columnKey, propertyMappingsBuilder.size());
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey, int columnIndex) {
		return addMapping(new CsvColumnKey(columnKey, columnIndex), CsvColumnDefinition.identity());
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey, final CsvColumnDefinition columnDefinition) {
		return addMapping(columnKey, propertyMappingsBuilder.size(), columnDefinition);
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey, int columnIndex, CsvColumnDefinition columnDefinition) {
		return addMapping(new CsvColumnKey(columnKey, columnIndex), columnDefinition);
	}

	public final CsvMapperBuilder<T> addMapping(final CsvColumnKey key, final CsvColumnDefinition columnDefinition) {
		final CsvColumnDefinition composedDefinition = CsvColumnDefinition.compose(getColumnDefinition(key), columnDefinition);
		final CsvColumnKey mappedColumnKey = composedDefinition.rename(key);

		propertyMappingsBuilder.addProperty(mappedColumnKey, composedDefinition);

		return this;
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey, final Object... properties) {
		return addMapping(columnKey, propertyMappingsBuilder.size(), properties);
	}

	public final CsvMapperBuilder<T> addMapping(final String columnKey, int columnIndex, final Object... properties) {
		return addMapping(new CsvColumnKey(columnKey, columnIndex), properties);
	}

	public final CsvMapperBuilder<T> addMapping(final CsvColumnKey key, final Object... properties) {
		return addMapping(key, CsvColumnDefinition.of(properties));
	}

	private <P> void addMapping(PropertyMeta<T, P> propertyMeta, final CsvColumnKey key, final CsvColumnDefinition columnDefinition) {
		propertyMappingsBuilder.addProperty(key, columnDefinition, propertyMeta);
	}
	
	private CsvColumnDefinition getColumnDefinition(CsvColumnKey key) {
		CsvColumnDefinition columnDefinition = mapperConfig.columnDefinitions().getColumnDefinition(key);
		return CsvColumnDefinition.compose(CsvColumnDefinition.of(new DefaultDateFormatProperty(defaultDateFormat)), columnDefinition);
	}


	public void setDefaultDateFormat(String defaultDateFormat) {
		this.defaultDateFormat = defaultDateFormat;
	}

	public final CsvMapper<T> mapper() {

		mapperConfig
				.columnDefinitions()
				.forEach(
						DefaultValueProperty.class,
						new BiConsumer<Predicate<? super CsvColumnKey>, DefaultValueProperty>() {
							@Override
							public void accept(Predicate<? super CsvColumnKey> predicate, DefaultValueProperty columnProperty) {
								if (propertyMappingsBuilder.hasKey(predicate)){
									return;
								}
								if (predicate instanceof Named) {
									String name = ((Named)predicate).getName();
									final CsvColumnDefinition columnDefinition = CsvColumnDefinition.identity().add(columnProperty, NONE_READER_PROPERTY);
									CsvColumnKey key = new CsvColumnKey(name, propertyMappingsBuilder.size());
									propertyMappingsBuilder.addPropertyIfPresent(key, columnDefinition);
								}
							}
						});

        ParsingContextFactoryBuilder parsingContextFactoryBuilder = new ParsingContextFactoryBuilder(propertyMappingsBuilder.maxIndex() + 1);

		ConstructorParametersDelayedCellSetter constructorParams = buildConstructorParametersDelayedCellSetter();
        @SuppressWarnings("unchecked")
		final Instantiator<CsvMapperCellHandler<T>, T> instantiator = getInstantiator(constructorParams.parameterGetterMap);
        final CsvColumnKey[] keys = getKeys();

        // will build the context factory builder
        final CellSetter<T>[] setters = getSetters(parsingContextFactoryBuilder, constructorParams.index);
        final DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories = buildDelayedSetters(parsingContextFactoryBuilder, constructorParams.index, constructorParams.hasKeys);

        // needs to happen last
        final CsvMapperCellHandlerFactory<T> csvMapperCellHandlerFactory = newCsvMapperCellHandlerFactory(parsingContextFactoryBuilder, instantiator, keys, delayedCellSetterFactories, setters);

		int maxMandatoryIndex = propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition>>() {
			int maxMandatoryIndex = 0;

			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> pm) {
				if (pm.getColumnDefinition().has(MandatoryColumnProperty.class)) {
					maxMandatoryIndex = Math.max(maxMandatoryIndex, pm.getColumnKey().getIndex());
				}
			}
		}).maxMandatoryIndex;

		return new CsvMapperImpl<T>(csvMapperCellHandlerFactory,
                delayedCellSetterFactories,
                setters, getJoinKeys(), mapperConfig.consumerErrorHandler(),
				maxMandatoryIndex);
	}

    private CsvMapperCellHandlerFactory<T> newCsvMapperCellHandlerFactory(ParsingContextFactoryBuilder parsingContextFactoryBuilder,
                                                                          Instantiator<CsvMapperCellHandler<T>, T> instantiator,
                                                                          CsvColumnKey[] keys,
                                                                          DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories,
                                                                          CellSetter<T>[] setters
    ) {

        final ParsingContextFactory parsingContextFactory = parsingContextFactoryBuilder.newFactory();
        if (isEligibleForAsmHandler()) {
            try {
                return reflectionService.getAsmFactory()
						.registerOrCreate(CsvAsmFactory.class, new UnaryFactory<AsmFactory, CsvAsmFactory>() {
					@Override
					public CsvAsmFactory newInstance(AsmFactory asmFactory) {
						return new CsvAsmFactory(asmFactory);
					}
			}).<T>createCsvMapperCellHandler(target, delayedCellSetterFactories, setters,
                        instantiator, keys, parsingContextFactory, mapperConfig.fieldMapperErrorHandler(),
								 mapperConfig.maxMethodSize());
            } catch (Exception e) {
                if (mapperConfig.failOnAsm()) {
                    return ErrorHelper.rethrow(e);
                } else {
                    return new CsvMapperCellHandlerFactory<T>(instantiator, keys, parsingContextFactory, mapperConfig.fieldMapperErrorHandler());
                }
            }
        } else {
            return new CsvMapperCellHandlerFactory<T>(instantiator, keys, parsingContextFactory, mapperConfig.fieldMapperErrorHandler());
        }
    }

    private boolean isEligibleForAsmHandler() {
        return reflectionService.hasAsmFactory()
                &&  this.propertyMappingsBuilder.size() < mapperConfig.asmMapperNbFieldsLimit();
    }


    private CsvColumnKey[] getKeys() {
		return propertyMappingsBuilder.getKeys().toArray(new CsvColumnKey[0]);
	}

    private CsvColumnKey[] getJoinKeys() {
        final List<CsvColumnKey> keys = new ArrayList<CsvColumnKey>();

        propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition>>() {
            @Override
            public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> pm) {
                if (pm.getColumnDefinition().isKey() && pm.getColumnDefinition().keyAppliesTo().test(pm.getPropertyMeta())) {
                    keys.add(pm.getColumnKey());
                }
            }
        });
        return keys.toArray(new CsvColumnKey[0]);
    }

	private  Instantiator<CsvMapperCellHandler<T>, T>  getInstantiator(Map<Parameter, Getter<? super CsvMapperCellHandler<T>, ?>> params) throws MapperBuildingException {
		InstantiatorFactory instantiatorFactory = reflectionService.getInstantiatorFactory();

		try {
			return new MapperInstantiatorFactory(instantiatorFactory)
					.getInstantiator(
						new TypeReference<CsvMapperCellHandler<T>>(){}.getType(),
						target,
						propertyMappingsBuilder,
						params,
						new GetterFactory<CsvMapperCellHandler<T>, CsvColumnKey>() {
							@Override
							public <P> Getter<CsvMapperCellHandler<T>, P> newGetter(Type target, CsvColumnKey key, Object... properties) {
								return newDelayedGetter(target, key, properties);
							}
						}, reflectionService.builderIgnoresNullValues());
		} catch(Exception e) {
            return ErrorHelper.rethrow(e);
		}
	}

	private ConstructorParametersDelayedCellSetter<T> buildConstructorParametersDelayedCellSetter() {
        final BuildConstructorInjections buildConstructorInjections = new BuildConstructorInjections();

        propertyMappingsBuilder.forEachProperties(buildConstructorInjections);

		return new ConstructorParametersDelayedCellSetter<T>(
				buildConstructorInjections.constructorInjections,
                buildConstructorInjections.delayedSetterEnd,
				buildConstructorInjections.hasKeys);
	}


    @SuppressWarnings({ "unchecked" })
	private DelayedCellSetterFactory<T, ?>[] buildDelayedSetters(final ParsingContextFactoryBuilder parsingContextFactoryBuilder, int delayedSetterEnd, boolean hasKeys) {

		final Map<String, CsvMapperBuilder<?>> delegateMapperBuilders = new HashMap<String, CsvMapperBuilder<?>>();
        final Map<String, Integer> propertyToMapperIndex = new HashMap<String, Integer>();

		final DelayedCellSetterFactory<T, ?>[] delayedSetters = new DelayedCellSetterFactory[delayedSetterEnd];

        final int newMinDelayedSetter = minDelayedSetter != 0 ? minDelayedSetter : hasKeys ? delayedSetterEnd : 0;

		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {
			final CellSetterFactory cellSetterFactory = new CellSetterFactory(cellValueReaderFactory, mapperConfig.mapperBuilderErrorHandler());

			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping) {
				if (propMapping != null) {
					PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();
					CsvColumnKey key = propMapping.getColumnKey();
					if (prop != null) {
						if (prop.isSubProperty()) {
							addSubProperty(delegateMapperBuilders, prop, key, propMapping.getColumnDefinition());
						}else {
							delayedSetters[propMapping.getColumnKey().getIndex()] =  cellSetterFactory.getDelayedCellSetter(prop, key.getIndex(), propMapping.getColumnDefinition(), parsingContextFactoryBuilder);
						}
					}
				}
			}

			private <I, P> void addSubProperty(
					Map<String, CsvMapperBuilder<?>> delegateMapperBuilders,
					PropertyMeta<T, P> prop, CsvColumnKey key, CsvColumnDefinition columnDefinition) {
				SubPropertyMeta<T, I, P> subPropertyMeta = (SubPropertyMeta<T, I, P>)prop;

				final PropertyMeta<T, I> propOwner = subPropertyMeta.getOwnerProperty();
				CsvMapperBuilder<I> delegateMapperBuilder = (CsvMapperBuilder<I>) delegateMapperBuilders .get(propOwner.getName());

				if (delegateMapperBuilder == null) {
					delegateMapperBuilder = new CsvMapperBuilder<I>(propOwner.getPropertyType(), propOwner.getPropertyClassMeta(), newMinDelayedSetter, cellValueReaderFactory, mapperConfig);
					delegateMapperBuilders.put(propOwner.getName(), delegateMapperBuilder);
				}

                Integer currentIndex = propertyToMapperIndex.get(propOwner.getName());
                if (currentIndex == null || currentIndex < key.getIndex()) {
                    propertyToMapperIndex.put(propOwner.getName(), key.getIndex());
                }

				delegateMapperBuilder.addMapping(subPropertyMeta.getSubProperty(), key, columnDefinition);

			}
		}, 0, delayedSetterEnd);


		final Map<String, CsvMapper<?>> mappers = new HashMap<String, CsvMapper<?>>();


		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {
			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping) {
				if (propMapping == null) return;

				PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();

				if (prop.isSubProperty()) {
					addSubPropertyDelayedSetter(delegateMapperBuilders,	delayedSetters, propMapping.getColumnKey().getIndex(), prop);
				}
			}

			private <I, P> void addSubPropertyDelayedSetter(
					Map<String, CsvMapperBuilder<?>> delegateMapperBuilders,
					DelayedCellSetterFactory<T, ?>[] delayedSetters,
					int setterIndex,
					PropertyMeta<T, P> prop) {
				PropertyMeta<T, I> subProp = ((SubPropertyMeta<T, I, P>) prop).getOwnerProperty();

				final String propName = subProp.getName();

				CsvMapper<I> mapper = (CsvMapper<I>) mappers.get(propName);

                if (mapper == null) {
                    CsvMapperBuilder<I> delegateMapperBuilder = (CsvMapperBuilder<I>) delegateMapperBuilders.get(propName);
                    mapper = delegateMapperBuilder.mapper();
                    mappers.put(propName, mapper);
                }

                int indexOfMapper = propertyToMapperIndex.get(propName);
                Setter<? super T, ? super I> setter = null;

                if (!subProp.isConstructorProperty()) {
                    setter =  subProp.getSetter();
				}

                delayedSetters[setterIndex] = new DelegateMarkerDelayedCellSetterFactory<T, I>(mapper, setter, setterIndex, indexOfMapper);
            }
		}, 0, delayedSetterEnd);


		return delayedSetters;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private CellSetter<T>[] getSetters(final ParsingContextFactoryBuilder parsingContextFactoryBuilder, final int delayedSetterEnd) {
		final Map<String, CsvMapperBuilder<?>> delegateMapperBuilders = new HashMap<String, CsvMapperBuilder<?>>();

        final Map<String, Integer> propertyToMapperIndex = new HashMap<String, Integer>();

        // calculate maxIndex
		int maxIndex = propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {

			int maxIndex = delayedSetterEnd;
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
							final PropertyMeta<?, ?> propOwner = ((SubPropertyMeta)prop).getOwnerProperty();
							CsvMapperBuilder<?> delegateMapperBuilder = delegateMapperBuilders .get(propOwner.getName());
							
							if (delegateMapperBuilder == null) {
								delegateMapperBuilder =
										new CsvMapperBuilder(propOwner.getPropertyType(), propOwner.getPropertyClassMeta(), minDelayedSetter, cellValueReaderFactory, mapperConfig);
								delegateMapperBuilders.put(propOwner.getName(), delegateMapperBuilder);
							}


                            Integer currentIndex = propertyToMapperIndex.get(propOwner.getName());
                            if (currentIndex == null || currentIndex < key.getIndex()) {
                                propertyToMapperIndex.put(propOwner.getName(), key.getIndex());
                            }

							delegateMapperBuilder.addMapping(((SubPropertyMeta) prop).getSubProperty(), key, propMapping.getColumnDefinition());
							
						}
					}
				}
			}
		}, delayedSetterEnd).maxIndex;


        // builder se setters
		final CellSetter<T>[] setters = new CellSetter[maxIndex + 1 - delayedSetterEnd];


		propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>>() {
			final Map<String, CsvMapperImpl<?>> mappers = new HashMap<String, CsvMapperImpl<?>>();
			final CellSetterFactory cellSetterFactory = new CellSetterFactory(cellValueReaderFactory, mapperConfig.mapperBuilderErrorHandler());

			@Override
			public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping) {
				if (propMapping == null) {
					return;
				}
				
				PropertyMeta<T, ?> prop = propMapping.getPropertyMeta();

				if (prop == null || prop instanceof SelfPropertyMeta) {
					return;
				}

				if (prop instanceof SubPropertyMeta) {
                    DelegateMarkerSetter<T, ?> delegateMarkerSetter = getDelegateMarkerSetter((SubPropertyMeta) prop);

                    setters[propMapping.getColumnKey().getIndex()- delayedSetterEnd] = delegateMarkerSetter;
				} else {
					setters[propMapping.getColumnKey().getIndex()- delayedSetterEnd] = cellSetterFactory.getCellSetter(prop, propMapping.getColumnKey().getIndex(), propMapping.getColumnDefinition(), parsingContextFactoryBuilder);
				}
			}

            private <I, P> DelegateMarkerSetter<T, I> getDelegateMarkerSetter(SubPropertyMeta<T, I, P> prop) {
                final String propName = prop.getOwnerProperty().getName();

                CsvMapperImpl<I> mapper = (CsvMapperImpl<I>) mappers.get(propName);
                if (mapper == null) {
                    CsvMapperBuilder<I> delegateMapperBuilder = (CsvMapperBuilder<I>) delegateMapperBuilders.get(propName);
                    mapper = (CsvMapperImpl<I>) delegateMapperBuilder.mapper();
                    mappers.put(propName, mapper);
                }

                int indexOfMapper = propertyToMapperIndex.get(propName);

                return new DelegateMarkerSetter<T, I>(mapper, prop.getOwnerProperty().getSetter(), indexOfMapper);
            }
        }, delayedSetterEnd);
		
		
		return setters;
	}

	public void addDefaultHeaders() {
		addDefaultHeaders(propertyMappingsBuilder.getClassMeta(), "");
	}

	private <P> void addDefaultHeaders(final ClassMeta<P> classMeta, final String prefix) {
		classMeta.forEachProperties(new Consumer<PropertyMeta<P,?>>() {

			@Override
			public void accept(PropertyMeta<P, ?> propertyMeta) {

				String currentName = prefix +  propertyMeta.getPath();

				if (cellValueReaderFactory.getReader(propertyMeta.getPropertyType(), 0, CsvColumnDefinition.identity(), new ParsingContextFactoryBuilder(1)) == null) {
					addDefaultHeaders(propertyMeta.getPropertyClassMeta(), currentName + "_");
				} else {
					addMapping(currentName);
				}

			}
		});
	}

	private class BuildConstructorInjections implements ForEachCallBack<PropertyMapping<T,?,CsvColumnKey, CsvColumnDefinition>> {
        final CellSetterFactory cellSetterFactory;
        private final Map<Parameter, Getter<? super CsvMapperCellHandler<T>, ?>> constructorInjections;
        int delayedSetterEnd;
        boolean hasKeys;

        public BuildConstructorInjections() {
            this.constructorInjections = new HashMap<Parameter, Getter<? super CsvMapperCellHandler<T>, ?>>();
            cellSetterFactory = new CellSetterFactory(cellValueReaderFactory, mapperConfig.mapperBuilderErrorHandler());
            delayedSetterEnd = minDelayedSetter;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handle(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping) {
            if (propMapping == null) return;

            PropertyMeta<T, ?> meta = propMapping.getPropertyMeta();

            hasKeys = hasKeys || propMapping.getColumnDefinition().isKey();

            if (meta == null) return;
            final CsvColumnKey key = propMapping.getColumnKey();
            if (meta.isConstructorProperty()) {
                Getter<CsvMapperCellHandler<T>, ?> delayedGetter = newDelayedGetter(meta.getPropertyType(), key, propMapping.getColumnDefinition().properties());
                constructorInjections.put(((ConstructorPropertyMeta<T, ?>) meta).getParameter(), delayedGetter);
            } else if (meta.isSubProperty()) {
                SubPropertyMeta<T, ?, ?> subMeta = (SubPropertyMeta<T, ?, ?>) meta;
                if (subMeta.getOwnerProperty().isConstructorProperty()) {
                    ConstructorPropertyMeta<?, ?> constPropMeta = (ConstructorPropertyMeta<?, ?>) subMeta.getOwnerProperty();
                    Getter<CsvMapperCellHandler<T>, ?> delayedGetter = cellSetterFactory.newDelayedGetter(key, constPropMeta.getPropertyType());
                    constructorInjections.put(constPropMeta.getParameter(), delayedGetter);
                }
            }
            
            if (needDelayedSetter(propMapping)) {
				delayedSetterEnd = Math.max(delayedSetterEnd, key.getIndex() + 1);
			}
        }

		private boolean needDelayedSetter(PropertyMapping<T, ?, CsvColumnKey, CsvColumnDefinition> propMapping) {
			PropertyMeta<T, ?> meta = propMapping.getPropertyMeta();

			if (meta.isSelf()) return true;
			
			if (hasConstructor(meta)) return true;
			
			if (propMapping.getColumnDefinition().isKey()
					&& propMapping.getColumnDefinition().keyAppliesTo().test(propMapping.getPropertyMeta())) {
        		return true;
			}
			
			if (propMapping.getColumnDefinition().has(DefaultValueProperty.class)) {
        		return true;
			}
        	
			return false;
		}

		private boolean hasConstructor(PropertyMeta<?, ?> meta) {
			if (meta.isConstructorProperty()) {
				return true;
            }
			if (meta.isSubProperty()) {
				SubPropertyMeta<?, ?, ?> subMeta = (SubPropertyMeta<?, ?, ?>) meta;
				return hasConstructor(subMeta.getOwnerProperty());// || hasConstructor(subMeta.getSubProperty());
			}
			return false;
		}
	}

	private <P> Getter<CsvMapperCellHandler<T>, P> newDelayedGetter(Type propertyType, CsvColumnKey key, Object... properties) {
		final CellSetterFactory cellSetterFactory = new CellSetterFactory(cellValueReaderFactory, mapperConfig.mapperBuilderErrorHandler());

		for(Object prop :properties) {
			if (prop instanceof DefaultValueProperty) {
				return new DelayedGetter<T, P>(key.getIndex());
			}
		}
		return cellSetterFactory.newDelayedGetter(key, propertyType);
	}

	// Map<Parameter, Getter<? super CsvMapperCellHandler<T>, ?>>, Integer, Boolean
	private static class ConstructorParametersDelayedCellSetter<T> {
		private final Map<Parameter, Getter<? super CsvMapperCellHandler<T>, ?>> parameterGetterMap;
		private final int index;
		private final boolean hasKeys;

		private ConstructorParametersDelayedCellSetter(Map<Parameter, Getter<? super CsvMapperCellHandler<T>, ?>> parameterGetterMap, int index, boolean hasKeys) {
			this.parameterGetterMap = parameterGetterMap;
			this.index = index;
			this.hasKeys = hasKeys;
		}
	}

}