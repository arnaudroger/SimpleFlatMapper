package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.ConstantUnaryFactory;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.UnaryFactory;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractColumnDefinitionProvider<C extends ColumnDefinition<K, C>, K extends FieldKey<K>> implements ColumnDefinitionProvider<C, K> {

    protected final List<PredicatedColunnDefinition<C, K>> definitions;
    protected final List<PredicatedColunnPropertyFactory<C, K>> properties;

    public AbstractColumnDefinitionProvider() {
        definitions = new ArrayList<PredicatedColunnDefinition<C, K>>();
        properties = new ArrayList<PredicatedColunnPropertyFactory<C, K>>();
    }
    public AbstractColumnDefinitionProvider(List<PredicatedColunnDefinition<C, K>> definitions,
                                            List<PredicatedColunnPropertyFactory<C, K>> properties) {
        this.definitions = definitions;
        this.properties = properties;
    }

    public void addColumnDefinition(Predicate<? super K> predicate, C definition) {
        definitions.add(new PredicatedColunnDefinition<C,K>(predicate, definition));
    }

    public void addColumnProperty(Predicate<? super K> predicate, UnaryFactory<? super K, Object> propertyFactory) {
        properties.add(new PredicatedColunnPropertyFactory<C, K>(predicate, propertyFactory));
    }

    @Override
    public C getColumnDefinition(K key) {
        C definition = identity();

        for(PredicatedColunnDefinition<C,K> def : definitions) {
            if (def.predicate.test(key)) {
                definition = compose(definition, def.columnDefinition);
            }
        }

        for (PredicatedColunnPropertyFactory<C, K> tuple2 : properties) {
            if (tuple2.predicate.test(key)) {
                Object columnProperty = tuple2.columnPropertyFactory.newInstance(key);
                if (columnProperty != null) {
                    definition = definition.add(columnProperty);
                }
            }
        }

        return  definition;
    }

    protected abstract C compose(C definition, C second);
    protected abstract C identity();

    public List<PredicatedColunnDefinition<C, K>> getDefinitions() {
        return definitions;
    }

    public List<PredicatedColunnPropertyFactory<C, K>> getProperties() {
        return properties;
    }

    @Override
    public <CP, BC extends BiConsumer<Predicate<? super K>, CP>> BC forEach(Class<CP> propertyType, BC consumer) {
        for(PredicatedColunnDefinition<C,K> def : definitions) {
            final CP cp = def.columnDefinition.lookFor(propertyType);
            if (cp != null) {
                consumer.accept(def.predicate, cp);
            }
        }
        for (PredicatedColunnPropertyFactory tuple2 : properties) {
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

    //Tuple2<Predicate<? super K>, C>
    public static class PredicatedColunnDefinition<C extends ColumnDefinition<K, C>, K extends FieldKey<K>> {
        private final Predicate<? super K> predicate;
        private final C columnDefinition;

        public PredicatedColunnDefinition(Predicate<? super K> predicate, C columnDefinition) {
            this.predicate = predicate;
            this.columnDefinition = columnDefinition;
        }
    }
    //Tuple2<Predicate<? super K>, UnaryFactory<? super K, ColumnProperty>>
    public static class PredicatedColunnPropertyFactory<C extends ColumnDefinition<K, C>, K extends FieldKey<K>> {
        private final Predicate<? super K> predicate;
        private final UnaryFactory<? super K, Object> columnPropertyFactory;

        public PredicatedColunnPropertyFactory(Predicate<? super K> predicate, UnaryFactory<? super K, Object> columnPropertyFactory) {
            this.predicate = predicate;
            this.columnPropertyFactory = columnPropertyFactory;
        }
    }

}
