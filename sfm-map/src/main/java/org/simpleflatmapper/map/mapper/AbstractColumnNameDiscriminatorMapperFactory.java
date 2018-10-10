package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.property.OptionalProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.Consumer;

import java.lang.reflect.Type;
import java.util.function.Function;

public class AbstractColumnNameDiscriminatorMapperFactory<
        K extends FieldKey<K>,
        MF extends AbstractColumnNameDiscriminatorMapperFactory<K, MF, S>, S> extends AbstractMapperFactory<K, MF, S> {
    private final ColumnNameGetterFactory<S> columnNameGetterFactory;
    public AbstractColumnNameDiscriminatorMapperFactory(AbstractMapperFactory<K, ?, S> config, ColumnNameGetterFactory<S> columnNameGetterFactory) {
        super(config);
        this.columnNameGetterFactory = columnNameGetterFactory;
    }

    public AbstractColumnNameDiscriminatorMapperFactory(AbstractColumnDefinitionProvider<K> columnDefinitions, ColumnDefinition<K, ?> identity, ColumnNameGetterFactory<S> columnNameGetterFactory) {
        super(columnDefinitions, identity);
        this.columnNameGetterFactory = columnNameGetterFactory;
    }
    public <T> MF discriminator(Class<T> commonType, String discriminatorColumn, Consumer<DiscriminatorConditionBuilder<S, Object, T>> consumer) {
        return discriminator((Type)commonType, discriminatorColumn, consumer);
    }
    
    public <T> MF discriminator(Type commonType, String discriminatorColumn, Consumer<DiscriminatorConditionBuilder<S, Object, T>> consumer) {
        return discriminator(commonType, discriminatorColumn, Object.class, consumer);
    }
    
    public <T, V> MF discriminator(Class<T> commonType, String discriminatorColumn, Class<V> discriminatorType, Consumer<DiscriminatorConditionBuilder<S, V, T>> consumer) {
        return discriminator((Type)commonType, discriminatorColumn, discriminatorType, consumer);
    }
    
    public <T, V> MF discriminator(Type commonType, String discriminatorColumn, Class<V> discriminatorType, Consumer<DiscriminatorConditionBuilder<S, V, T>> consumer) {
        addColumnProperty(discriminatorColumn, OptionalProperty.INSTANCE);
        Getter<? super S, ? extends V> getter = columnNameGetterFactory.getGetter(discriminatorColumn, discriminatorType);
        return discriminator(commonType, getter, consumer);
    }

    public interface ColumnNameGetterFactory<S> {
        <T> Getter<? super S, ? extends T> getGetter(String discriminatorColumn, Class<T> discriminatorType);
    }
    
}
