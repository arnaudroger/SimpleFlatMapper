package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.UserType;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.datastax.DatastaxMapperFactory;
import org.sfm.map.Mapper;
import org.sfm.map.MapperConfig;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.FieldMapperMapperBuilder;
import org.sfm.map.mapper.MapperSourceImpl;
import org.sfm.reflect.Getter;
import org.sfm.reflect.meta.ClassMeta;

import java.lang.reflect.Type;
import java.util.Iterator;

public class DatastaxUDTGetter<T> implements Getter<GettableByIndexData, T> {
    private final Mapper<GettableByIndexData, T> mapper;
    private final int index;

    public DatastaxUDTGetter(Mapper<GettableByIndexData, T> mapper, int index) {
        this.mapper = mapper;
        this.index = index;
    }

    @Override
    public T get(GettableByIndexData target) throws Exception {
        return mapper.map(target.getUDTValue(index));
    }

    @SuppressWarnings("unchecked")
    public static <P> Getter<GettableByIndexData, P> newInstance(DatastaxMapperFactory factory, Type target,  UserType tt, int index) {
        Mapper<GettableByIndexData, P> mapper = newUDTMapper(target, tt, factory);
        return new DatastaxUDTGetter<P>(mapper, index);
    }

    public static <P> Mapper<GettableByIndexData, P> newUDTMapper(Type target, UserType tt, DatastaxMapperFactory factory) {
        FieldMapperMapperBuilder<GettableByIndexData, P, DatastaxColumnKey> builder = newFieldMapperBuilder(factory, target);

        Iterator<UserType.Field> iterator = tt.iterator();
        int i = 0;
        while(iterator.hasNext()) {
            UserType.Field f = iterator.next();
            FieldMapperColumnDefinition<DatastaxColumnKey, GettableByIndexData> identity = FieldMapperColumnDefinition.identity();
            builder.addMapping(new DatastaxColumnKey(f.getName(), i, f.getType()), identity);
            i ++;
        }

        return builder.mapper();
    }

    public static <P> FieldMapperMapperBuilder<GettableByIndexData, P, DatastaxColumnKey> newFieldMapperBuilder(DatastaxMapperFactory factory, Type target) {
        MapperConfig mapperConfig = factory.mapperConfig();
        MapperSourceImpl<GettableByIndexData, DatastaxColumnKey> mapperSource = new MapperSourceImpl<GettableByIndexData, DatastaxColumnKey>(GettableByIndexData.class, new RowGetterFactory(factory));
        ClassMeta<P> classMeta = factory.getClassMeta(target);
        return new FieldMapperMapperBuilder<GettableByIndexData, P, DatastaxColumnKey>(
                mapperSource,
                classMeta,
                mapperConfig,
                new DatastaxMappingContextFactoryBuilder()
                );
    }
}
