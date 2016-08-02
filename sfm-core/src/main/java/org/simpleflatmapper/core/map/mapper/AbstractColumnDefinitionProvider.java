package org.simpleflatmapper.core.map.mapper;


import org.simpleflatmapper.core.map.FieldKey;
import org.simpleflatmapper.core.map.column.ColumnProperty;
import org.simpleflatmapper.core.tuples.Tuple2;
import org.simpleflatmapper.core.utils.BiConsumer;
import org.simpleflatmapper.core.utils.ConstantUnaryFactory;
import org.simpleflatmapper.core.utils.Predicate;
import org.simpleflatmapper.core.utils.UnaryFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractColumnDefinitionProvider<C extends ColumnDefinition<K, C>, K extends FieldKey<K>> implements ColumnDefinitionProvider<C, K> {

    protected final List<Tuple2<Predicate<? super K>, C>> definitions;
    protected final List<Tuple2<Predicate<? super K>, UnaryFactory<? super K, ColumnProperty>>> properties;

    public AbstractColumnDefinitionProvider() {
        definitions = new ArrayList<Tuple2<Predicate<? super K>, C>>();
        properties = new ArrayList<Tuple2<Predicate<? super K>, UnaryFactory<? super K, ColumnProperty>>>();
    }
    public AbstractColumnDefinitionProvider(List<Tuple2<Predicate<? super K>, C>> definitions,
                                            List<Tuple2<Predicate<? super K>, UnaryFactory<? super K, ColumnProperty>>> properties) {
        this.definitions = definitions;
        this.properties = properties;
    }

    public void addColumnDefinition(Predicate<? super K> predicate, C definition) {
        definitions.add(new Tuple2<Predicate<? super K>, C>(predicate, definition));
    }

    public void addColumnProperty(Predicate<? super K> predicate, UnaryFactory<? super K, ColumnProperty> propertyFactory) {
        properties.add(new Tuple2<Predicate<? super K>, UnaryFactory<? super K, ColumnProperty>>(predicate, propertyFactory));
    }

    @Override
    public C getColumnDefinition(K key) {
        C definition = identity();

        for(Tuple2<Predicate<? super K>, C> def : definitions) {
            if (def.first().test(key)) {
                definition = compose(definition, def.second());
            }
        }

        for (Tuple2<Predicate<? super K>, UnaryFactory<? super K, ColumnProperty>> tuple2 : properties) {
            if (tuple2.first().test(key)) {
                ColumnProperty columnProperty = tuple2.second().newInstance(key);
                if (columnProperty != null) {
                    definition = definition.add(columnProperty);
                }
            }
        }

        return  definition;
    }

    protected abstract C compose(C definition, C second);
    protected abstract C identity();

    public List<Tuple2<Predicate<? super K>, C>> getDefinitions() {
        return definitions;
    }

    public List<Tuple2<Predicate<? super K>, UnaryFactory<? super K, ColumnProperty>>> getProperties() {
        return properties;
    }

    @Override
    public <CP extends ColumnProperty, BC extends BiConsumer<Predicate<? super K>, CP>> BC forEach(Class<CP> propertyType, BC consumer) {
        for(Tuple2<Predicate<? super K>, C> def : definitions) {
            final CP cp = def.getElement1().lookFor(propertyType);
            if (cp != null) {
                consumer.accept(def.getElement0(), cp);
            }
        }
        for (Tuple2<Predicate<? super K>, UnaryFactory<? super K, ColumnProperty>> tuple2 : properties) {
            final UnaryFactory<? super K, ColumnProperty> unaryFactory = tuple2.getElement1();
            if (unaryFactory instanceof ConstantUnaryFactory) {
                final ColumnProperty columnProperty = unaryFactory.newInstance(null);
                if (propertyType.isInstance(columnProperty)) {
                    consumer.accept(tuple2.getElement0(), propertyType.cast(columnProperty));
                }
            }
        }
        return consumer;
    }
}
