package org.sfm.map.mapper;


import org.sfm.map.FieldKey;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.Predicate;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractColumnDefinitionProvider<C extends ColumnDefinition<K, C>, K extends FieldKey<K>> implements ColumnDefinitionProvider<C, K> {

    protected final List<Tuple2<Predicate<? super K>, C>> definitions;

    public AbstractColumnDefinitionProvider() {
        definitions = new ArrayList<Tuple2<Predicate<? super K>, C>>();
    }
    public AbstractColumnDefinitionProvider(List<Tuple2<Predicate<? super K>, C>> definitions) {
        this.definitions = definitions;
    }

    public void addColumnDefinition(Predicate<? super K> predicate, C definition) {
        definitions.add(new Tuple2<Predicate<? super K>, C>(predicate, definition));
    }

    @Override
    public C getColumnDefinition(K key) {
        C definition = identity();

        for(Tuple2<Predicate<? super K>, C> def : definitions) {
            if (def.first().test(key)) {
                definition = compose(definition, def.second());
            }
        }

        return  definition;
    }

    protected abstract C compose(C definition, C second);
    protected abstract C identity();
}
