package org.simpleflatmapper.jdbc.test.impl;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcTypeHelper;

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

import static org.junit.Assert.assertEquals;

public class JDBCTypeHelperTest {

    @Test
    public void testSqlTypeMapping() {
        testSqlTypes(Array.class, Types.ARRAY);
        testSqlTypes(String.class, Types.CHAR, Types.VARCHAR, Types.LONGNVARCHAR);
        testSqlTypes(BigDecimal.class, Types.NUMERIC, Types.DECIMAL);
        testSqlTypes(boolean.class, Types.BIT);
        testSqlTypes(byte.class, Types.TINYINT);
        testSqlTypes(short.class, Types.SMALLINT);
        testSqlTypes(int.class, Types.INTEGER);
        testSqlTypes(long.class, Types.BIGINT);
        testSqlTypes(float.class, Types.REAL);
        testSqlTypes(double.class, Types.FLOAT, Types.DOUBLE);
        testSqlTypes(byte[].class, Types.BINARY, Types.VARBINARY, Types.LONGVARBINARY);
        testSqlTypes(Date.class, Types.DATE);
        testSqlTypes(Time.class, Types.TIME);
        testSqlTypes(Timestamp.class, Types.TIMESTAMP);
        testSqlTypes(Clob.class, Types.CLOB);
        testSqlTypes(Blob.class, Types.BLOB);
        testSqlTypes(Struct.class, Types.STRUCT);
        testSqlTypes(Ref.class, Types.REF);

        //IFJAVA8_START
        testSqlTypes(OffsetTime.class, Types.TIME_WITH_TIMEZONE);
        testSqlTypes(OffsetDateTime.class, Types.TIMESTAMP_WITH_TIMEZONE);
        //IFJAVA8_END
    }

    @Test
    public void testSqlTypeMappingDefault() {
        testSqlTypes(Timestamp.class, java.util.Date.class, Timestamp.class);

        //IFJAVA8_START
        testSqlTypes(Time.class, OffsetTime.class);
        testSqlTypes(Timestamp.class, OffsetDateTime.class);
        //IFJAVA8_END
    }

    private void testSqlTypes(Class<?> expected, int... sqlTypes) {
        for(int type : sqlTypes) {
            Assert.assertEquals(expected, JdbcTypeHelper.toJavaType(type, null));
        }
    }

    private void testSqlTypes(Class<?> expected, Class<?>... sqlTypes) {
        for(Class<?> type : sqlTypes) {
            assertEquals(expected, JdbcTypeHelper.toJavaType(JdbcColumnKey.UNDEFINED_TYPE, type));
        }
    }

}