package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.TupleType;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.datastax.DatastaxMapperFactory;
import org.simpleflatmapper.map.ContextualSourceMapper;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.ConstantSourceMapperBuilder;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.tuple.Tuple2;

import java.lang.reflect.Type;
import java.util.List;

public class DatastaxTupleGetter<T extends Tuple2<?, ?>> implements Getter<GettableByIndexData, T> {
    private final ContextualSourceMapper<GettableByIndexData, T> mapper;
    private final int index;

    public DatastaxTupleGetter(ContextualSourceMapper<GettableByIndexData, T> mapper, int index) {
        this.mapper = mapper;
        this.index = index;
    }

    @Override
    public T get(GettableByIndexData target) throws Exception {
        return mapper.map(target.getTupleValue(index));
    }

    @SuppressWarnings("unchecked")
    public static <P extends  Tuple2<?, ?>> Getter<GettableByIndexData, P> newInstance(DatastaxMapperFactory factory, Type target,  TupleType tt, int index) {
        ContextualSourceMapper<GettableByIndexData, P> mapper = newTupleMapper(target, tt, factory);
        return new DatastaxTupleGetter<P>(mapper, index);
    }

    public static <P extends Tuple2<?, ?>> ContextualSourceMapper<GettableByIndexData, P> newTupleMapper(Type target, TupleType tt, DatastaxMapperFactory factory) {
        ConstantSourceMapperBuilder<GettableByIndexData, P, DatastaxColumnKey> builder =
                DatastaxUDTGetter.newFieldMapperBuilder(factory, target);

        List<DataType> componentTypes = tt.getComponentTypes();
        for(int i = 0; i < componentTypes.size(); i++) {
            FieldMapperColumnDefinition<DatastaxColumnKey> identity = FieldMapperColumnDefinition.identity();
            builder.addMapping(new DatastaxColumnKey(String.valueOf( i), i, componentTypes.get(i)),
                    identity);
        }

        return builder.mapper();
    }
}
