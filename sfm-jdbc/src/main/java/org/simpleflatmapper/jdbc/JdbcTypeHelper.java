package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

//IFJAVA8_START
import java.time.OffsetDateTime;
import java.time.OffsetTime;
//IFJAVA8_END

import java.util.HashMap;
import java.util.Map;

public class JdbcTypeHelper {

    private static final Map<String, Class<?>> javaTypeToSqlType = new HashMap<String, Class<?>>();

    static {
        javaTypeToSqlType.put("java.lang.Number", BigDecimal.class);
        javaTypeToSqlType.put("java.util.Date", Timestamp.class);
        javaTypeToSqlType.put("java.util.Calendar", Timestamp.class);
        javaTypeToSqlType.put("java.sql.Timestamp", Timestamp.class);

        javaTypeToSqlType.put("java.time.Instant", Timestamp.class);
        javaTypeToSqlType.put("java.time.LocalDateTime", Timestamp.class);
        javaTypeToSqlType.put("java.time.ZonedDateTime", Timestamp.class);
        javaTypeToSqlType.put("java.time.OffsetDateTime", Timestamp.class);
        javaTypeToSqlType.put("java.time.LocalTime", Time.class);
        javaTypeToSqlType.put("java.time.OffsetTime", Time.class);
        javaTypeToSqlType.put("java.sql.Time", Time.class);
        javaTypeToSqlType.put("java.time.LocalDate", Date.class);
        javaTypeToSqlType.put("java.time.YearMonth", Date.class);
        javaTypeToSqlType.put("java.time.Year", Date.class);
        javaTypeToSqlType.put("java.sql.Date", Date.class);

        javaTypeToSqlType.put("org.joda.time.Instant", Timestamp.class);
        javaTypeToSqlType.put("org.joda.time.LocalDateTime", Timestamp.class);
        javaTypeToSqlType.put("org.joda.time.DateTime", Timestamp.class);
        javaTypeToSqlType.put("org.joda.time.LocalTime", Time.class);
        javaTypeToSqlType.put("org.joda.time.LocalDate", Date.class);
    }

    public static Class<?> toJavaType(int sqlType, Type propertyType) {
        switch (sqlType) {
            case Types.ARRAY: return Array.class;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGNVARCHAR:
                return String.class;
            case Types.NUMERIC:
            case Types.DECIMAL:
                return BigDecimal.class;
            case Types.BIT:
                return boolean.class;
            case Types.TINYINT:
                return byte.class;
            case Types.SMALLINT:
                return short.class;
            case Types.INTEGER:
                return int.class;
            case Types.BIGINT:
                return long.class;
            case Types.REAL:
                return float.class;
            case Types.FLOAT:
            case Types.DOUBLE:
                return double.class;
            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                return byte[].class;
            case Types.DATE:
                return Date.class;
            case Types.TIME:
                return Time.class;
            case Types.TIMESTAMP:
                return Timestamp.class;
            case Types.CLOB:
                return Clob.class;
            case Types.BLOB:
                return Blob.class;
            case Types.STRUCT:
                return Struct.class;
            case Types.REF:
                return Ref.class;

            //IFJAVA8_START
            case Types.TIME_WITH_TIMEZONE:
                return OffsetTime.class;
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return OffsetDateTime.class;
            //IFJAVA8_END

        }

        Class<?> defaultSqlType = javaTypeToSqlType.get(TypeHelper.toClass(propertyType).getName());
        if (defaultSqlType != null) {
            return defaultSqlType;
        }

        return Object.class;
    }
}
