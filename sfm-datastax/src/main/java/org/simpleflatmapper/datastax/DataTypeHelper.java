package org.simpleflatmapper.datastax;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.TupleValue;
import com.datastax.driver.core.UDTValue;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.Date;

public class DataTypeHelper {



    // https://docs.datastax.com/en/latest-java-driver/java-driver/reference/javaClass2Cql3Datatypes.html
    public static Class<?> asJavaClass(DataType dataType) {
        DataType.Name name = dataType.getName();
        return asJavaClass(name);
    }

    public static Class<?> asJavaClass(DataType.Name name) {
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
            case DATE:
                return LocalDate.class;
            case TIME:
                return Long.class;
            case SMALLINT:
                return Short.class;
            case TINYINT:
                return Byte.class;
        }

        return null;
    }


    public static boolean isNumber(DataType type) {
        return isNumber(type.getName());
    }

    public static boolean isNumber(DataType.Name name) {
        switch (name) {
            case BIGINT:
            case VARINT:
            case INT:
            case DECIMAL:
            case FLOAT:
            case DOUBLE:
            case COUNTER:
            case SMALLINT:
            case TINYINT:
            case TIME:
                return true;
        }
        return false;
    }
    
    private final static Map<String, Class<?>> defaultTypes = new HashMap<String, Class<?>>();
    static {
        defaultTypes.put("java.util.Date", Date.class);
        defaultTypes.put("java.util.Calendar", Date.class);
        defaultTypes.put("java.time.Instant", Date.class);
        defaultTypes.put("org.joda.time.Instant", Date.class);
        defaultTypes.put("java.time.LocalDateTime", Date.class);
        defaultTypes.put("java.time.ZonedDateTime", Date.class);
        defaultTypes.put("java.time.OffsetDateTime", Date.class);
        defaultTypes.put("org.joda.time.DateTime", Date.class);
        defaultTypes.put("org.joda.time.LocalDateTime", Date.class);
        defaultTypes.put("java.time.LocalTime", Date.class);
        defaultTypes.put("java.time.OffsetTime", Date.class);
        defaultTypes.put("org.joda.time.LocalTime", Date.class);
        defaultTypes.put("java.time.LocalDate", Date.class);
        defaultTypes.put("java.time.YearMonth", Date.class);
        defaultTypes.put("java.time.Year", Date.class);
        defaultTypes.put("org.joda.time.LocalDate", Date.class);
        defaultTypes.put("java.lang.Number", BigDecimal.class);
    }
    public static Class<?> asJavaClass(DataType dataType, Type target) {
        if (dataType != null) {
            Class<?> dataTypeClass = asJavaClass(dataType);
            if (dataTypeClass != null) {
                return  dataTypeClass;
            }
        }
        return defaultTypes.get(TypeHelper.toClass(target).getName());
    }
}
