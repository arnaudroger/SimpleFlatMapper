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
import org.sfm.utils.ForEachCallBack;
import org.springframework.jdbc.core.StatementCreatorUtils;

public final class SqlParameterSourceBuilder<T> {
    private final PropertyMappingsBuilder<T, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> builder;
    private int index = 1;

    public SqlParameterSourceBuilder(Class<T> target) {
        this(ReflectionService.newInstance().<T>getClassMeta(target));
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

    @SuppressWarnings("unchecked")
    public StaticSqlParameters<T> build() {
        final PlaceHolder<T>[] parameters = new PlaceHolder[builder.size()];
        builder.forEachProperties(
                new ForEachCallBack<PropertyMapping<T,?,JdbcColumnKey,FieldMapperColumnDefinition<JdbcColumnKey>>>(){
                    int i = 0;
                    @Override
                    public void handle(PropertyMapping<T, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> pm) {
                        PlaceHolder parameter =
                                new PlaceHolder(pm.getColumnKey().getName(),
                                        StatementCreatorUtils.javaTypeToSqlParameterType(TypeHelper.toClass(pm.getPropertyMeta().getPropertyType())),
                                        null, pm.getPropertyMeta().getGetter());
                        parameters[i] = parameter;
                        i++;
                    }
                });

        return new StaticSqlParameters<T>(parameters);
    }
}
