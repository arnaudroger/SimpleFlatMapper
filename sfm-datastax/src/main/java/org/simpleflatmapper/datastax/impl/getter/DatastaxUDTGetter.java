package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.UserType;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.datastax.DatastaxMapperBuilder;
import org.simpleflatmapper.datastax.DatastaxMapperFactory;
import org.simpleflatmapper.datastax.impl.DatastaxMappingContextFactoryBuilder;
import org.simpleflatmapper.datastax.impl.RowGetterFactory;
import org.simpleflatmapper.map.ContextualSourceMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.ConstantSourceMapperBuilder;
import org.simpleflatmapper.map.mapper.MapperSourceImpl;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.meta.ClassMeta;

import java.lang.reflect.Type;
import java.util.Iterator;

public class DatastaxUDTGetter<T> implements Getter<GettableByIndexData, T> {

    private final ContextualSourceMapper<GettableByIndexData, T> mapper;
    private final int index;

    public DatastaxUDTGetter(ContextualSourceMapper<GettableByIndexData, T> mapper, int index) {
        this.mapper = mapper;
        this.index = index;
    }

    @Override
    public T get(GettableByIndexData target) throws Exception {
        return mapper.map(target.getUDTValue(index));
    }

    @SuppressWarnings("unchecked")
    public static <P> Getter<GettableByIndexData, P> newInstance(DatastaxMapperFactory factory, Type target,  UserType tt, int index) {
        ContextualSourceMapper<GettableByIndexData, P> mapper = newUDTMapper(target, tt, factory);
        return new DatastaxUDTGetter<P>(mapper, index);
    }

    public static <P> ContextualSourceMapper<GettableByIndexData, P> newUDTMapper(Type target, UserType tt, DatastaxMapperFactory factory) {
        ConstantSourceMapperBuilder<GettableByIndexData, P, DatastaxColumnKey> builder = newFieldMapperBuilder(factory, target);

        Iterator<UserType.Field> iterator = tt.iterator();
        int i = 0;
        while(iterator.hasNext()) {
            UserType.Field f = iterator.next();
            FieldMapperColumnDefinition<DatastaxColumnKey> identity = FieldMapperColumnDefinition.identity();
            builder.addMapping(new DatastaxColumnKey(f.getName(), i, f.getType()), identity);
            i ++;
        }

        return builder.mapper();
    }

    public static <P> ConstantSourceMapperBuilder<GettableByIndexData, P, DatastaxColumnKey> newFieldMapperBuilder(DatastaxMapperFactory factory, Type target) {
        MapperConfig<DatastaxColumnKey, Row> config = factory.mapperConfig(target);
        MapperSourceImpl<GettableByIndexData, DatastaxColumnKey> mapperSource = new MapperSourceImpl<GettableByIndexData, DatastaxColumnKey>(GettableByIndexData.class, new ContextualGetterFactoryAdapter<GettableByIndexData, DatastaxColumnKey>(new RowGetterFactory(factory)));
        ClassMeta<P> classMeta = factory.getClassMeta(target);
        return ConstantSourceMapperBuilder.<GettableByIndexData, P, DatastaxColumnKey>newConstantSourceMapperBuilder(
                mapperSource,
                classMeta,
                config,
                new DatastaxMappingContextFactoryBuilder<GettableByIndexData>(!config.unorderedJoin()),
                DatastaxMapperBuilder.KEY_FACTORY);
    }
}
