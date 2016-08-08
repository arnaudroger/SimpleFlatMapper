package org.simpleflatmapper.datastax;

import com.datastax.driver.core.DataType;
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

    public static final Class<?> localDateClass;
    private static final int DATE_ORDINAL = getOrdinal("DATE");


    private static final int TIME_ORDINAL = getOrdinal("TIME");
    private static final int SMALLINT_ORDINAL = getOrdinal("SMALLINT");
    private static final int TINYINT_ORDINAL = getOrdinal("TINYINT");

    static {
        localDateClass = getLocalDateClass();
    }

    private static int getOrdinal(String type) {
        try {
            return DataType.Name.valueOf(type).ordinal();
        } catch(IllegalArgumentException e) {}
        return -1;
    }

    private static Class<?> getLocalDateClass() {
        try {
            return DataType.class.getClassLoader().loadClass("com.datastax.driver.core.LocalDate");
        } catch(Throwable t) {}
        return null;
    }

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
        }

        if (isDate(name)) return localDateClass;
        if (isTime(name)) return Long.class;
        if (isSmallInt(name)) return Short.class;
        if (isTinyInt(name)) return Byte.class;
        return null;
    }

    public static boolean isTinyInt(DataType.Name name) {
        return name.ordinal() == TINYINT_ORDINAL;
    }

    public static boolean isSmallInt(DataType.Name name) {
        return name.ordinal() == SMALLINT_ORDINAL;
    }

    public static boolean isTime(DataType.Name name) {
        return name.ordinal() == TIME_ORDINAL;
    }

    public static boolean isDate(DataType.Name name) {
        return name.ordinal() == DATE_ORDINAL;
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
                return true;
        }

        return DataTypeHelper.isSmallInt(name)
                || DataTypeHelper.isTinyInt(name)
                || DataTypeHelper.isTime(name);
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
