package org.sfm.map.impl;


import org.sfm.map.CaseInsensitiveFieldKeyNamePredicate;
import org.sfm.map.ColumnDefinition;
import org.sfm.map.ColumnDefinitionProvider;
import org.sfm.map.FieldKey;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.Predicate;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractColumnDefinitionProvider<C extends ColumnDefinition<K>, K extends FieldKey<K>> implements ColumnDefinitionProvider<C, K> {

    private List<Tuple2<Predicate<? super K>, C>> defintions = new ArrayList<Tuple2<Predicate<? super K>, C>>();

    public void addColumnDefinition(Predicate<? super K> predicate, C definition) {
        defintions.add(new Tuple2<Predicate<? super K>, C>(predicate, definition));
    }

    @Override
    public C getColumnDefinition(K key) {
        C definition = identity();

        for(Tuple2<Predicate<? super K>, C> def : defintions) {
            if (def.first().test(key)) {
                definition = compose(definition, def.second());
            }
        }

        return  definition;
    }

    protected abstract C compose(C definition, C second);
    protected abstract C identity();
}
