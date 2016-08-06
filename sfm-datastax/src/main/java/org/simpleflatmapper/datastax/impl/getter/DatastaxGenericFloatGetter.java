package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DataHelper;
import org.simpleflatmapper.datastax.DataTypeHelper;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;

public class DatastaxGenericFloatGetter implements FloatGetter<GettableByIndexData>, Getter<GettableByIndexData, Float> {

    private final int index;
    private final DataType.Name dataTypeName;

    public DatastaxGenericFloatGetter(int index, DataType dataType) {
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
    public Float get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
          return getFloat(target);
    }

    @Override
    public float getFloat(GettableByIndexData target) throws Exception {
        switch (dataTypeName) {
            case BIGINT:
            case COUNTER:
                return (float)target.getLong(index);
            case VARINT:
                return target.getVarint(index).floatValue();
            case INT:
                return (float)target.getInt(index);
            case DECIMAL:
                return target.getDecimal(index).floatValue();
            case FLOAT:
                return target.getFloat(index);
            case DOUBLE:
                return (float)target.getDouble(index);
        }
        if (DataTypeHelper.isSmallInt(dataTypeName)) return (float) DataHelper.getShort(index, target);
        if (DataTypeHelper.isTinyInt(dataTypeName)) return (float)DataHelper.getByte(index, target);
        if (DataTypeHelper.isTime(dataTypeName)) return (float)DataHelper.getTime(index, target);

        return 0;
    }
}
