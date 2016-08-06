package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DataHelper;
import org.simpleflatmapper.datastax.DataTypeHelper;
import org.simpleflatmapper.reflect.Getter;

import java.math.BigInteger;

public class DatastaxGenericBigIntegerGetter implements Getter<GettableByIndexData, BigInteger> {

    private final int index;
    private final DataType.Name dataTypeName;

    public DatastaxGenericBigIntegerGetter(int index, DataType dataType) {
        this.index = index;
        this.dataTypeName = validateName(dataType);
    }

    private DataType.Name validateName(DataType dataType) {
        if (DataTypeHelper.isNumber(dataType)) {
            return dataType.getName();
        }
        throw new IllegalArgumentException("DataType " + dataType + "is not a number");
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

        if (DataTypeHelper.isSmallInt(dataTypeName)) return BigInteger.valueOf((long) DataHelper.getShort(index, target));
        if (DataTypeHelper.isTinyInt(dataTypeName)) return BigInteger.valueOf((long) DataHelper.getByte(index, target));
        if (DataTypeHelper.isTime(dataTypeName)) return BigInteger.valueOf(DataHelper.getTime(index, target));

        return null;
    }
}
