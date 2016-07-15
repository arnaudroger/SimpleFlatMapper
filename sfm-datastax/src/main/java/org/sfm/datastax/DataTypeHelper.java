package org.sfm.datastax;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.TupleValue;
import com.datastax.driver.core.UDTValue;
import com.sun.corba.se.impl.ior.ByteBuffer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.*;

public class DataTypeHelper {

    // https://docs.datastax.com/en/latest-java-driver/java-driver/reference/javaClass2Cql3Datatypes.html
    public static Class<?> asJavaClass(DataType dataType) {
        DataType.Name name = dataType.getName();
        if (name == null) return null;
        switch (name) {
            case ASCII:
                return String.class;
            case BIGINT:
                return Long.class;
            case BLOB:
                return ByteBuffer.class;
            case BOOLEAN:
                return Boolean.class;
            case COUNTER:
                return Long.class;
            case DECIMAL:
                return BigDecimal.class;
            case DOUBLE:
                return Double.class;
            case FLOAT:
                return Float.class;
            case INET:
                return InetAddress.class;
            case INT:
                return Integer.class;
            case LIST:
                return List.class;
            case MAP:
                return Map.class;
            case SET:
                return Set.class;
            case TEXT:
                return String.class;
            case TIMESTAMP:
                return Date.class;
            case TIMEUUID:
                return UUID.class;
            case UUID:
                return UUID.class;
            case VARCHAR:
                return String.class;
            case VARINT:
                return BigInteger.class;
            case UDT:
                return UDTValue.class;
            case TUPLE:
                return TupleValue.class;
            case CUSTOM:
                return ByteBuffer.class;

            case DATE: return LocalDate.class;
            case TIME: return Long.class;
            case SMALLINT: return Short.class;
            case TINYINT: return Byte.class;

        }
        return null;
    }
}
