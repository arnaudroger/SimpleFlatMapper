package org.simpleflatmapper.jooq;


import org.jooq.Field;
import org.jooq.ResultQuery;
import org.jooq.Select;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.UniqueKey;
import org.jooq.exception.DataAccessException;
import org.simpleflatmapper.jdbc.JdbcTypeHelper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.mapper.MapperCache;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.map.mapper.MapperKeyComparator;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.util.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END

public final class SelectQueryMapper<T> {

    private final MapperCache<JooqFieldKey, SetRowMapper<ResultSet, ResultSet, T, SQLException>> mapperCache = new MapperCache<JooqFieldKey, SetRowMapper<ResultSet, ResultSet, T, SQLException>>(JdbcColumnKeyMapperKeyComparator.INSTANCE);
    private final ClassMeta<T> classMeta;
    private final MapperConfig<JooqFieldKey, ResultSet> mapperConfig;

    protected SelectQueryMapper(ClassMeta<T> classMeta, MapperConfig<JooqFieldKey, ResultSet> mapperConfig) {
        this.classMeta = classMeta;
        this.mapperConfig = mapperConfig;
    }

    public <SET extends TableLike & ResultQuery> List<T> asList(final SET source)
            throws MappingException {
        SetRowMapper<ResultSet, ResultSet, T, SQLException> mapper = getMapper(source);
        try {
            final ArrayList<T> list = new ArrayList<T>();

            ResultSet rs = source.fetchResultSet();
            try {
                mapper.forEach(rs, new Consumer<T>() {
                    @Override
                    public void accept(T t) {
                        list.add(t);
                    }
                });
            } finally {
                rs.close();
            }
            return list;
        } catch (MappingException e) {
            throw new org.jooq.exception.MappingException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e);

        }
    }

    /**
     * Loop over the resultSet, map each row to a new newInstance of T and call back the handler
     *<p>
     * The method will return the handler passed as an argument so you can easily chain the calls like <br>
     * <code>
     *     List&lt;T&gt; list = jdbcMapper.forEach(rs, new ListHandler&lt;T&gt;()).getList();
     * </code>
     * <br>
     *
     * @param source the source
     * @param handler the handler that will get the callback
     * @param <H> the row handler type
     * @return the handler passed in
     * @throws MappingException if an error occurs during the mapping
     *
     */
    public <SET extends TableLike & ResultQuery, H extends CheckedConsumer<? super T>> H forEach(final SET source, final H handler)
            throws MappingException {
        SetRowMapper<ResultSet, ResultSet, T, SQLException> mapper = getMapper(source);
        try {
            ResultSet rs = source.fetchResultSet();
            try {
                mapper.forEach(rs, handler);
            } finally {
                rs.close();
            }
        } catch (MappingException e) {
            throw new org.jooq.exception.MappingException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
        return handler;
    }

    /**
     *
     * @param source the source
     * @return an iterator that will return a map object for each row of the result set.
     * @throws MappingException if an error occurs during the mapping
     */
    public <SET extends TableLike & ResultQuery> AutoCloseableIterator<T> iterator(SET source)
            throws MappingException {
        SetRowMapper<ResultSet, ResultSet, T, SQLException> mapper = getMapper(source);
        try {
            final ResultSet rs = source.fetchResultSet();
            final Iterator<T> iterator = new ExceptionTranslatorIterator<T>(mapper.iterator(rs));
            return new AutoCloseableIterator<T>(iterator, closer(rs));
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    /**
     *
     * @param source the source
     * @return a stream that will contain a map object for each row of the result set.
     * @throws MappingException if an error occurs during the mapping
     */
    //IFJAVA8_START
    public <SET extends TableLike & ResultQuery> Stream<T> stream(SET source) throws MappingException {
        SetRowMapper<ResultSet, ResultSet, T, SQLException> mapper = getMapper(source);
        try {
            final ResultSet rs = source.fetchResultSet();

            final Enumerable<T> enumerate = new ExceptionTranslatorEnumerable<T>(mapper.enumerate(rs));
            final Stream<T> stream = StreamSupport.stream(new EnumerableSpliterator<T>(enumerate), false);

            return stream.onClose(new Runnable() {
                @Override
                public void run() {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        throw new DataAccessException(e.getMessage(), e);
                    }
                }
            });
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }
    //IFJAVA8_END

    public <SET extends TableLike & ResultQuery> AutoCloseableEnumerable<T> enumerate(SET source)
            throws MappingException {
        SetRowMapper<ResultSet, ResultSet, T, SQLException> mapper = getMapper(source);
        try {
            final ResultSet rs = source.fetchResultSet();
            final Enumerable<T> enumerable = new ExceptionTranslatorEnumerable<T>(mapper.enumerate(rs));
            return new AutoCloseableEnumerable<T>(enumerable, closer(rs));
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }


    private <SET extends TableLike & ResultQuery> SetRowMapper<ResultSet, ResultSet, T, SQLException> getMapper(SET source) {
        try {
            Field[] fields = getFields(source);

            JooqFieldKey[] keys = new JooqFieldKey[fields.length];
            for (int i = 0; i < fields.length; i++) {
                keys[i] = new JooqFieldKey(fields[i], i);
            }

            MapperKey<JooqFieldKey> mapperKey = new MapperKey<JooqFieldKey>(keys);

            SetRowMapper<ResultSet, ResultSet, T, SQLException> mapper = mapperCache.get(mapperKey);

            if (mapper == null) {
                mapper = buildMapper(fields);
                mapperCache.add(mapperKey, mapper);
            }

            return mapper;
        } catch (Exception e) {
            throw new org.jooq.exception.MappingException(e.getMessage(), e);
        }
    }

    private <SET extends TableLike & ResultQuery> Field[] getFields(SET source) {
        if (source instanceof Select) {
            List<Field<?>> select = ((Select<?>) source).getSelect();
            return select.toArray(new Field[0]);
        }
        return source.fields();
    }

    private SetRowMapper<ResultSet, ResultSet, T, SQLException> buildMapper(Field[] fields) {

        JooqJdbcMapperBuilder<T> mapperBuilder = new JooqJdbcMapperBuilder<T>(classMeta, mapperConfig);

        for(int i = 0; i < fields.length; i ++) {
            Field field = fields[i];
            Object[] properties = isKey(field) ? new Object[]{KeyProperty.DEFAULT} : new Object[0];

            int sqlType = field.getDataType().getSQLType();

            JooqFieldKey key = new JooqFieldKey(field, i + 1, JdbcTypeHelper.toJavaType(sqlType, field.getType()));

            mapperBuilder.addMapping(key, properties);
        }

        return mapperBuilder.mapper();
    }

    private boolean isKey(Field<?> field) {
        if (field instanceof TableField) {
            TableField<?, ?> tf = (TableField<?, ?>) field;
            List<? extends UniqueKey<?>> keys = tf.getTable().getKeys();
            if (keys != null) {
                for (UniqueKey key : keys) {
                    if (key.getFields().contains(field)) return true;
                }
            }
        }
        return false;
    }


    private Closer closer(final ResultSet rs) {
        return new Closer() {
            @Override
            public void close() {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage(), e);
                }
            }
        };
    }


    private static final class JdbcColumnKeyMapperKeyComparator extends MapperKeyComparator<JooqFieldKey> {

        public final static JdbcColumnKeyMapperKeyComparator INSTANCE = new JdbcColumnKeyMapperKeyComparator();

        private JdbcColumnKeyMapperKeyComparator() {
        }

        @Override
        public int compare(MapperKey<JooqFieldKey> m1, MapperKey<JooqFieldKey> m2) {
            JooqFieldKey[] keys1 = m1.getColumns();
            JooqFieldKey[] keys2 = m2.getColumns();
            int d = keys1.length - keys2.length;
            if (d != 0) {
                return d;
            }
            return compareKeys(keys1, keys2);
        }

        private int compareKeys(JooqFieldKey[] keys1, JooqFieldKey[] keys2) {
            for (int i = 0; i < keys1.length; i++) {
                int d = compare(keys1[i], keys2[i]);
                if (d != 0) {
                    return d;
                }
            }
            return 0;
        }

        protected int compare(JooqFieldKey k1, JooqFieldKey k2) {
            int d = k1.getIndex() - k2.getIndex();
            if (d != 0) return d;
            d = k1.getField().getName().compareTo(k2.getField().getName());
            return d;
        }
    }

    private static class ExceptionTranslatorIterator<T> implements Iterator<T> {
        private final Iterator<T> delegate;

        public ExceptionTranslatorIterator(Iterator<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public boolean hasNext() {
            try {
                return delegate.hasNext();
            } catch (MappingException e) {
                throw new org.jooq.exception.MappingException(e.getMessage(), e);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new DataAccessException(e.getMessage(), e);
            }
        }

        @Override
        public T next() {
            try {
                return delegate.next();
            } catch (MappingException e) {
                throw new org.jooq.exception.MappingException(e.getMessage(), e);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new DataAccessException(e.getMessage(), e);
            }
        }
    }

    private class ExceptionTranslatorEnumerable<T> implements Enumerable<T> {
        private final Enumerable<T> delegate;

        public ExceptionTranslatorEnumerable(Enumerable<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean next() {
            try {
                return delegate.next();
            } catch (MappingException e) {
                throw new org.jooq.exception.MappingException(e.getMessage(), e);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new DataAccessException(e.getMessage(), e);
            }
        }

        @Override
        public T currentValue() {
            try {
                return delegate.currentValue();
            } catch (MappingException e) {
                throw new org.jooq.exception.MappingException(e.getMessage(), e);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new DataAccessException(e.getMessage(), e);
            }
        }
    }
}
