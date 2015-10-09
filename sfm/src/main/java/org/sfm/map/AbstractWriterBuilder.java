package org.sfm.map;


import org.sfm.map.column.ColumnProperty;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.column.ConstantValueProperty;
import org.sfm.map.context.KeySourceGetter;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.mapper.ConstantTargetFieldMapperFactory;
import org.sfm.map.mapper.ContextualMapper;
import org.sfm.map.mapper.MapperImpl;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.mapper.PropertyMappingsBuilder;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.impl.ConstantGetter;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.ObjectPropertyMeta;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.ForEachCallBack;
import org.sfm.utils.Predicate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWriterBuilder<S, T, K  extends FieldKey<K>, B extends AbstractWriterBuilder<S, T, K , B>> {

    private final ReflectionService reflectionService;
    private final MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig;

    private final PropertyMappingsBuilder<T, K,  FieldMapperColumnDefinition<K>> propertyMappingsBuilder;

    private final ConstantTargetFieldMapperFactory<S, K> fieldAppenderFactory;
    protected final ClassMeta<T> classMeta;
    private final Class<S> sourceClass;

    private int currentIndex = getStartingIndex();
    private List<Tuple2<K, FieldMapperColumnDefinition<K>>> staticValues;

    public AbstractWriterBuilder(
            ClassMeta<T> classMeta,
            Class<S> sourceClass, MapperConfig<K, FieldMapperColumnDefinition<K>> mapperConfig,
            ConstantTargetFieldMapperFactory<S, K> fieldAppenderFactory) {
        this.sourceClass = sourceClass;
        this.fieldAppenderFactory = fieldAppenderFactory;
        this.reflectionService = classMeta.getReflectionService();
        this.mapperConfig = mapperConfig;
        this.propertyMappingsBuilder =
                new PropertyMappingsBuilder<T, K,  FieldMapperColumnDefinition<K>>(
                        classMeta,
                        mapperConfig.propertyNameMatcherFactory(),
                        mapperConfig.mapperBuilderErrorHandler(), new Predicate<PropertyMeta<?, ?>>() {
                    @Override
                    public boolean test(PropertyMeta<?, ?> propertyMeta) {
                        return propertyMeta.getGetter() != null;
                    }
                });
        this.classMeta = classMeta;
        staticValues = new ArrayList<Tuple2<K, FieldMapperColumnDefinition<K>>>();
    }

    public B addColumn(String column) {
        return addColumn(column, FieldMapperColumnDefinition.<K>identity());

    }

    public B addColumn(String column, ColumnProperty... properties) {
        return addColumn(newKey(column, currentIndex++), properties);
    }

    public B addColumn(K key, ColumnProperty... properties) {
        return addColumn(key, FieldMapperColumnDefinition.<K>identity().add(properties));
    }

    public B addColumn(String column,  FieldMapperColumnDefinition<K> columnDefinition) {
        return addColumn(newKey(column, currentIndex++), columnDefinition);
    }

    @SuppressWarnings("unchecked")
    public B addColumn(K key,   FieldMapperColumnDefinition<K> columnDefinition) {
        final FieldMapperColumnDefinition<K> composedDefinition = columnDefinition.compose(mapperConfig.columnDefinitions().getColumnDefinition(key));
        final K mappedColumnKey = composedDefinition.rename(key);

        if (composedDefinition.has(ConstantValueProperty.class)) {
            staticValues.add(new Tuple2<K, FieldMapperColumnDefinition<K>>(key, composedDefinition));
        } else {
            propertyMappingsBuilder.addProperty(mappedColumnKey, composedDefinition);
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public Mapper<T, S> mapper() {

        final List<FieldMapper<T, S>> mappers = new ArrayList<FieldMapper<T, S>>();


        final MappingContextFactoryBuilder mappingContextFactoryBuilder = new MappingContextFactoryBuilder(new KeySourceGetter<K, T>() {
            @Override
            public Object getValue(K key, T source) throws SQLException {
                throw new UnsupportedOperationException();
            }
        });

        for(Tuple2<K, FieldMapperColumnDefinition<K>> staticProperty : staticValues) {
            ConstantValueProperty staticValueProperty = staticProperty.second().lookFor(ConstantValueProperty.class);
            PropertyMeta<T, Object> meta = new ObjectPropertyMeta<>(staticProperty.first().getName(), reflectionService, staticValueProperty.getType(), new ConstantGetter<T, Object>(staticValueProperty.getValue()), null);
            PropertyMapping<T, Object, K, FieldMapperColumnDefinition<K>> pm = new PropertyMapping<T, Object, K, FieldMapperColumnDefinition<K>>(meta, staticProperty.first(), staticProperty.second());
            mappers.add(fieldAppenderFactory.newFieldMapper(pm, mappingContextFactoryBuilder, mapperConfig.mapperBuilderErrorHandler()));
        }

        propertyMappingsBuilder.forEachProperties(
                new ForEachCallBack<PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>>>() {
                    @Override
                    public void handle(PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> pm) {
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


        Mapper<T, S> mapper;
        FieldMapper[] fields = mappers.toArray(new FieldMapper[0]);
        Instantiator<T, S> instantiator = getInstantiator();
        if (mappers.size() < 256) {
            try {
                mapper =
                        reflectionService
                                .getAsmFactory()
                                .<T, S>createMapper(
                                        getKeys(),
                                        fields,
                                        new FieldMapper[0],
                                        instantiator,
                                        TypeHelper.<T>toClass(classMeta.getType()),
                                        sourceClass
                                );
            } catch (Exception e) {
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

        return
            new ContextualMapper<T, S>(mapper, mappingContextFactoryBuilder.newFactory());
    }

    protected void postMapperProcess(List<FieldMapper<T,S>> mappers) {
    }

    protected void postFieldProcess(List<FieldMapper<T,S>> mappers, PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> pm) {
    }

    protected void preFieldProcess(List<FieldMapper<T,S>> mappers, PropertyMapping<T, ?, K, FieldMapperColumnDefinition<K>> pm) {
    }

    protected int getStartingIndex() {
        return 0;
    }

    protected abstract Instantiator<T, S> getInstantiator();

    protected abstract K newKey(String column, int i);

    private FieldKey<?>[] getKeys() {
        return propertyMappingsBuilder.getKeys().toArray(new FieldKey[0]);
    }

}
