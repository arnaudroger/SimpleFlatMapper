package org.sfm.jdbc;


import org.sfm.jdbc.impl.setter.BooleanPreparedStatementSetter;
import org.sfm.jdbc.impl.setter.BytePreparedStatementSetter;
import org.sfm.jdbc.impl.setter.LongPreparedStatementSetter;
import org.sfm.jdbc.impl.setter.StringPreparedStatementSetter;
import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperToSourceFactory;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.impl.fieldmapper.BooleanFieldMapper;
import org.sfm.map.impl.fieldmapper.ByteFieldMapper;
import org.sfm.map.impl.fieldmapper.FieldMapperImpl;
import org.sfm.map.impl.fieldmapper.LongFieldMapper;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.primitive.BooleanGetter;
import org.sfm.reflect.primitive.ByteGetter;
import org.sfm.reflect.primitive.LongGetter;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class PreparedStatementFieldMapperFactory implements FieldMapperToSourceFactory<PreparedStatement, JdbcColumnKey> {

    private static final PreparedStatementFieldMapperFactory INSTANCE = new PreparedStatementFieldMapperFactory();

    private final Map<Class<?>, FieldMapperToSourceFactory<PreparedStatement, JdbcColumnKey>> factoryPerClass =
            new HashMap<Class<?>, FieldMapperToSourceFactory<PreparedStatement, JdbcColumnKey>>();

    {
        factoryPerClass.put(boolean.class,
                new FieldMapperToSourceFactory<PreparedStatement, JdbcColumnKey>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <T, P> FieldMapper<T, PreparedStatement> newFieldMapperToSource(PropertyMapping<T, P, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm, MappingContextFactoryBuilder builder) {
                        Getter<T, P> getter = pm.getPropertyMeta().getGetter();
                        BooleanPreparedStatementSetter preparedStatementSetter = new BooleanPreparedStatementSetter(pm.getColumnKey().getIndex());

                        if ((getter instanceof BooleanGetter)) {
                            return new BooleanFieldMapper<T, PreparedStatement>((BooleanGetter<T>) getter, preparedStatementSetter);
                        } else {
                            return new FieldMapperImpl<T, PreparedStatement, Boolean>((Getter<? super T, ? extends Boolean>) getter, preparedStatementSetter);
                        }
                    }
                });
        factoryPerClass.put(Boolean.class, factoryPerClass.get(boolean.class));

        factoryPerClass.put(byte.class,
                new FieldMapperToSourceFactory<PreparedStatement, JdbcColumnKey>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <T, P> FieldMapper<T, PreparedStatement> newFieldMapperToSource(PropertyMapping<T, P, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm, MappingContextFactoryBuilder builder) {
                        Getter<T, P> getter = pm.getPropertyMeta().getGetter();
                        BytePreparedStatementSetter preparedStatementSetter = new BytePreparedStatementSetter(pm.getColumnKey().getIndex());

                        if ((getter instanceof ByteGetter)) {
                            return new ByteFieldMapper<T, PreparedStatement>((ByteGetter<T>) getter, preparedStatementSetter);
                        } else {
                            return new FieldMapperImpl<T, PreparedStatement, Byte>((Getter<? super T, ? extends Byte>) getter, preparedStatementSetter);
                        }
                    }
                });
        factoryPerClass.put(Byte.class, factoryPerClass.get(byte.class));

        factoryPerClass.put(long.class,
                new FieldMapperToSourceFactory<PreparedStatement, JdbcColumnKey>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <T, P> FieldMapper<T, PreparedStatement> newFieldMapperToSource(PropertyMapping<T, P, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm, MappingContextFactoryBuilder builder) {
                        Getter<T, P> getter = pm.getPropertyMeta().getGetter();
                        LongPreparedStatementSetter preparedStatementSetter = new LongPreparedStatementSetter(pm.getColumnKey().getIndex());

                        if ((getter instanceof LongGetter)) {
                            return new LongFieldMapper<T, PreparedStatement>((LongGetter<T>) getter, preparedStatementSetter);
                        } else {
                            return new FieldMapperImpl<T, PreparedStatement, Long>((Getter<? super T, ? extends Long>) getter, preparedStatementSetter);
                        }
                    }
                });
        factoryPerClass.put(Long.class, factoryPerClass.get(long.class));

        factoryPerClass.put(String.class,
            new FieldMapperToSourceFactory<PreparedStatement, JdbcColumnKey>() {
                @Override
                public <T, P> FieldMapper<T, PreparedStatement> newFieldMapperToSource(PropertyMapping<T, P, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm, MappingContextFactoryBuilder builder) {
                    return new FieldMapperImpl<T, PreparedStatement, String>((Getter<? super T, ? extends String>) pm.getPropertyMeta().getGetter(),
                            new StringPreparedStatementSetter(pm.getColumnKey().getIndex()));
                }
            });
    }

    @SuppressWarnings("unchecked")
    public <T, P> FieldMapper<T, PreparedStatement> newFieldMapperToSource(
            PropertyMapping<T, P, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm,
            MappingContextFactoryBuilder builder) {

        Type propertyType = pm.getPropertyMeta().getPropertyType();

        FieldMapperToSourceFactory<PreparedStatement, JdbcColumnKey> fieldMapperToSourceFactory = factoryPerClass.get(TypeHelper.toClass(propertyType));

        if (fieldMapperToSourceFactory != null) {
            return fieldMapperToSourceFactory.newFieldMapperToSource(pm, builder);
        }

        return null;
    }

    public static PreparedStatementFieldMapperFactory instance() {
        return INSTANCE;
    }
}
