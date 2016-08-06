package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DataHelper;
import org.simpleflatmapper.datastax.DataTypeHelper;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.IntGetter;

public class DatastaxGenericIntegerGetter implements IntGetter<GettableByIndexData>, Getter<GettableByIndexData, Integer> {

    private final int index;
    private final DataType.Name dataTypeName;

    public DatastaxGenericIntegerGetter(int index, DataType dataType) {
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
    public Integer get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
          return getInt(target);
    }

    @Override
    public int getInt(GettableByIndexData target) throws Exception {
        switch (dataTypeName) {
            case BIGINT:
            case COUNTER:
                return (int)target.getLong(index);
            case VARINT:
                return target.getVarint(index).intValue();
            case INT:
                return target.getInt(index);
            case DECIMAL:
                return target.getDecimal(index).intValue();
            case FLOAT:
                return (int)target.getFloat(index);
            case DOUBLE:
                return (int)target.getDouble(index);
        }
        if (DataTypeHelper.isSmallInt(dataTypeName)) return (int) DataHelper.getShort(index, target);
        if (DataTypeHelper.isTinyInt(dataTypeName)) return (int)DataHelper.getByte(index, target);
        if (DataTypeHelper.isTime(dataTypeName)) return (int) DataHelper.getTime(index, target);

        return 0;
    }
}
