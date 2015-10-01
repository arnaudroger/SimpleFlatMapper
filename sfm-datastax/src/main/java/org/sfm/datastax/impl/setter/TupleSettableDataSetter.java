package org.sfm.datastax.impl.setter;

import com.datastax.driver.core.*;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.datastax.SettableDataMapperBuilder;
import org.sfm.datastax.impl.SettableDataSetterFactory;
import org.sfm.map.Mapper;
import org.sfm.map.MapperConfig;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ConstantTargetFieldMapperFactorImpl;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.tuples.Tuple2;

import java.lang.reflect.Type;
import java.util.List;

public class TupleSettableDataSetter<T extends Tuple2<?, ?>> implements Setter<SettableByIndexData, T> {
    private final int index;
    private final TupleType tupleType;
    private final Mapper<T, SettableByIndexData> mapper;

    public TupleSettableDataSetter(int index, TupleType tupleType, Mapper<T, SettableByIndexData> mapper) {
        this.index = index;
        this.tupleType = tupleType;
        this.mapper = mapper;
    }

    @Override
    public void set(SettableByIndexData target, T value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            TupleValue tupleValue = tupleType.newValue();
            mapper.mapTo(value, tupleValue, null);
            target.setTupleValue(index, tupleValue);
        }
    }

    public static <T extends Tuple2<?, ?>> Setter<SettableByIndexData, T> newInstance(Type target,  TupleType tt, int index, MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> config,
                                                                                      ReflectionService reflectionService) {
        Mapper<T, SettableByIndexData> mapper = newTupleMapper(target, tt, config, reflectionService);
        return new TupleSettableDataSetter<T>(index, tt, mapper);
    }

    public static <T extends Tuple2<?, ?>> Mapper<T, SettableByIndexData> newTupleMapper(Type target, TupleType tt,
                                                                                         MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> config,
                                                                                         ReflectionService reflectionService) {
        SettableDataMapperBuilder<T> builder = newFieldMapperBuilder(config, reflectionService, target);
        List<DataType> componentTypes = tt.getComponentTypes();
        for(int i = 0; i < componentTypes.size(); i++) {
            builder.addColumn(new DatastaxColumnKey("elt" + i, i, componentTypes.get(i)));
        }
        return builder.mapper();
    }

    public static <T> SettableDataMapperBuilder<T> newFieldMapperBuilder(MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> config,
                                                                         ReflectionService reflectionService,  Type target) {
        ClassMeta<T> classMeta = reflectionService.getClassMeta(target);
        return new SettableDataMapperBuilder<T>(classMeta, config, ConstantTargetFieldMapperFactorImpl.instance(new SettableDataSetterFactory(config, reflectionService)));
    }
}
