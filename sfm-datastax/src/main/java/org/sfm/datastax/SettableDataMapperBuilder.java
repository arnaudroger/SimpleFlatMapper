package org.sfm.datastax;


import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.SettableByIndexData;
import org.sfm.map.*;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ConstantTargetFieldMapperFactory;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.meta.ClassMeta;

public class SettableDataMapperBuilder<T> extends AbstractWriterBuilder<SettableByIndexData, T, DatastaxColumnKey, SettableDataMapperBuilder<T>> {

    public SettableDataMapperBuilder(
            ClassMeta<T> classMeta,
            MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> mapperConfig,
            ConstantTargetFieldMapperFactory<SettableByIndexData, DatastaxColumnKey> fieldMapperFactory) {
        super(classMeta, SettableByIndexData.class, mapperConfig, fieldMapperFactory);
    }

    @Override
    protected Instantiator<T, SettableByIndexData> getInstantiator() {
        return new NullInstantiator<T>();
    }

    @Override
    protected DatastaxColumnKey newKey(String column, int i, FieldMapperColumnDefinition<DatastaxColumnKey> columnDefinition) {
        return new DatastaxColumnKey(column, i);
    }

    private static class NullInstantiator<T> implements Instantiator<T, SettableByIndexData> {
        @Override
        public BoundStatement newInstance(T o) throws Exception {
            throw new UnsupportedOperationException();
        }
    }

    protected int getStartingIndex() {
        return 0;
    }


}
