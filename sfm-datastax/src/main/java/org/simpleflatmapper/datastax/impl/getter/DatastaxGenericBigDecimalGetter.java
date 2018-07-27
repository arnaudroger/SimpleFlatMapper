package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DataTypeHelper;
import org.simpleflatmapper.reflect.Getter;

import java.math.BigDecimal;

public class DatastaxGenericBigDecimalGetter implements Getter<GettableByIndexData, BigDecimal> {

    private final int index;
    private final DataType.Name dataTypeName;

    public DatastaxGenericBigDecimalGetter(int index, DataType dataType) {
        this.index = index;
        this.dataTypeName = validateName(dataType);
    }

    private DataType.Name validateName(DataType dataType) {

        final DataType.Name name = dataType.getName();

        if (DataTypeHelper.isNumber(name)) {
            return name;
        }

        throw new IllegalArgumentException("Datatype " + dataType + " not a number");
    }

    @Override
    public BigDecimal get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
        switch (dataTypeName) {
            case BIGINT:
            case COUNTER:
                return BigDecimal.valueOf(target.getLong(index));
            case VARINT:
                return new BigDecimal(target.getVarint(index));
            case INT:
                return BigDecimal.valueOf(target.getInt(index));
            case DECIMAL:
                return target.getDecimal(index);
            case FLOAT:
                return BigDecimal.valueOf(target.getFloat(index));
            case DOUBLE:
                return BigDecimal.valueOf(target.getDouble(index));
            case SMALLINT:
                return BigDecimal.valueOf(target.getShort(index));
            case TINYINT:
                return BigDecimal.valueOf(target.getByte(index));
            case TIME:
                return BigDecimal.valueOf(target.getTime(index));
        }

        return null;
    }
}
