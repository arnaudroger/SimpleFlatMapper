package org.simpleflatmapper.jdbc.impl;

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
import java.util.HashMap;
import java.util.Map;
//IFJAVA8_END

public class JDBCTypeHelper {

    private static final Map<String, Class<?>> propertyTypeDefaultSqlType = new HashMap<String, Class<?>>();

    static {
        propertyTypeDefaultSqlType.put("java.util.Date", Timestamp.class);
        propertyTypeDefaultSqlType.put("java.util.Calendar", Timestamp.class);
        propertyTypeDefaultSqlType.put("java.time.Instant", Timestamp.class);
        propertyTypeDefaultSqlType.put("org.joda.time.Instant", Timestamp.class);
        propertyTypeDefaultSqlType.put("java.time.LocalDateTime", Timestamp.class);
        propertyTypeDefaultSqlType.put("java.time.ZonedDateTime", Timestamp.class);
        propertyTypeDefaultSqlType.put("java.time.OffsetDateTime", Timestamp.class);
        propertyTypeDefaultSqlType.put("org.joda.time.DateTime", Timestamp.class);
        propertyTypeDefaultSqlType.put("org.joda.time.LocalDateTime", Timestamp.class);
        propertyTypeDefaultSqlType.put("java.time.LocalTime", Time.class);
        propertyTypeDefaultSqlType.put("java.time.OffsetTime", Time.class);
        propertyTypeDefaultSqlType.put("org.joda.time.LocalTime", Time.class);
        propertyTypeDefaultSqlType.put("java.time.LocalDate", Date.class);
        propertyTypeDefaultSqlType.put("java.time.YearMonth", Date.class);
        propertyTypeDefaultSqlType.put("java.time.Year", Date.class);
        propertyTypeDefaultSqlType.put("org.joda.time.LocalDate", Date.class);
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
                return byte.class;
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

        Class<?> defaultSqlType = propertyTypeDefaultSqlType.get(TypeHelper.toClass(propertyType).getName());
        if (defaultSqlType != null) {
            return defaultSqlType;
        }

        return Object.class;
    }
}
