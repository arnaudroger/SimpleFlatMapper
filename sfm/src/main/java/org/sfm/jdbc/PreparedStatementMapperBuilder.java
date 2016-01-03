package org.sfm.jdbc;


import org.sfm.jdbc.impl.CollectionIndexFieldMapper;
import org.sfm.jdbc.impl.MultiIndexFieldMapper;
import org.sfm.jdbc.impl.MultiIndexQueryPreparer;
import org.sfm.jdbc.impl.SingleIndexFieldMapper;
import org.sfm.jdbc.impl.MapperQueryPreparer;
import org.sfm.jdbc.impl.PreparedStatementSetterFactory;
import org.sfm.jdbc.impl.setter.PreparedStatementIndexSetter;
import org.sfm.jdbc.named.NamedSqlQuery;
import org.sfm.map.*;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ConstantTargetFieldMapperFactory;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.Getter;
import org.sfm.reflect.IndexedGetter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.impl.ArrayIndexedGetter;
import org.sfm.reflect.impl.ArraySizeGetter;
import org.sfm.reflect.impl.ListIndexedGetter;
import org.sfm.reflect.impl.ListSizeGetter;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.utils.ForEachCallBack;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class PreparedStatementMapperBuilder<T> extends AbstractWriterBuilder<PreparedStatement, T, JdbcColumnKey, PreparedStatementMapperBuilder<T>> {

    public PreparedStatementMapperBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> mapperConfig,
            ConstantTargetFieldMapperFactory<PreparedStatement, JdbcColumnKey> preparedStatementFieldMapperFactory) {
        super(classMeta, PreparedStatement.class, mapperConfig, preparedStatementFieldMapperFactory);
    }

    private PreparedStatementMapperBuilder(PreparedStatementMapperBuilder<T> builder) {
        this(builder.classMeta, builder.mapperConfig, builder.fieldAppenderFactory);
    }

    @Override
    protected Instantiator<T, PreparedStatement> getInstantiator() {
        return new NullInstantiator<T>();
    }

    @Override
    protected JdbcColumnKey newKey(String column, int i, FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition) {
        JdbcColumnKey key = new JdbcColumnKey(column, i);

        SqlTypeColumnProperty typeColumnProperty = columnDefinition.lookFor(SqlTypeColumnProperty.class);

        if (typeColumnProperty == null) {
            FieldMapperColumnDefinition<JdbcColumnKey> globalDef = mapperConfig.columnDefinitions().getColumnDefinition(key);
            typeColumnProperty = globalDef.lookFor(SqlTypeColumnProperty.class);
        }

        if (typeColumnProperty != null) {
            return new JdbcColumnKey(key.getName(), key.getIndex(), typeColumnProperty.getSqlType(), key);
        }

        return key;
    }

    private static class NullInstantiator<T> implements Instantiator<T, PreparedStatement> {
        @Override
        public PreparedStatement newInstance(T o) throws Exception {
            throw new UnsupportedOperationException();
        }
    }

    protected int getStartingIndex() {
        return 1;
    }

    public QueryPreparer<T> to(NamedSqlQuery query) {
        return to(query, null);
    }

    public QueryPreparer<T> to(NamedSqlQuery query, String[] generatedKeys) {

        PreparedStatementMapperBuilder<T> builder =
                new PreparedStatementMapperBuilder<T>(this);

        return builder.preparedStatementMapper(query, generatedKeys);
    }

    private QueryPreparer<T> preparedStatementMapper(NamedSqlQuery query, String[] generatedKeys) {

        for(int i = 0; i < query.getParametersSize(); i++) {
            addColumn(query.getParameter(i).getName());
        }

        boolean hasMultiIndex =
                propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>>>() {
                    boolean hasMultiIndex;

                    @Override
                    public void handle(PropertyMapping<T, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> pm) {
                        hasMultiIndex |= isMultiIndex(pm.getPropertyMeta());
                    }


                }).hasMultiIndex;


        if (hasMultiIndex) {
            return new MultiIndexQueryPreparer<T>(query, buildIndexFieldMappers(), generatedKeys);
        } else {
            return new MapperQueryPreparer<T>(query, mapper(), generatedKeys);
        }
    }

    private boolean isMultiIndex(PropertyMeta<?, ?> propertyMeta) {
        return
                TypeHelper.isArray(propertyMeta.getPropertyType())
                || TypeHelper.isAssignable(List.class, propertyMeta.getPropertyType());
    }

    @SuppressWarnings("unchecked")
    public MultiIndexFieldMapper<T>[] buildIndexFieldMappers() {
        final List<MultiIndexFieldMapper<T>> fields = new ArrayList<MultiIndexFieldMapper<T>>();

        propertyMappingsBuilder.forEachProperties(new ForEachCallBack<PropertyMapping<T, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>>>() {
            final PreparedStatementSetterFactory setterFactory = new PreparedStatementSetterFactory();
            @Override
            public void handle(PropertyMapping<T, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> pm) {

                if (isMultiIndex(pm.getPropertyMeta())) {
                    fields.add(newCollectionFieldMapper(pm));
                } else {
                    fields.add(newFieldMapper(pm));
                }
            }

            private <P, C> MultiIndexFieldMapper<T> newCollectionFieldMapper(PropertyMapping<T, P, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> pm) {

                PropertyMeta<T, ?> propertyMeta = pm.getPropertyMeta();

                IndexedGetter<C, P> indexedGetter;
                IntGetter<? super C> sizeGetter;
                Getter<T, C> collectionGetter = (Getter<T, C>) propertyMeta.getGetter();


                if (TypeHelper.isAssignable(List.class, propertyMeta.getPropertyType())) {
                    indexedGetter = (IndexedGetter<C, P>) new ListIndexedGetter<P>();
                    sizeGetter = (IntGetter<C>) new ListSizeGetter();
                } else if (TypeHelper.isArray(propertyMeta.getPropertyType())) {
                    indexedGetter = (IndexedGetter<C, P>) new ArrayIndexedGetter<P>();
                    sizeGetter = new ArraySizeGetter();
                } else {
                    throw new IllegalArgumentException("Unexpected elementMeta" + propertyMeta);
                }

                PropertyMeta<C, P> childProperty = (PropertyMeta<C, P>) pm.getPropertyMeta().getPropertyClassMeta().newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("0"));

                PreparedStatementIndexSetter<P> setter = setterFactory.getIndexedSetter(pm.propertyMeta(childProperty));

                if (setter == null) {
                    mapperConfig.mapperBuilderErrorHandler().accessorNotFound("Could not find setter for " + pm);
                }

                return new CollectionIndexFieldMapper<T, C, P>(setter, collectionGetter, sizeGetter, indexedGetter);
            }

            private <P> MultiIndexFieldMapper<T> newFieldMapper(PropertyMapping<T, P, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> pm) {
                final PreparedStatementIndexSetter indexedSetter = setterFactory.getIndexedSetter(pm);
                return new SingleIndexFieldMapper<T, P>(indexedSetter, pm.getPropertyMeta().getGetter());
            }
        });

        return fields.toArray(new MultiIndexFieldMapper[fields.size()]);
    }
}
