package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.CaseInsensitiveFieldKeyNamePredicate;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.util.CheckedBiFunction;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;

public class AbstractColumnNameDiscriminatorMapperFactory<
        K extends FieldKey<K>,
        MF extends AbstractColumnNameDiscriminatorMapperFactory<K, MF, S>, S> extends AbstractMapperFactory<K, MF, S> {
    private final DiscriminatorNamedGetterFactory<S> columnNameGetterFactory;
    public AbstractColumnNameDiscriminatorMapperFactory(AbstractMapperFactory<K, ?, S> config, DiscriminatorNamedGetterFactory<S> columnNameGetterFactory) {
        super(config);
        this.columnNameGetterFactory = columnNameGetterFactory;
    }

    public AbstractColumnNameDiscriminatorMapperFactory(AbstractColumnDefinitionProvider<K> columnDefinitions, ColumnDefinition<K, ?> identity, DiscriminatorNamedGetterFactory<S> columnNameGetterFactory, ContextualGetterFactory<S, K> getterFactory) {
        super(columnDefinitions, identity, getterFactory);
        this.columnNameGetterFactory = columnNameGetterFactory;
    }
    public <T> MF discriminator(Class<T> commonType, String discriminatorColumn, Consumer<DiscriminatorConditionBuilder<S, K, Object, T>> consumer) {
        return discriminator((Type)commonType, discriminatorColumn, consumer);
    }
    
    public <T> MF discriminator(Type commonType, String discriminatorColumn, Consumer<DiscriminatorConditionBuilder<S, K, Object, T>> consumer) {
        return discriminator(commonType, discriminatorColumn, Object.class, consumer);
    }
    
    public <T, V> MF discriminator(Class<T> commonType, String discriminatorColumn, Class<V> discriminatorType, Consumer<DiscriminatorConditionBuilder<S, K, V, T>> consumer) {
        return discriminator((Type)commonType, discriminatorColumn, discriminatorType, consumer);
    }
    
    public <T, V> MF discriminator(Type commonType, String discriminatorColumn, final Class<V> discriminatorType, Consumer<DiscriminatorConditionBuilder<S, K, V, T>> consumer) {
        return discriminator(commonType, CaseInsensitiveFieldKeyNamePredicate.of(discriminatorColumn), discriminatorType, consumer);
    }


    public <T> MF discriminator(Class<T> commonType, Predicate<? super K> discriminatorColumnPredicate, Consumer<DiscriminatorConditionBuilder<S, K, Object, T>> consumer) {
        return discriminator((Type)commonType, discriminatorColumnPredicate, consumer);
    }

    public <T> MF discriminator(Type commonType, Predicate<? super K> discriminatorColumnPredicate, Consumer<DiscriminatorConditionBuilder<S, K, Object, T>> consumer) {
        return discriminator(commonType, discriminatorColumnPredicate, Object.class, consumer);
    }

    public <T, V> MF discriminator(Class<T> commonType, Predicate<? super K> discriminatorColumnPredicate, Class<V> discriminatorType, Consumer<DiscriminatorConditionBuilder<S, K, V, T>> consumer) {
        return discriminator((Type)commonType, discriminatorColumnPredicate, discriminatorType, consumer);
    }

    public <T, V> MF discriminator(Type commonType, Predicate<? super K> discriminatorColumnPredicate, final Class<V> discriminatorType, Consumer<DiscriminatorConditionBuilder<S, K, V, T>> consumer) {
        final DiscriminatorNamedGetter<S, V> getter = columnNameGetterFactory.newGetter(discriminatorType);
        return discriminator(commonType, discriminatorColumnPredicate, new CheckedBiFunction<S, String, V>() {
            @Override
            public V apply(S s, String discriminatorColumn) throws Exception {

                return getter.get(s, discriminatorColumn);
            }
        }, consumer);
    }


    public interface DiscriminatorNamedGetterFactory<S> {
        <T> DiscriminatorNamedGetter<S, T> newGetter(Class<T> type);
    }

    public interface DiscriminatorNamedGetter<S, T> {
        T get(S s, String discriminatorColumn) throws Exception;
    }
    
}
