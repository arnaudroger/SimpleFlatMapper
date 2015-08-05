package org.sfm.datastax.impl;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.TupleType;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.datastax.DatastaxMapperFactory;
import org.sfm.map.Mapper;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.FieldMapperMapperBuilder;
import org.sfm.reflect.Getter;
import org.sfm.tuples.Tuple2;

import java.lang.reflect.Type;
import java.util.List;

public class DatastaxTupleGetter<T extends Tuple2<?, ?>> implements Getter<GettableByIndexData, T> {
    private final Mapper<GettableByIndexData, T> mapper;
    private final int index;

    public DatastaxTupleGetter(Mapper<GettableByIndexData, T> mapper, int index) {
        this.mapper = mapper;
        this.index = index;
    }

    @Override
    public T get(GettableByIndexData target) throws Exception {
        return mapper.map(target.getTupleValue(index));
    }

    @SuppressWarnings("unchecked")
    public static <P extends  Tuple2<?, ?>> Getter<GettableByIndexData, P> newInstance(DatastaxMapperFactory factory, Type target,  TupleType tt, int index) {
        Mapper<GettableByIndexData, P> mapper = newTupleMapper(target, tt, factory);
        return new DatastaxTupleGetter<P>(mapper, index);
    }

    public static <P extends Tuple2<?, ?>> Mapper<GettableByIndexData, P> newTupleMapper(Type target, TupleType tt, DatastaxMapperFactory factory) {
        FieldMapperMapperBuilder<GettableByIndexData, P, DatastaxColumnKey> builder =
                DatastaxUDTGetter.newFieldMapperBuilder(factory, target);

        List<DataType> componentTypes = tt.getComponentTypes();
        for(int i = 0; i < componentTypes.size(); i++) {
            FieldMapperColumnDefinition<DatastaxColumnKey> identity = FieldMapperColumnDefinition.identity();
            builder.addMapping(new DatastaxColumnKey("elt" + i, i, componentTypes.get(i)),
                    identity);
        }

        return builder.mapper();
    }
}
