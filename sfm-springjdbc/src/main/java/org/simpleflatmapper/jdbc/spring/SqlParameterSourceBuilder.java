package org.simpleflatmapper.jdbc.spring;

import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.converter.DefaultContextFactoryBuilder;
import org.simpleflatmapper.converter.EmptyContextFactory;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.SqlTypeColumnProperty;
import org.simpleflatmapper.jdbc.JdbcTypeHelper;
import org.simpleflatmapper.jdbc.named.NamedParameter;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.PropertyWithGetter;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.map.getter.ContextualGetterAdapter;
import org.simpleflatmapper.map.getter.NullContextualGetter;
import org.simpleflatmapper.map.property.ConstantValueProperty;
import org.simpleflatmapper.map.property.ConverterProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.mapper.PropertyMappingsBuilder;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.ScoredGetter;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.map.fieldmapper.FieldMapperGetterWithConverter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.ObjectPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;

import java.lang.reflect.Type;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

//IFJAVA8_START
import java.time.*;
//IFJAVA8_END

public final class SqlParameterSourceBuilder<T> {


    private final PropertyMappingsBuilder<T, JdbcColumnKey> builder;
    private final MapperConfig<JdbcColumnKey, ?> mapperConfig;
    private final ReflectionService reflectionService;
    private int index = 1;
    
    private final List<PlaceHolderValueGetter> parameters = new ArrayList<PlaceHolderValueGetter>();


    public SqlParameterSourceBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<JdbcColumnKey, ?> mapperConfig) {
        this.mapperConfig = mapperConfig;
        this.reflectionService = classMeta.getReflectionService();
        this.builder =
                PropertyMappingsBuilder.of(classMeta, mapperConfig, new PropertyMappingsBuilder.PropertyPredicateFactory<JdbcColumnKey>() {
                    @Override
                    public PropertyFinder.PropertyFilter predicate(JdbcColumnKey jdbcColumnKey, Object[] objects, List<PropertyMappingsBuilder.AccessorNotFound> accessorNotFounds) {
                        return new PropertyFinder.PropertyFilter(PropertyWithGetter.INSTANCE);
                    }
                });
    }

    public SqlParameterSourceBuilder<T> add(String column) {
        return add(new JdbcColumnKey(column, index++), FieldMapperColumnDefinition.<JdbcColumnKey>identity());
    }

    public SqlParameterSourceBuilder<T> add(JdbcColumnKey key, FieldMapperColumnDefinition<JdbcColumnKey> columnDefinition) {
        final FieldMapperColumnDefinition<JdbcColumnKey> composedDefinition =
                columnDefinition.compose(mapperConfig.columnDefinitions().getColumnDefinition(key));
        final JdbcColumnKey mappedColumnKey = composedDefinition.rename(key);

        PropertyMapping<T, Object, JdbcColumnKey> propertyMapping;
        if (composedDefinition.has(ConstantValueProperty.class)) {
            ConstantValueProperty staticValueProperty = composedDefinition.lookFor(ConstantValueProperty.class);
            PropertyMeta<T, Object> meta = new ObjectPropertyMeta<T, Object>(key.getName(), builder.getClassMeta().getType(), reflectionService, staticValueProperty.getType(), ScoredGetter.of(new ConstantGetter<T, Object>(staticValueProperty.getValue()), 1), null, null);
            propertyMapping = builder.addProperty(key, columnDefinition, meta);
        } else {
            propertyMapping = builder.addProperty(mappedColumnKey, composedDefinition);
        }

        parameters.add(build(key, propertyMapping));
        
        return this;
    }

    private <P> PlaceHolderValueGetter<T> build(JdbcColumnKey key, PropertyMapping<T, P, JdbcColumnKey> pm) {
        if (pm != null) {
            int parameterType =
                    getParameterType(pm);
            ContextualGetter<T, ? extends P> getter = ContextualGetterAdapter.of(pm.getPropertyMeta().getGetter());

            // need conversion ?
            final DefaultContextFactoryBuilder contextFactoryBuilder = new DefaultContextFactoryBuilder();
            Type propertyType = pm.getPropertyMeta().getPropertyType();
            Class<?> sqlType = JdbcTypeHelper.toJavaType(parameterType, propertyType);

            boolean findConverter = false;
            for(ConverterProperty cp  : pm.getColumnDefinition().lookForAll(ConverterProperty.class)) {
                if (TypeHelper.isAssignable(cp.inType, propertyType)) {
                    getter = new FieldMapperGetterWithConverter(cp.function, getter);
                    findConverter = true;
                    break;
                }
            }

            if (!findConverter && !TypeHelper.isAssignable(sqlType, propertyType)) {
                ContextualConverter<? super Object, ?> converter = ConverterService.getInstance().findConverter(propertyType, sqlType, contextFactoryBuilder);

                if (converter != null) {
                    getter = new FieldMapperGetterWithConverter(converter, getter);
                }
            }

            return 
                    new PlaceHolderValueGetter<T>(pm.getColumnKey().getOrginalName(),
                            parameterType,
                            null, getter, contextFactoryBuilder.build());
        } else {
            return
                    new PlaceHolderValueGetter<T>(key.getOrginalName(),
                            key.getSqlType(null),
                            null, NullContextualGetter.<T, P>getter(), 
                            EmptyContextFactory.INSTANCE);
        }
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
        final PlaceHolderValueGetter<T>[] parameters = this.parameters.toArray(new PlaceHolderValueGetter[0]);
        
        return parameters.length < 10
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
