package org.sfm.jdbc.spring;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.map.PropertyWithGetter;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.error.RethrowMapperBuilderErrorHandler;
import org.sfm.map.mapper.DefaultPropertyNameMatcherFactory;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.mapper.PropertyMappingsBuilder;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.ForEachCallBack;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;

import java.lang.reflect.Field;
import java.util.List;

public final class SqlParameterSourceBuilder<T> {


    private final PropertyMappingsBuilder<T, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> builder;
    private int index = 1;

    private static final Field paramNamesField;
    static {
        Field f = null;
        try {
            f = ParsedSql.class.getDeclaredField("parameterNames");
            f.setAccessible(true);
        } catch (Exception e) {
            // ignore
        }
        paramNamesField = f;
    }

    public SqlParameterSourceBuilder(ClassMeta<T> classMeta) {
        builder = new PropertyMappingsBuilder<T, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>>(classMeta,
                DefaultPropertyNameMatcherFactory.DEFAULT, new
                RethrowMapperBuilderErrorHandler(),
                new PropertyWithGetter()
                );
    }

    public SqlParameterSourceBuilder<T> add(String column) {
        builder.addProperty(new JdbcColumnKey(column, index++), FieldMapperColumnDefinition.<JdbcColumnKey>identity());
        return this;
    }

    public SqlParameterSourceFactory<T> buildFactory(ParsedSql parsedSql) {
        if (paramNamesField == null) {
            throw new IllegalArgumentException("Unable to gain access to paramNames field in parsedSql");
        }

        try {
            List<String> names = (List<String>) paramNamesField.get(parsedSql);

            for(String name : names) {
                add(name);
            }
        } catch (IllegalAccessException e) {
            ErrorHelper.rethrow(e);
        }
        return buildFactory();
    }

    @SuppressWarnings("unchecked")
    public PlaceHolderValueGetterSource<T> buildSource() {
        final PlaceHolderValueGetter<T>[] parameters = new PlaceHolderValueGetter[builder.size()];
        builder.forEachProperties(
                new ForEachCallBack<PropertyMapping<T,?,JdbcColumnKey,FieldMapperColumnDefinition<JdbcColumnKey>>>(){
                    int i = 0;
                    @Override
                    public void handle(PropertyMapping<T, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> pm) {
                        PlaceHolderValueGetter parameter =
                                new PlaceHolderValueGetter(pm.getColumnKey().getName(),
                                        StatementCreatorUtils.javaTypeToSqlParameterType(TypeHelper.toClass(pm.getPropertyMeta().getPropertyType())),
                                        null, pm.getPropertyMeta().getGetter());
                        parameters[i] = parameter;
                        i++;
                    }
                });

        return parameters.length > 10
                ? new ArrayPlaceHolderValueGetterSource<T>(parameters)
                : new MapPlaceHolderValueGetterSource<T>(parameters)
                ;
    }

    public SqlParameterSourceFactory<T> buildFactory() {
        return new SqlParameterSourceFactory<T>(buildSource());
    }
}
