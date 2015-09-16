package org.sfm.jdbc;


import org.sfm.csv.CellWriter;
import org.sfm.csv.impl.writer.CsvCellWriter;
import org.sfm.map.FieldKey;
import org.sfm.map.FieldMapper;
import org.sfm.map.Mapper;
import org.sfm.map.MapperConfig;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.context.KeySourceGetter;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.mapper.ContextualMapper;
import org.sfm.map.mapper.MapperImpl;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.mapper.PropertyMappingsBuilder;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.ForEachCallBack;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PreparedStatementMapperBuilder<T> {

    private final ReflectionService reflectionService;
    private final MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> mapperConfig;

    private final PropertyMappingsBuilder<T, JdbcColumnKey,  FieldMapperColumnDefinition<JdbcColumnKey>> propertyMappingsBuilder;
    private final PreparedStatementFieldMapperFactory preparedStatementFieldMapperFactory;
    private final ClassMeta<T> classMeta;

    private int currentIndex = 1;

    public PreparedStatementMapperBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> mapperConfig,
            PreparedStatementFieldMapperFactory preparedStatementFieldMapperFactory) {
        this.reflectionService = classMeta.getReflectionService();
        this.mapperConfig = mapperConfig;
        this.preparedStatementFieldMapperFactory = preparedStatementFieldMapperFactory;
        this.propertyMappingsBuilder =
                new PropertyMappingsBuilder<T, JdbcColumnKey,  FieldMapperColumnDefinition<JdbcColumnKey>>(
                        classMeta,
                        mapperConfig.propertyNameMatcherFactory(),
                        mapperConfig.mapperBuilderErrorHandler());
        this.classMeta = classMeta;
    }

    public PreparedStatementMapperBuilder<T> addColumn(String column) {
        return addColumn(column, FieldMapperColumnDefinition.<JdbcColumnKey>identity());

    }
    public PreparedStatementMapperBuilder<T> addColumn(String column,  FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition) {
        propertyMappingsBuilder.addProperty(new JdbcColumnKey(column, currentIndex++), columnDefinition);
        return this;
    }
    public PreparedStatementMapperBuilder<T> addColumn(String column, ColumnProperty... properties) {
        propertyMappingsBuilder.addProperty(new JdbcColumnKey(column, currentIndex++), FieldMapperColumnDefinition.<JdbcColumnKey>identity().add(properties));
        return this;
    }

    @SuppressWarnings("unchecked")
    public Mapper<T, PreparedStatement> mapper() {

        final List<FieldMapper<T, PreparedStatement>> mappers = new ArrayList<FieldMapper<T, PreparedStatement>>();


        final MappingContextFactoryBuilder mappingContextFactoryBuilder = new MappingContextFactoryBuilder(new KeySourceGetter<JdbcColumnKey, T>() {
            @Override
            public Object getValue(JdbcColumnKey key, T source) throws SQLException {
                throw new UnsupportedOperationException();
            }
        });

        propertyMappingsBuilder.forEachProperties(
                new ForEachCallBack<PropertyMapping<T, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>>>() {
                    int i = 0;

                    @Override
                    public void handle(PropertyMapping<T, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> pm) {
                        FieldMapper<T, PreparedStatement> fieldMapper =
                                preparedStatementFieldMapperFactory.newFieldAppender(
                                        pm,
                                        mappingContextFactoryBuilder);
                        if (fieldMapper == null) {
                            mapperConfig.mapperBuilderErrorHandler().getterNotFound("Could not find setter for " + pm);
                        }
                        mappers.add(fieldMapper);
                        i++;
                    }
                }
        );


        Mapper<T, PreparedStatement> mapper;
        FieldMapper[] fields = mappers.toArray(new FieldMapper[0]);
        if (mappers.size() < 256) {
            try {
                mapper =
                        reflectionService
                                .getAsmFactory()
                                .<T, PreparedStatement>createMapper(
                                        getKeys(),
                                        fields,
                                        new FieldMapper[0],
                                        NULL_INSTANTIATOR,
                                        TypeHelper.<T>toClass(classMeta.getType()),
                                       PreparedStatement.class
                                );
            } catch (Exception e) {
                if (mapperConfig.failOnAsm()) {
                    return ErrorHelper.rethrow(e);
                } else {
                    mapper = new MapperImpl<T, PreparedStatement>(fields, new FieldMapper[0], NULL_INSTANTIATOR);
                }
            }

        } else {
            mapper = new MapperImpl<T, PreparedStatement>(
                    fields,
                    new FieldMapper[0],
                    NULL_INSTANTIATOR);
        }

        return
            new ContextualMapper<T, PreparedStatement>(mapper, mappingContextFactoryBuilder.newFactory());
    }

    private FieldKey<?>[] getKeys() {
        return propertyMappingsBuilder.getKeys().toArray(new FieldKey[0]);
    }


    private static final NullInstantiator NULL_INSTANTIATOR = new NullInstantiator();

    public static <T> PreparedStatementMapperBuilder<T> newBuilder(Class<T> clazz) {
        ClassMeta<T> classMeta = ReflectionService.newInstance().getClassMeta(clazz);
        return PreparedStatementMapperBuilder.newBuilder(classMeta);
    }

    public static <T> PreparedStatementMapperBuilder<T> newBuilder(ClassMeta<T> classMeta) {
        return PreparedStatementMapperBuilder.newBuilder(classMeta, CsvCellWriter.DEFAULT_WRITER);
    }

    public static <T> PreparedStatementMapperBuilder<T> newBuilder(ClassMeta<T> classMeta, CellWriter cellWriter) {
        MapperConfig<JdbcColumnKey,FieldMapperColumnDefinition<JdbcColumnKey>> config =
                MapperConfig.<T, JdbcColumnKey>fieldMapperConfig();
        PreparedStatementMapperBuilder<T> builder =
                new PreparedStatementMapperBuilder<T>(
                        classMeta,
                        config, PreparedStatementFieldMapperFactory.instance());
        return builder;
    }

    private static class NullInstantiator implements Instantiator<Object, PreparedStatement> {
        @Override
        public PreparedStatement newInstance(Object o) throws Exception {
            throw new UnsupportedOperationException();
        }
    }

}
