package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DataTypeHelper;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;

public class DatastaxGenericShortGetter implements ShortGetter<GettableByIndexData>, Getter<GettableByIndexData, Short> {

    private final int index;
    private final DataType.Name dataTypeName;

    public DatastaxGenericShortGetter(int index, DataType dataType) {
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
    public Short get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
          return getShort(target);
    }

    @Override
    public short getShort(GettableByIndexData target) throws Exception {
        switch (dataTypeName) {
            case BIGINT:
            case COUNTER:
                return (short)target.getLong(index);
            case VARINT:
                return target.getVarint(index).shortValue();
            case INT:
                return (short)target.getInt(index);
            case DECIMAL:
                return target.getDecimal(index).shortValue();
            case FLOAT:
                return (short)target.getFloat(index);
            case DOUBLE:
                return (short)target.getDouble(index);
            case SMALLINT:
                return (short)target.getShort(index);
            case TINYINT:
                return (short)target.getByte(index);
            case TIME:
                return (short)target.getTime(index);
        }

        return 0;
    }
}
