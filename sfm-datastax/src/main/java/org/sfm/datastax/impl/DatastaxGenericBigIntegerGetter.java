package org.sfm.datastax.impl;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.sfm.reflect.Getter;

import java.math.BigInteger;

public class DatastaxGenericBigIntegerGetter implements Getter<GettableByIndexData, BigInteger> {

    private final int index;
    private final DataType.Name dataTypeName;

    public DatastaxGenericBigIntegerGetter(int index, DataType dataType) {
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
    public BigInteger get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        switch (dataTypeName) {
            case BIGINT:
            case COUNTER:
                return BigInteger.valueOf(target.getLong(index));
            case VARINT:
                return target.getVarint(index);
            case INT:
                return BigInteger.valueOf(target.getInt(index));
            case DECIMAL:
                return target.getDecimal(index).toBigInteger();
            case FLOAT:
                return BigInteger.valueOf((long) target.getFloat(index));
            case DOUBLE:
                return BigInteger.valueOf((long) target.getDouble(index));
        }
        return null;
    }
}
