package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.*;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.datastax.SettableDataMapperBuilder;
import org.simpleflatmapper.datastax.impl.SettableDataSetterFactory;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactoryImpl;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.meta.ClassMeta;

import java.lang.reflect.Type;
import java.util.Iterator;

public class UDTObjectSettableDataSetter<T> implements Setter<SettableByIndexData, T> {
    private final int index;
    private final UserType udtType;
    private final Mapper<T, SettableByIndexData> mapper;

    public UDTObjectSettableDataSetter(int index, UserType udtType, Mapper<T, SettableByIndexData> mapper) {
        this.index = index;
        this.udtType = udtType;
        this.mapper = mapper;
    }

    @Override
    public void set(SettableByIndexData target, T value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            UDTValue udtValue = udtType.newValue();
            mapper.mapTo(value, udtValue, null);
            target.setUDTValue(index, udtValue);
        }
    }

    public static <T> Setter<SettableByIndexData, T> newInstance(Type target,  UserType tt, int index, MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> config,
                                                                                      ReflectionService reflectionService) {
        Mapper<T, SettableByIndexData> mapper = newUDTMapper(target, tt, config, reflectionService);
        return new UDTObjectSettableDataSetter<T>(index, tt, mapper);
    }

    public static <T> Mapper<T, SettableByIndexData> newUDTMapper(Type target, UserType tt,
                                                                                       MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> config,
                                                                                       ReflectionService reflectionService) {
        SettableDataMapperBuilder<T> builder = newFieldMapperBuilder(config, reflectionService, target);
        Iterator<UserType.Field> iterator = tt.iterator();
        int i = 0;
        while(iterator.hasNext()) {
            UserType.Field f = iterator.next();
            builder.addColumn(new DatastaxColumnKey(f.getName(), i, f.getType()));
            i++;
        }
        return builder.mapper();
    }

    public static <T> SettableDataMapperBuilder<T> newFieldMapperBuilder(MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> config,
                                                                         ReflectionService reflectionService,  Type target) {
        ClassMeta<T> classMeta = reflectionService.getClassMeta(target);
        return new SettableDataMapperBuilder<T>(classMeta, config, ConstantTargetFieldMapperFactoryImpl.newInstance(new SettableDataSetterFactory(config, reflectionService), SettableByIndexData.class));
    }
}
