package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.PropertyWithGetter;
import org.simpleflatmapper.map.asm.MapperAsmFactory;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.ConstantValueProperty;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.ScoredGetter;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ObjectPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.UnaryFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConstantTargetMapperBuilder<S, T, K  extends FieldKey<K>, B extends AbstractConstantTargetMapperBuilder<S, T, K , B>> {

    private static KeySourceGetter KEY_SOURCE_GETTER = new KeySourceGetter() {
        @Override
        public Object getValue(Object key, Object source) throws Exception {
            throw new UnsupportedOperationException();
        }
    };
    
    private final ReflectionService reflectionService;
    protected final MapperConfig<K, ?> mapperConfig;

    protected final PropertyMappingsBuilder<T, K> propertyMappingsBuilder;

    protected final ConstantTargetFieldMapperFactory<S, K> fieldAppenderFactory;
    protected final ClassMeta<T> classMeta;
    protected final Class<S> sourceClass;

    private int currentIndex = getStartingIndex();

    public AbstractConstantTargetMapperBuilder(
            ClassMeta<T> classMeta,
            Class<S> sourceClass, MapperConfig<K, ?> mapperConfig,
            ConstantTargetFieldMapperFactory<S, K> fieldAppenderFactory) {
        this.sourceClass = sourceClass;
        this.fieldAppenderFactory = fieldAppenderFactory;
        this.reflectionService = classMeta.getReflectionService();
        this.mapperConfig = mapperConfig;
        this.propertyMappingsBuilder =
                PropertyMappingsBuilder.of(classMeta, mapperConfig, new PropertyMappingsBuilder.PropertyPredicateFactory<K>() {
                    @Override
                    public PropertyFinder.PropertyFilter predicate(K k, Object[] objects, List<PropertyMappingsBuilder.AccessorNotFound> accessorNotFounds) {
                        return new PropertyFinder.PropertyFilter(PropertyWithGetter.INSTANCE);
                    }
                });
        this.classMeta = classMeta;
    }

    public B addColumn(String column) {
        return addColumn(column, FieldMapperColumnDefinition.<K>identity());

    }

    public B addColumn(String column, Object... properties) {
        FieldMapperColumnDefinition<K> columnDefinition = FieldMapperColumnDefinition.of(properties);
        return addColumn(newKey(column, currentIndex++, columnDefinition), columnDefinition);
    }

    public B addColumn(K key, Object... properties) {
        return addColumn(key, FieldMapperColumnDefinition.<K>identity().add(properties));
    }

    public B addColumn(String column,  FieldMapperColumnDefinition<K> columnDefinition) {
        return addColumn(newKey(column, currentIndex++, columnDefinition), columnDefinition);
    }

    @SuppressWarnings("unchecked")
    public B addColumn(K key,   FieldMapperColumnDefinition<K> columnDefinition) {
        final FieldMapperColumnDefinition<K> composedDefinition = columnDefinition.compose(mapperConfig.columnDefinitions().getColumnDefinition(key));
        final K mappedColumnKey = composedDefinition.rename(key);

        if (composedDefinition.has(ConstantValueProperty.class)) {
            ConstantValueProperty staticValueProperty = composedDefinition.lookFor(ConstantValueProperty.class);
            PropertyMeta<T, Object> meta = new ObjectPropertyMeta<T, Object>(key.getName(), classMeta.getType(), reflectionService, staticValueProperty.getType(), ScoredGetter.of(new ConstantGetter<T, Object>(staticValueProperty.getValue()), 1), null, null);
            propertyMappingsBuilder.addProperty(key, columnDefinition, meta);
        } else {
            propertyMappingsBuilder.addProperty(mappedColumnKey, composedDefinition);
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public ContextualSourceFieldMapperImpl<T, S> mapper() {

        final List<FieldMapper<T, S>> mappers = new ArrayList<FieldMapper<T, S>>();


        final MappingContextFactoryBuilder<T, K> mappingContextFactoryBuilder = new MappingContextFactoryBuilder<T, K>(keySourceGetter(), !mapperConfig.unorderedJoin());

        propertyMappingsBuilder.forEachProperties(
                new ForEachCallBack<PropertyMapping<T, ?, K>>() {
                    @Override
                    public void handle(PropertyMapping<T, ?, K> pm) {

                        if (pm.getPropertyMeta().isNonMapped()) return;

                        pm = pm.compressSubSelf();
                        preFieldProcess(mappers, pm);
                        FieldMapper<T, S> fieldMapper =
                                fieldAppenderFactory.newFieldMapper(
                                        pm,
                                        mappingContextFactoryBuilder,
                                        mapperConfig.mapperBuilderErrorHandler());
                        mappers.add(fieldMapper);
                        postFieldProcess(mappers, pm);
                    }
                }
        );
        postMapperProcess(mappers);


        AbstractMapper<T, S> mapper;
        FieldMapper[] fields = mappers.toArray(new FieldMapper[0]);
        BiInstantiator<T, MappingContext<? super T>, S> instantiator = getInstantiator();
        if (mappers.size() < 256) {
            try {
                mapper =
                        reflectionService
                                .getAsmFactory(TypeHelper.toClass(classMeta.getType()).getClassLoader())
                                .registerOrCreate(MapperAsmFactory.class, new UnaryFactory<AsmFactory, MapperAsmFactory>() {
                                    @Override
                                    public MapperAsmFactory newInstance(AsmFactory asmFactory) {
                                        return new MapperAsmFactory(asmFactory);
                                    }
                                })
                                .<T, S>createMapper(
                                        getKeys(),
                                        fields,
                                        new FieldMapper[0],
                                        instantiator,
                                        TypeHelper.<T>toClass(classMeta.getType()),
                                        sourceClass
                                );
            } catch (Throwable e) {
                if (mapperConfig.failOnAsm()) {
                    return ErrorHelper.rethrow(e);
                } else {
                    mapper = new MapperImpl<T, S>(fields, new FieldMapper[0], instantiator);
                }
            }

        } else {
            mapper = new MapperImpl<T, S>(
                    fields,
                    new FieldMapper[0],
                    instantiator);
        }

        return new ContextualSourceFieldMapperImpl<T, S>(mappingContextFactoryBuilder.build(), mapper);
    }

    protected void postMapperProcess(List<FieldMapper<T,S>> mappers) {
        // override
    }

    protected void postFieldProcess(List<FieldMapper<T,S>> mappers, PropertyMapping<T, ?, K> pm) {
        // override
    }

    protected void preFieldProcess(List<FieldMapper<T,S>> mappers, PropertyMapping<T, ?, K> pm) {
        // override
    }

    protected int getStartingIndex() {
        return 0;
    }

    protected abstract BiInstantiator<T, MappingContext<? super T>, S> getInstantiator();

    protected abstract K newKey(String column, int i, FieldMapperColumnDefinition<K> columnDefinition);

    private FieldKey<?>[] getKeys() {
        return propertyMappingsBuilder.getKeys().toArray(new FieldKey[0]);
    }

    protected KeySourceGetter<K, ? super T> keySourceGetter() {
        return KEY_SOURCE_GETTER;
    }


}
