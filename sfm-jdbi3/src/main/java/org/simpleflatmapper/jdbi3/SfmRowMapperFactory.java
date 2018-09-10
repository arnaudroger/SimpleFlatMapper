package org.simpleflatmapper.jdbi3;

import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.mapper.RowMapperFactory;
import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.ContextualSourceMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.util.BiPredicate;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SfmRowMapperFactory implements RowMapperFactory {

    private static final UnaryFactory<Type, ContextualSourceMapper<ResultSet, ?>> DEFAULT_FACTORY = new UnaryFactory<Type, ContextualSourceMapper<ResultSet, ?>>() {
        @Override
        public ContextualSourceMapper<ResultSet, ?> newInstance(Type type) {
            return JdbcMapperFactory.newInstance().newMapper(type);
        }
    };
    private static final BiPredicate<Type, ConfigRegistry> DEFAULT_ACCEPT_PREDICATE = new BiPredicate<Type, ConfigRegistry>() {
        @Override
        public boolean test(Type type, ConfigRegistry configRegistry) {
            return true;
        }
    };

    private final UnaryFactory<Type, ContextualSourceMapper<ResultSet, ?>> mapperFactory;
    private final ConcurrentMap<Type, RowMapper<?>> cache = new ConcurrentHashMap<Type, RowMapper<?>>();
    private final BiPredicate<Type, ConfigRegistry> acceptsPredicate;

    public SfmRowMapperFactory() {
        this(DEFAULT_FACTORY);
    }

    public SfmRowMapperFactory(UnaryFactory<Type, ContextualSourceMapper<ResultSet, ?>> mapperFactory) {
        this(DEFAULT_ACCEPT_PREDICATE, mapperFactory);
    }

    public SfmRowMapperFactory(BiPredicate<Type, ConfigRegistry> acceptsPredicate, UnaryFactory<Type, ContextualSourceMapper<ResultSet, ?>> mapperFactory) {
        this.mapperFactory = mapperFactory;
        this.acceptsPredicate = acceptsPredicate;
    }

    private <T> RowMapper<T> toRowMapper(ContextualSourceMapper<ResultSet, T> resultSetMapper) {
        RowMapper<T> mapper;
        if (resultSetMapper instanceof DynamicJdbcMapper) {
            mapper = new DynamicRowMapper<T>((DynamicJdbcMapper<T>) resultSetMapper);
        } else {
            mapper = new StaticRowMapper<T>(resultSetMapper);
        }
        return mapper;
    }

    @Override
    public Optional<RowMapper<?>> build(Type type, ConfigRegistry configRegistry) {
        if (acceptsPredicate.test(type, configRegistry)) {
            RowMapper<?> rowMapper = cache.get(type);
            if (rowMapper == null) {
                ContextualSourceMapper<ResultSet, ?> resultSetMapper = mapperFactory.newInstance(type);
                rowMapper = toRowMapper(resultSetMapper);
                RowMapper<?> cachedMapper = cache.putIfAbsent(type, rowMapper);
                if (cachedMapper != null) {
                    rowMapper = cachedMapper;
                }
            }
            return Optional.of(rowMapper);
        }

        return Optional.empty();
    }
}
