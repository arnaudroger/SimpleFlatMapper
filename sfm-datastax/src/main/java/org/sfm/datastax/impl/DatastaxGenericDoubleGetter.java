package org.sfm.datastax.impl;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableData;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.DoubleGetter;
import org.sfm.reflect.primitive.ShortGetter;

public class DatastaxGenericDoubleGetter implements DoubleGetter<GettableData>, Getter<GettableData, Double> {

    private final int index;
    private final DataType.Name dataTypeName;

    public DatastaxGenericDoubleGetter(int index, DataType dataType) {
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
    public Double get(GettableData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
          return getDouble(target);
    }

    @Override
    public double getDouble(GettableData target) throws Exception {
        switch (dataTypeName) {
            case BIGINT:
            case COUNTER:
                return (double)target.getLong(index);
            case VARINT:
                return target.getVarint(index).doubleValue();
            case INT:
                return (double)target.getInt(index);
            case DECIMAL:
                return target.getDecimal(index).doubleValue();
            case FLOAT:
                return (double)target.getFloat(index);
            case DOUBLE:
                return target.getDouble(index);
        }
        return 0;
    }
}
