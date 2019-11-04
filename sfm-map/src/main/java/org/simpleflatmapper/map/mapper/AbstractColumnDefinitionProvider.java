package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.map.CaseInsensitiveFieldKeyNamePredicate;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.map.property.SetterProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.ConstantUnaryFactory;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.UnaryFactory;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractColumnDefinitionProvider<K extends FieldKey<K>> implements ColumnDefinitionProvider<K> {

    protected final List<PredicatedColumnPropertyFactory<K>> properties;

    public AbstractColumnDefinitionProvider() {
        this(new ArrayList<PredicatedColumnPropertyFactory<K>>());
    }
    public AbstractColumnDefinitionProvider(List<PredicatedColumnPropertyFactory<K>> properties) {
        this.properties = properties;
    }

    public void addColumnDefinition(Predicate<? super K> predicate, ColumnDefinition<K, ?> definition) {
        if (predicate == null) throw new NullPointerException();
        for(Object prop : definition.properties()) {
            addColumnProperty(predicate, newFactory(prop));
        }
    }
    public void addColumnDefinition(String name, ColumnDefinition<K, ?> definition) {
        Predicate<? super K> predicate = newPredicate(name);
        for(Object prop : definition.properties()) {
            addColumnProperty(predicate, newFactory(prop));
        }
    }

    public void addColumnProperty(String name, Object property) {
        addColumnProperty(newPredicate(name), newFactory(property));
    }

    public void addColumnProperty(Predicate<? super K> predicate, Object property) {
        if (predicate == null) throw new NullPointerException();
        addColumnProperty(predicate, newFactory(property));
    }

    public void addColumnProperty(Predicate<? super K> predicate, UnaryFactory<? super K, Object> propertyFactory) {
        properties.add(new PredicatedColumnPropertyFactory<K>(predicate, propertyFactory));
    }


    private CaseInsensitiveFieldKeyNamePredicate newPredicate(String name) {
        return CaseInsensitiveFieldKeyNamePredicate.of(name);
    }

    private UnaryFactory<? super K, Object> newFactory(Object prop) {
        return ConstantUnaryFactory.of(upgrade(prop));
    }

    private Object upgrade(Object property) {
        if (property instanceof Setter) {
            return new SetterProperty((Setter<?, ?>) property);
        }
        if (property instanceof Getter) {
            return new GetterProperty((Getter<?, ?>) property);
        }
        return property;
    }




    @Override
    public ColumnDefinition<K, ?> getColumnDefinition(K key) {
        ColumnDefinition<K, ?> definition = identity();

        for(int i = properties.size() - 1; i >= 0; i--) {
            PredicatedColumnPropertyFactory<K> tuple2 = properties.get(i);
            if (tuple2.predicate.test(key)) {
                Object columnProperty = tuple2.columnPropertyFactory.newInstance(key);
                if (columnProperty != null) {
                    definition = definition.add(columnProperty);
                }
            }
        }

        return  definition;
    }

    public abstract AbstractColumnDefinitionProvider<K> copy();

    protected abstract ColumnDefinition<K, ?> identity();

    public List<PredicatedColumnPropertyFactory<K>> getProperties() {
        return properties;
    }

    @Override
    public <CP, BC extends BiConsumer<Predicate<? super K>, CP>> BC forEach(Class<CP> propertyType, BC consumer) {
        for (PredicatedColumnPropertyFactory<K> tuple2 : properties) {
            final UnaryFactory<? super K, Object> unaryFactory = tuple2.columnPropertyFactory;
            if (unaryFactory instanceof ConstantUnaryFactory) {
                final Object columnProperty = unaryFactory.newInstance(null);
                if (propertyType.isInstance(columnProperty)) {
                    consumer.accept(tuple2.predicate, propertyType.cast(columnProperty));
                }
            }
        }
        return consumer;
    }

    public void addColumnProperty(PredicatedColumnPropertyFactory predicatedColumnPropertyFactory) {
        properties.add(predicatedColumnPropertyFactory);
    }

    public static class PredicatedColumnPropertyFactory<K extends FieldKey<K>> {
        private final Predicate<? super K> predicate;
        private final UnaryFactory<? super K, Object> columnPropertyFactory;

        public PredicatedColumnPropertyFactory(Predicate<? super K> predicate, UnaryFactory<? super K, Object> columnPropertyFactory) {
            if (predicate == null) throw new NullPointerException();
            this.predicate = predicate;
            this.columnPropertyFactory = columnPropertyFactory;
        }

        public Predicate<? super K> getPredicate() {
            return predicate;
        }

        public UnaryFactory<? super K, Object> getColumnPropertyFactory() {
            return columnPropertyFactory;
        }
    }

}
