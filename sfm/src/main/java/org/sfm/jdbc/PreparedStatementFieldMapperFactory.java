package org.sfm.jdbc;


import org.sfm.jdbc.impl.setter.*;
import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperToSourceFactory;
import org.sfm.map.context.MappingContextFactoryBuilder;
import org.sfm.map.impl.fieldmapper.*;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.primitive.*;

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

        factoryPerClass.put(char.class,
                new FieldMapperToSourceFactory<PreparedStatement, JdbcColumnKey>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <T, P> FieldMapper<T, PreparedStatement> newFieldMapperToSource(PropertyMapping<T, P, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm, MappingContextFactoryBuilder builder) {
                        Getter<T, P> getter = pm.getPropertyMeta().getGetter();
                        CharacterPreparedStatementSetter preparedStatementSetter = new CharacterPreparedStatementSetter(pm.getColumnKey().getIndex());

                        if ((getter instanceof CharacterGetter)) {
                            return new CharacterFieldMapper<T, PreparedStatement>((CharacterGetter<T>) getter, preparedStatementSetter);
                        } else {
                            return new FieldMapperImpl<T, PreparedStatement, Character>((Getter<? super T, ? extends Character>) getter, preparedStatementSetter);
                        }
                    }
                });
        factoryPerClass.put(Character.class, factoryPerClass.get(char.class));

        factoryPerClass.put(short.class,
            new FieldMapperToSourceFactory<PreparedStatement, JdbcColumnKey>() {
                @SuppressWarnings("unchecked")
                @Override
                public <T, P> FieldMapper<T, PreparedStatement> newFieldMapperToSource(PropertyMapping<T, P, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm, MappingContextFactoryBuilder builder) {
                    Getter<T, P> getter = pm.getPropertyMeta().getGetter();
                    ShortPreparedStatementSetter preparedStatementSetter = new ShortPreparedStatementSetter(pm.getColumnKey().getIndex());

                    if ((getter instanceof ShortGetter)) {
                        return new ShortFieldMapper<T, PreparedStatement>((ShortGetter<T>) getter, preparedStatementSetter);
                    } else {
                        return new FieldMapperImpl<T, PreparedStatement, Short>((Getter<? super T, ? extends Short>) getter, preparedStatementSetter);
                    }
                }
            });
        factoryPerClass.put(Short.class, factoryPerClass.get(short.class));

        factoryPerClass.put(int.class,
                new FieldMapperToSourceFactory<PreparedStatement, JdbcColumnKey>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <T, P> FieldMapper<T, PreparedStatement> newFieldMapperToSource(PropertyMapping<T, P, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm, MappingContextFactoryBuilder builder) {
                        Getter<T, P> getter = pm.getPropertyMeta().getGetter();
                        IntegerPreparedStatementSetter preparedStatementSetter = new IntegerPreparedStatementSetter(pm.getColumnKey().getIndex());

                        if ((getter instanceof IntGetter)) {
                            return new IntFieldMapper<T, PreparedStatement>((IntGetter<T>) getter, preparedStatementSetter);
                        } else {
                            return new FieldMapperImpl<T, PreparedStatement, Integer>((Getter<? super T, ? extends Integer>) getter, preparedStatementSetter);
                        }
                    }
                });
        factoryPerClass.put(Integer.class, factoryPerClass.get(int.class));

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
