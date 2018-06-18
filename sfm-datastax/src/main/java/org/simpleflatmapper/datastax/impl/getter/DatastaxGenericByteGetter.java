package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.datastax.DataTypeHelper;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;

public class DatastaxGenericByteGetter implements ByteGetter<GettableByIndexData>, Getter<GettableByIndexData, Byte> {

    private final int index;
    private final DataType.Name dataTypeName;

    public DatastaxGenericByteGetter(int index, DataType dataType) {
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
    public Byte get(GettableByIndexData target) throws Exception {
        if (target.isNull(index)) {
            return null;
        }
          return getByte(target);
    }

    @Override
    public byte getByte(GettableByIndexData target) throws Exception {
        switch (dataTypeName) {
            case BIGINT:
            case COUNTER:
                return (byte)target.getLong(index);
            case VARINT:
                return target.getVarint(index).byteValue();
            case INT:
                return (byte)target.getInt(index);
            case DECIMAL:
                return target.getDecimal(index).byteValue();
            case FLOAT:
                return (byte)target.getFloat(index);
            case DOUBLE:
                return (byte)target.getDouble(index);
            case SMALLINT:
                return (byte)target.getShort(index);
            case TINYINT:
                return (byte)target.getByte(index);
            case TIME:
                return (byte)target.getTime(index);
        }

        return 0;
    }
}
