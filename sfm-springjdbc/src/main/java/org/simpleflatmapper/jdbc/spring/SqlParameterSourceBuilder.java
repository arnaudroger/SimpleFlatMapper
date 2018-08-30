package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.converter.DefaultContextFactoryBuilder;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.SqlTypeColumnProperty;
import org.simpleflatmapper.jdbc.JdbcTypeHelper;
import org.simpleflatmapper.jdbc.named.NamedParameter;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.PropertyWithGetter;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.property.ConstantValueProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.mapper.PropertyMappingsBuilder;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.ScoredGetter;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.map.fieldmapper.FieldMapperGetterWithConverter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ObjectPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.TypeHelper;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;

import java.lang.reflect.Type;
import java.sql.Types;

//IFJAVA8_START
import java.time.*;
//IFJAVA8_END

public final class SqlParameterSourceBuilder<T> {


    private final PropertyMappingsBuilder<T, JdbcColumnKey> builder;
    private final MapperConfig<JdbcColumnKey> mapperConfig;
    private final ReflectionService reflectionService;
    private int index = 1;


    public SqlParameterSourceBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<JdbcColumnKey> mapperConfig) {
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
                new ForEachCallBack<PropertyMapping<T,?,JdbcColumnKey>>(){
                    int i = 0;
                    @Override
                    public void handle(PropertyMapping<T, ?, JdbcColumnKey> pm) {
                        int parameterType =
                                getParameterType(pm);
                        ContextualGetter<? super T, ?> getter = ContextualGetterAdapter.of(pm.getPropertyMeta().getGetter());


                        // need conversion ?
                        final DefaultContextFactoryBuilder contextFactoryBuilder = new DefaultContextFactoryBuilder();
                        Type propertyType = pm.getPropertyMeta().getPropertyType();
                        Class<?> sqlType = JdbcTypeHelper.toJavaType(parameterType, propertyType);
                        if (!TypeHelper.isAssignable(sqlType, propertyType)) {
                            Converter<? super Object, ?> converter = ConverterService.getInstance().findConverter(propertyType, sqlType, contextFactoryBuilder);
                            
                            if (converter != null) {
                                getter = new FieldMapperGetterWithConverter(converter, getter);
                            }
                        }
                        
                        PlaceHolderValueGetter parameter =
                                new PlaceHolderValueGetter(pm.getColumnKey().getOrginalName(),
                                        parameterType,
                                        null, getter, contextFactoryBuilder.build());
                        parameters[i] = parameter;
                        i++;
                    }
                });

        return parameters.length > 10
                ? new ArrayPlaceHolderValueGetterSource<T>(parameters)
                : new MapPlaceHolderValueGetterSource<T>(parameters)
                ;
    }

    private static int getParameterType(PropertyMapping<?, ?, JdbcColumnKey> pm) {
        Class<?> propertyType = TypeHelper.toClass(pm.getPropertyMeta().getPropertyType());
        
        if (pm.getColumnDefinition().has(SqlTypeColumnProperty.class)) {
            return pm.getColumnDefinition().lookFor(SqlTypeColumnProperty.class).getSqlType();
        }

        int t =  StatementCreatorUtils.javaTypeToSqlParameterType(propertyType);
        if (t == SqlTypeValue.TYPE_UNKNOWN) {
            //IFJAVA8_START
            if (propertyType.equals(ZonedDateTime.class) || propertyType.equals(OffsetDateTime.class)) {
                return Types.TIMESTAMP_WITH_TIMEZONE;
            }

            if (propertyType.equals(Instant.class) || propertyType.equals(LocalDateTime.class)) {
                return Types.TIMESTAMP;
            }

            if (propertyType.equals(LocalDate.class)) {
                return Types.DATE;
            }

            if (propertyType.equals(LocalTime.class)) {
                return Types.TIME;
            }

            //IFJAVA8_END
            return JdbcColumnKey.UNDEFINED_TYPE;
        }
        return t;
    }

    public SqlParameterSourceFactory<T> buildFactory() {
        return new SqlParameterSourceFactory<T>(buildSource());
    }
}
