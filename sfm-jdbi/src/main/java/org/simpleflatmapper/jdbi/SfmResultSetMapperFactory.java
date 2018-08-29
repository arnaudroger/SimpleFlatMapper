package org.simpleflatmapper.jdbi;

import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.util.BiPredicate;
import org.simpleflatmapper.util.UnaryFactory;
import org.skife.jdbi.v2.ResultSetMapperFactory;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SfmResultSetMapperFactory implements ResultSetMapperFactory {

    private static final UnaryFactory<Class<?>, JdbcMapper<?>> DEFAULT_FACTORY = new UnaryFactory<Class<?>, JdbcMapper<?>>() {
        @Override
        public JdbcMapper<?> newInstance(Class<?> aClass) {
            return JdbcMapperFactory.newInstance().newMapper(aClass);
        }
    };
    private static final BiPredicate<Class<?>, StatementContext> DEFAULT_ACCEPT_PREDICATE = new BiPredicate<Class<?>, StatementContext>() {
        @Override
        public boolean test(Class<?> aClass, StatementContext statementContext) {
            return true;
        }
    };

    private final UnaryFactory<Class<?>, JdbcMapper<?>> mapperFactory;
    private final ConcurrentMap<Class<?>, ResultSetMapper<?>> cache = new ConcurrentHashMap<Class<?>, ResultSetMapper<?>>();
    private final BiPredicate<Class<?>, StatementContext> acceptsPredicate;

    public SfmResultSetMapperFactory() {
        this(DEFAULT_FACTORY);
    }

    public SfmResultSetMapperFactory(UnaryFactory<Class<?>, JdbcMapper<?>> mapperFactory) {
        this(DEFAULT_ACCEPT_PREDICATE, mapperFactory);
    }

    public SfmResultSetMapperFactory(BiPredicate<Class<?>, StatementContext> acceptsPredicate, UnaryFactory<Class<?>, JdbcMapper<?>> mapperFactory) {
        this.mapperFactory = mapperFactory;
        this.acceptsPredicate = acceptsPredicate;
    }

    @Override
    public boolean accepts(Class aClass, StatementContext statementContext) {
        return acceptsPredicate.test(aClass, statementContext);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResultSetMapper mapperFor(Class aClass, StatementContext statementContext) {
        ResultSetMapper mapper = cache.get(aClass);

        if (mapper == null) {
            JdbcMapper<?> resultSetMapper = mapperFactory.newInstance(aClass);
            mapper = toResultSetMapper(resultSetMapper);
            ResultSetMapper<?> cachedMapper = cache.putIfAbsent(aClass, mapper);
            if (cachedMapper != null) {
                mapper = cachedMapper;
            }
        }

        return mapper;
    }

    private <T> ResultSetMapper<T> toResultSetMapper(JdbcMapper<T> resultSetMapper) {
        ResultSetMapper mapper;
        if (resultSetMapper instanceof DynamicJdbcMapper) {
            mapper = new DynamicSfmResultSetMapper<T>((DynamicJdbcMapper<T>) resultSetMapper);
        } else {
            mapper = new DefaultSfmResultSetMapper<T>(resultSetMapper);
        }
        return mapper;
    }
}
