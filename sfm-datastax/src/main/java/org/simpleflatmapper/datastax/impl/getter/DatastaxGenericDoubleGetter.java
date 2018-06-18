package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DataTypeHelper;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;

public class DatastaxGenericDoubleGetter implements DoubleGetter<GettableByIndexData>, Getter<GettableByIndexData, Double> {

    private final int index;
    private final DataType.Name dataTypeName;

    public DatastaxGenericDoubleGetter(int index, DataType dataType) {
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
    public Double get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
          return getDouble(target);
    }

    @Override
    public double getDouble(GettableByIndexData target) throws Exception {
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
            case SMALLINT:
                return target.getShort(index);
            case TINYINT:
                return target.getByte(index);
            case TIME:
                return target.getTime(index);
        }
        
        return 0;
    }
}
