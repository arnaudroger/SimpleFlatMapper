package org.sfm.datastax.impl;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.reflect.primitive.LongGetter;

public class DatastaxGenericLongGetter implements LongGetter<GettableByIndexData>, Getter<GettableByIndexData, Long> {

    private final int index;
    private final DataType.Name dataTypeName;

    public DatastaxGenericLongGetter(int index, DataType dataType) {
        this.index = index;
        this.dataTypeName = validateName(dataType);
    }

    private DataType.Name validateName(DataType dataType) {

        final DataType.Name name = dataType.getName();
        switch (name) {
            case BIGINT:
            case VARINT:
            case INT:
            case DECIMAL:
            case FLOAT:
            case DOUBLE:
            case COUNTER:
            return name;
        }
        throw new IllegalArgumentException("Datatype " + dataType + " not a number");
    }

    @Override
    public Long get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
          return getLong(target);
    }

    @Override
    public long getLong(GettableByIndexData target) throws Exception {
        switch (dataTypeName) {
            case BIGINT:
            case COUNTER:
                return target.getLong(index);
            case VARINT:
                return target.getVarint(index).longValue();
            case INT:
                return target.getInt(index);
            case DECIMAL:
                return target.getDecimal(index).longValue();
            case FLOAT:
                return (long)target.getFloat(index);
            case DOUBLE:
                return (long)target.getDouble(index);
        }
        return 0;
    }
}
