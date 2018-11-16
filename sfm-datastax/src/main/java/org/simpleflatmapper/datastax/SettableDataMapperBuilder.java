package org.simpleflatmapper.datastax;


import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.mapper.AbstractConstantTargetMapperBuilder;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactory;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.meta.ClassMeta;

public class SettableDataMapperBuilder<T> extends AbstractConstantTargetMapperBuilder<SettableByIndexData, T, DatastaxColumnKey, SettableDataMapperBuilder<T>> {

    public SettableDataMapperBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<DatastaxColumnKey, ?> mapperConfig,
            ConstantTargetFieldMapperFactory<SettableByIndexData, DatastaxColumnKey> fieldMapperFactory) {
        super(classMeta, SettableByIndexData.class, mapperConfig, fieldMapperFactory);
    }

    @Override
    protected BiInstantiator<T, MappingContext<? super T>, SettableByIndexData> getInstantiator() {
        return new NullInstantiator<T>();
    }

    @Override
    protected DatastaxColumnKey newKey(String column, int i, FieldMapperColumnDefinition<DatastaxColumnKey> columnDefinition) {
        return new DatastaxColumnKey(column, i);
    }

    private static class NullInstantiator<T> implements BiInstantiator<T, MappingContext<? super T>, SettableByIndexData> {
        @Override
        public BoundStatement newInstance(T o, MappingContext< ? super T> context) throws Exception {
            throw new UnsupportedOperationException();
        }
    }

    protected int getStartingIndex() {
        return 0;
    }


}
