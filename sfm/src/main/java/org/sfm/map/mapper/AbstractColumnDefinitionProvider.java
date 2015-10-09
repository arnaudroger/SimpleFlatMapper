package org.sfm.map.mapper;


import org.sfm.map.FieldKey;
import org.sfm.map.column.ColumnProperty;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.Predicate;
import org.sfm.utils.Supplier;
import org.sfm.utils.UnaryFactory;

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
}
