package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Issue687ArrayMappingTest {
    @Test
    public void mapBooleanArrayToBooleanArray() throws SQLException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();


            DynamicJdbcMapper<MyObjBoolean> mapper = JdbcMapperFactory
                    .newInstance().newMapper(MyObjBoolean.class);

            ResultSet rs = s.executeQuery("select 123 as id, array[true, false]::boolean[] as array");


            rs.next();

            MyObjBoolean map = mapper.map(rs);

            assertEquals(123l, map.id);
            assertArrayEquals(new boolean[]{true, false}, map.array);

        } finally {
            c.close();
        }
    }

    public static class MyObjBoolean {
        public final boolean[] array;
        public final long id;

        public MyObjBoolean(boolean[] array, long id) {
            this.array = array;
            this.id = id;
        }
    }
 
    
    @Test
    public void mapByteArrayToByteArray() throws SQLException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();


            DynamicJdbcMapper<MyObjByte> mapper = JdbcMapperFactory
                    .newInstance().newMapper(MyObjByte.class);

            ResultSet rs = s.executeQuery("select 123 as id, bytea '\\x407F' as array");


            rs.next();

            MyObjByte map = mapper.map(rs);

            assertEquals(123l, map.id);
            assertArrayEquals(new byte[]{64, 127}, map.array);

        } finally {
            c.close();
        }
    }

    public static class MyObjByte {
        public final byte[] array;
        public final long id;

        public MyObjByte(byte[] array, long id) {
            this.array = array;
            this.id = id;
        }
    }

    @Test
    public void mapCharArrayToCharArray() throws SQLException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();


            DynamicJdbcMapper<MyObjChar> mapper = JdbcMapperFactory
                    .newInstance().newMapper(MyObjChar.class);

            ResultSet rs = s.executeQuery("select 123 as id, array[456, 678]::smallint[] as array");


            rs.next();

            MyObjChar map = mapper.map(rs);

            assertEquals(123l, map.id);
            assertArrayEquals(new char[]{456, 678}, map.array);

        } finally {
            c.close();
        }
    }

    public static class MyObjChar {
        public final char[] array;
        public final long id;

        public MyObjChar(char[] array, long id) {
            this.array = array;
            this.id = id;
        }
    }

    @Test
    public void mapShortArrayToShortArray() throws SQLException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();


            DynamicJdbcMapper<MyObjShort> mapper = JdbcMapperFactory
                    .newInstance().newMapper(MyObjShort.class);

            ResultSet rs = s.executeQuery("select 123 as id, array[456, 678]::smallint[] as array");


            rs.next();

            MyObjShort map = mapper.map(rs);

            assertEquals(123l, map.id);
            assertArrayEquals(new short[]{456, 678}, map.array);

        } finally {
            c.close();
        }
    }

    public static class MyObjShort {
        public final short[] array;
        public final long id;

        public MyObjShort(short[] array, long id) {
            this.array = array;
            this.id = id;
        }
    }

    @Test
    public void mapIntArrayToIntArray() throws SQLException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();


            DynamicJdbcMapper<MyObjInt> mapper = JdbcMapperFactory
                    .newInstance().newMapper(MyObjInt.class);

            ResultSet rs = s.executeQuery("select 123 as id, array[456, 678]::int[] as array");


            rs.next();

            MyObjInt map = mapper.map(rs);

            assertEquals(123l, map.id);
            assertArrayEquals(new int[]{456, 678}, map.array);

        } finally {
            c.close();
        }
    }

    public static class MyObjInt {
        public final int[] array;
        public final long id;

        public MyObjInt(int[] array, long id) {
            this.array = array;
            this.id = id;
        }
    }
    
    @Test
    public void mapBigintArrayToLongArray() throws SQLException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();


            DynamicJdbcMapper<MyObjLong> mapper = JdbcMapperFactory
                    .newInstance().newMapper(MyObjLong.class);

            ResultSet rs = s.executeQuery("select 123 as id, array[456, 678]::bigint[] as array");


            rs.next();

            MyObjLong map = mapper.map(rs);

            assertEquals(123l, map.id);
            assertArrayEquals(new long[]{456l, 678l}, map.array);

        } finally {
            c.close();
        }
    }

    public static class MyObjLong {
        public final long[] array;
        public final long id;

        public MyObjLong(long[] array, long id) {
            this.array = array;
            this.id = id;
        }
    }

    @Test
    public void mapFloatsToFloatArray() throws SQLException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();


            DynamicJdbcMapper<MyObjFloat> mapper = JdbcMapperFactory
                    .newInstance().newMapper(MyObjFloat.class);

            ResultSet rs = s.executeQuery("select 123 as id, array[456.54, 678.78]::real[] as array");


            rs.next();

            MyObjFloat map = mapper.map(rs);

            assertEquals(123l, map.id);
            assertArrayEquals(new float[]{456.54f, 678.78f}, map.array, 0.001f);

        } finally {
            c.close();
        }
    }

    public static class MyObjFloat {
        public final float[] array;
        public final long id;

        public MyObjFloat(float[] array, long id) {
            this.array = array;
            this.id = id;
        }
    }

    @Test
    public void mapDoublesToDoubleArray() throws SQLException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();


            DynamicJdbcMapper<MyObjDouble> mapper = JdbcMapperFactory
                    .newInstance().newMapper(MyObjDouble.class);

            ResultSet rs = s.executeQuery("select 123 as id, array[456.54, 678.78]::double precision[] as array");


            rs.next();

            MyObjDouble map = mapper.map(rs);

            assertEquals(123l, map.id);
            assertArrayEquals(new double[]{456.54, 678.78}, map.array, 0.001f);

        } finally {
            c.close();
        }
    }

    public static class MyObjDouble {
        public final double[] array;
        public final long id;

        public MyObjDouble(double[] array, long id) {
            this.array = array;
            this.id = id;
        }
    }

    @Test
    public void mapStringsToStringArray() throws SQLException {

        Connection c = DbHelper.getDbConnection(DbHelper.TargetDB.POSTGRESQL);
        if (c == null) return;
        try
        {
            Statement s = c.createStatement();


            DynamicJdbcMapper<MyObjString> mapper = JdbcMapperFactory
                    .newInstance().newMapper(MyObjString.class);

            ResultSet rs = s.executeQuery("select 123 as id, array['hello', 'world']::text[] as array");


            rs.next();

            MyObjString map = mapper.map(rs);

            assertEquals(123l, map.id);
            assertArrayEquals(new String[]{"hello", "world"}, map.array);

        } finally {
            c.close();
        }
    }

    public static class MyObjString {
        public final String[] array;
        public final long id;

        public MyObjString(String[] array, long id) {
            this.array = array;
            this.id = id;
        }
    }

}
