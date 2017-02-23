package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.named.NamedParameter;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.PropertyWithGetter;
import org.simpleflatmapper.map.property.ConstantValueProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.mapper.PropertyMappingsBuilder;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.ScoredGetter;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ObjectPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.TypeHelper;
import org.springframework.jdbc.core.StatementCreatorUtils;

public final class SqlParameterSourceBuilder<T> {


    private final PropertyMappingsBuilder<T, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> builder;
    private final MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> mapperConfig;
    private final ReflectionService reflectionService;
    private int index = 1;


    public SqlParameterSourceBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> mapperConfig) {
        this.mapperConfig = mapperConfig;
        this.reflectionService = classMeta.getReflectionService();
        this.builder =
                PropertyMappingsBuilder.of(classMeta, mapperConfig, PropertyWithGetter.INSTANCE);
    }

    public SqlParameterSourceBuilder<T> add(String column) {
        return add(new JdbcColumnKey(column, index++), FieldMapperColumnDefinition.<JdbcColumnKey>identity());
    }

    public SqlParameterSourceBuilder<T> add(JdbcColumnKey key, FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition) {
        final FieldMapperColumnDefinition<JdbcColumnKey> composedDefinition =
                columnDefinition.compose(mapperConfig.columnDefinitions().getColumnDefinition(key));
        final JdbcColumnKey mappedColumnKey = composedDefinition.rename(key);


        if (composedDefinition.has(ConstantValueProperty.class)) {
            ConstantValueProperty staticValueProperty = composedDefinition.lookFor(ConstantValueProperty.class);
            PropertyMeta<T, Object> meta = new ObjectPropertyMeta<T, Object>(key.getName(), builder.getClassMeta().getType(), reflectionService, staticValueProperty.getType(), ScoredGetter.of(new ConstantGetter<T, Object>(staticValueProperty.getValue()), 1), null, null);
            builder.addProperty(key, columnDefinition, meta);
        } else {
            builder.addProperty(mappedColumnKey, composedDefinition);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public SqlParameterSourceFactory<T> buildFactory(String sql) {
        NamedSqlQuery namedSqlQuery = NamedSqlQuery.parse(sql);
        for(int i = 0; i < namedSqlQuery.getParametersSize(); i++) {
            NamedParameter parameter = namedSqlQuery.getParameter(i);
            add(parameter.getName());
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
                                new PlaceHolderValueGetter(pm.getColumnKey().getOrginalName(),
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
