package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.util.Consumer;

import java.lang.reflect.Type;

public class AbstractColumnNameDiscriminatorMapperFactory<
        K extends FieldKey<K>,
        MF extends AbstractColumnNameDiscriminatorMapperFactory<K, MF, S>, S> extends AbstractMapperFactory<K, MF, S> {


    public AbstractColumnNameDiscriminatorMapperFactory(AbstractMapperFactory<K, ?, S> config) {
        super(config);
    }

    public AbstractColumnNameDiscriminatorMapperFactory(AbstractColumnDefinitionProvider<K> columnDefinitions, ColumnDefinition<K, ?> identity, ContextualGetterFactory<S, K> getterFactory) {
        super(columnDefinitions, identity, getterFactory);
    }

    @Deprecated
    public AbstractColumnNameDiscriminatorMapperFactory(AbstractMapperFactory<K, ?, S> config, DiscriminatorNamedGetterFactory<S> columnNameGetterFactory) {
        super(config);
    }

    @Deprecated
    public AbstractColumnNameDiscriminatorMapperFactory(AbstractColumnDefinitionProvider<K> columnDefinitions, ColumnDefinition<K, ?> identity, DiscriminatorNamedGetterFactory<S> columnNameGetterFactory, ContextualGetterFactory<S, K> getterFactory) {
        super(columnDefinitions, identity, getterFactory);
    }

    /**
     * @deprecated use {@link AbstractMapperFactory#discriminator(Type)} dsl
     */
    @Deprecated
    public <T> MF discriminator(Class<T> commonType, String discriminatorColumn, Consumer<DiscriminatorConditionBuilder<S, K, Object, T>> consumer) {
        return discriminator((Type)commonType, discriminatorColumn, consumer);
    }

    /**
     * @deprecated use {@link AbstractMapperFactory#discriminator(Type)} dsl
     */
    public <T> MF discriminator(Type commonType, String discriminatorColumn, Consumer<DiscriminatorConditionBuilder<S, K, Object, T>> consumer) {
        return discriminator(commonType, discriminatorColumn, Object.class, consumer);
    }

    /**
     * @deprecated use {@link AbstractMapperFactory#discriminator(Type)} dsl
     */
    public <T, KT> MF discriminator(Class<T> commonType, String discriminatorColumn, Class<KT> discriminatorType, Consumer<DiscriminatorConditionBuilder<S, K, KT, T>> consumer) {
        return discriminator((Type)commonType, discriminatorColumn, discriminatorType, consumer);
    }

    /**
     * @deprecated use {@link AbstractMapperFactory#discriminator(Type)} dsl
     */
    public <T, KT> MF discriminator(Type commonType, String discriminatorColumn, final Class<KT> discriminatorType, Consumer<DiscriminatorConditionBuilder<S, K, KT, T>> consumer) {
        return this.<T>discriminator(commonType).onColumn(discriminatorColumn, discriminatorType).with(consumer);
    }


    public interface DiscriminatorNamedGetterFactory<S> {
        <T> DiscriminatorNamedGetter<S, T> newGetter(Class<T> type);
    }

    public interface DiscriminatorNamedGetter<S, T> {
        T get(S s, String discriminatorColumn) throws Exception;
    }
    
}
