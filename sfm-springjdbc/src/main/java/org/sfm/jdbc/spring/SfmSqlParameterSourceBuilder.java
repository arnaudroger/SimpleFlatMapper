package org.sfm.jdbc.spring;

import org.sfm.map.FieldKey;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.error.RethrowMapperBuilderErrorHandler;
import org.sfm.map.mapper.DefaultPropertyNameMatcherFactory;
import org.sfm.map.mapper.PropertyMappingsBuilder;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.PropertyMeta;
import org.springframework.jdbc.core.namedparam.AbstractSqlParameterSource;
import org.springframework.jdbc.core.namedparam.ExposedParsedSql;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import static java.util.Objects.requireNonNull;

public class SfmSqlParameterSourceBuilder<T> {

    private static final ConcurrentMap<CacheKey<?>, SfmSqlParameterSourceBuilder<?>> CLASS_MAPPER_CACHE = new ConcurrentHashMap<>();

    public static <T> SfmSqlParameterSourceBuilder<T> newParameterSourceBuilder(final Class<T> targetClass, final String sql) {
        return builder(requireNonNull(targetClass, "targetClass can not be null"),
                       requireNonNull(sql, "sql can not be null"));
    }

    @SuppressWarnings("unchecked")
    private static <T> SfmSqlParameterSourceBuilder<T> builder(Class<T> targetClass, String sql) {
        // TODO intern sqlzz?
        return (SfmSqlParameterSourceBuilder<T>) CLASS_MAPPER_CACHE.computeIfAbsent(new CacheKey<>(targetClass, sql),
                                                                                    seedClass -> new SfmSqlParameterSourceBuilder<>(
                                                                                            getPropertyMappingsBuilder(targetClass),
                                                                                            sql));
    }

    private static <T> PropertyMappingsBuilder<T, ObjectFieldKey, FieldMapperColumnDefinition<ObjectFieldKey>> getPropertyMappingsBuilder(Class<T> clazz) {
        // TODO ReflectionService - is this thread-safe??
        return new PropertyMappingsBuilder<>(ReflectionService.newInstance().getClassMeta(clazz),
                                             new DefaultPropertyNameMatcherFactory(false, false),
                                             new RethrowMapperBuilderErrorHandler(), propertyMeta -> false /* TODO <- don't know what to put here */);
    }

    private final PropertyMappingsBuilder<T, ObjectFieldKey, FieldMapperColumnDefinition<ObjectFieldKey>> propertyMappingsBuilder;

    private SfmSqlParameterSourceBuilder(final PropertyMappingsBuilder<T, ObjectFieldKey, FieldMapperColumnDefinition<ObjectFieldKey>> propertyMappingsBuilder, String sql) {
        this.propertyMappingsBuilder = propertyMappingsBuilder;
        final ParsedSql parsedSql1 = NamedParameterUtils.parseSqlStatement(sql);
        final ExposedParsedSql parsedSql = ExposedParsedSql.expose(parsedSql1);
        final List<String> paramNames = parsedSql.getParameterNames();
        for (int i = 0; i < paramNames.size(); i++) {
            String paramName = paramNames.get(i);
            // TODO can we use this propertyMeta for nested objects?
            final PropertyMeta<T, Object> propertyMeta = propertyMappingsBuilder.addProperty(
                    new ObjectFieldKey(paramName, i),
                    FieldMapperColumnDefinition.identity());
        }
    }

    public <S extends T> SqlParameterSource with(final S target) {
        return new SfmSqlParameterSource<>(target);
    }

    public <S extends T> SqlParameterSource[] with(final Collection<S> target) {
        return target.stream().map(e -> new SfmSqlParameterSource<>(e)).toArray(SqlParameterSource[]::new);
    }

    private class SfmSqlParameterSource<S extends T> extends AbstractSqlParameterSource {

        private final S target;

        public SfmSqlParameterSource(S target) {
            this.target = target;
        }

        @Override
        public boolean hasValue(String paramName) {
            return propertyMappingsBuilder.getKeys().stream()
                                          .map(ObjectFieldKey::getName)
                                          .collect(Collectors.toList())
                                          .contains(paramName);
        }

        @Override
        public Object getValue(String paramName) throws IllegalArgumentException {
            final int index = propertyMappingsBuilder.getKeys().stream()
                                                     .map(ObjectFieldKey::getName)
                                                     .collect(Collectors.toList())
                                                     .indexOf(paramName);
            try {
                return propertyMappingsBuilder.get(index).getPropertyMeta().getGetter().get(target);
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable to retrieve value for paramName '" + paramName + "'");
            }
        }
    }

    private static final class CacheKey<T> {

        private final Class<T> targetClass;
        private final String sql;

        private CacheKey(Class<T> targetClass, String sql) {
            this.targetClass = requireNonNull(targetClass, "targetClass can not be null");
            this.sql = requireNonNull(sql, "sql can not be null");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            CacheKey<?> cacheKey = (CacheKey<?>) o;
            return Objects.equals(targetClass, cacheKey.targetClass) && Objects.equals(sql, cacheKey.sql);
        }

        @Override
        public int hashCode() {
            return Objects.hash(targetClass, sql);
        }
    }

    private static final class ObjectFieldKey implements FieldKey<ObjectFieldKey> {

        private final String name;
        private final int index;
        private final ObjectFieldKey parent;

        public ObjectFieldKey(String name, int index) {
            this.name = name;
            this.index = index;
            this.parent = null;
        }

        private ObjectFieldKey(String name, int index, ObjectFieldKey parent) {
            this.name = name;
            this.index = index;
            this.parent = parent;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getIndex() {
            return index;
        }

        public ObjectFieldKey getParent() {
            return parent;
        }

        @Override
        public ObjectFieldKey alias(String alias) {
            return new ObjectFieldKey(alias, index, this);
        }
    }
}
