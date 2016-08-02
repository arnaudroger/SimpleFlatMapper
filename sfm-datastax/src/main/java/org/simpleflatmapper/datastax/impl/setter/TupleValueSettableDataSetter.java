package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.SettableByIndexData;
import com.datastax.driver.core.TupleType;
import com.datastax.driver.core.TupleValue;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.datastax.SettableDataMapperBuilder;
import org.simpleflatmapper.datastax.impl.SettableDataSetterFactory;
import org.simpleflatmapper.core.map.Mapper;
import org.simpleflatmapper.core.map.MapperConfig;
import org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.core.map.mapper.ConstantTargetFieldMapperFactorImpl;
import org.simpleflatmapper.core.reflect.ReflectionService;
import org.simpleflatmapper.core.reflect.Setter;
import org.simpleflatmapper.core.reflect.meta.ClassMeta;
import org.simpleflatmapper.core.tuples.Tuple2;

import java.lang.reflect.Type;
import java.util.List;

public class TupleValueSettableDataSetter implements Setter<SettableByIndexData, TupleValue> {
    private final int index;

    public TupleValueSettableDataSetter(int index) {
        this.index = index;
    }

    @Override
    public void set(SettableByIndexData target, TupleValue value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            target.setTupleValue(index, value);
        }
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
