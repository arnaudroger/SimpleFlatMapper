package org.simpleflatmapper.datastax.test;

import com.datastax.driver.core.Session;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.datastax.DatastaxMapperFactory;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

public class DatastaxNumberTest extends AbstractDatastaxTest {

    @Before
    public void prepareTable() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                session.execute("create table IF NOT EXISTS test_number(bi  bigint primary key, i int, vi varint, dec decimal, f float, d double)");

                if (session.execute("select * from test_number").isExhausted()) {
                    session.execute("insert into test_number(bi, i, vi, dec, f, d) values(1, 2, 3, 3.5, 3.7, 3.9)");
                }
            }
        });
    }

    @Test
    public void testGetFloat() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final FloatObject nObject = DatastaxMapperFactory.newInstance().mapTo(FloatObject.class).iterator(session.execute("select * from test_number")).next();

                assertEquals(1.0, nObject.bi, 0.001);
                assertEquals(2.0, nObject.i, 0.001);
                assertEquals(3.0, nObject.vi, 0.001);
                assertEquals(3.5, nObject.dec, 0.001);
                assertEquals(3.7, nObject.f, 0.001);
                assertEquals(3.9, nObject.d, 0.001);
            }
        });

    }

    public static class FloatObject {
        public float bi;
        public float i;
        public float vi;
        public float dec;
        public float f;
        public float d;
    }


    @Test
    public void testGetDouble() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final DoubleObject nObject = DatastaxMapperFactory.newInstance().mapTo(DoubleObject.class).iterator(session.execute("select * from test_number")).next();

                assertEquals(1.0, nObject.bi, 0.001);
                assertEquals(2.0, nObject.i, 0.001);
                assertEquals(3.0, nObject.vi, 0.001);
                assertEquals(3.5, nObject.dec, 0.001);
                assertEquals(3.7, nObject.f, 0.001);
                assertEquals(3.9, nObject.d, 0.001);
            }
        });

    }

    public static class DoubleObject {
        public double bi;
        public double i;
        public double vi;
        public double dec;
        public double f;
        public double d;
    }



    @Test
    public void testGetBigDecimal() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final BigDecimalObject nObject = DatastaxMapperFactory.newInstance().mapTo(BigDecimalObject.class).iterator(session.execute("select * from test_number")).next();

                assertEquals(1.0, nObject.bi.doubleValue(), 0.001);
                assertEquals(2.0, nObject.i.doubleValue(), 0.001);
                assertEquals(3.0, nObject.vi.doubleValue(), 0.001);
                assertEquals(3.5, nObject.dec.doubleValue(), 0.001);
                assertEquals(3.7, nObject.f.doubleValue(), 0.001);
                assertEquals(3.9, nObject.d.doubleValue(), 0.001);
            }
        });

    }

    public static class BigDecimalObject {
        public BigDecimal bi;
        public BigDecimal i;
        public BigDecimal vi;
        public BigDecimal dec;
        public BigDecimal f;
        public BigDecimal d;
    }
    @Test
    public void testGetShort() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final ShortObject nObject = DatastaxMapperFactory.newInstance().mapTo(ShortObject.class).iterator(session.execute("select * from test_number")).next();

                assertEquals(1, nObject.bi);
                assertEquals(2, nObject.i);
                assertEquals(3, nObject.vi);
                assertEquals(3, nObject.dec);
                assertEquals(3, nObject.f);
                assertEquals(3, nObject.d);
            }
        });

    }

    public static class ShortObject {
        public short bi;
        public short i;
        public short vi;
        public short dec;
        public short f;
        public short d;
    }



    @Test
    public void testGetInt() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final IntObject nObject = DatastaxMapperFactory.newInstance().mapTo(IntObject.class).iterator(session.execute("select * from test_number")).next();

                assertEquals(1, nObject.bi);
                assertEquals(2, nObject.i);
                assertEquals(3, nObject.vi);
                assertEquals(3, nObject.dec);
                assertEquals(3, nObject.f);
                assertEquals(3, nObject.d);
            }
        });

    }

    public static class IntObject {
        public int bi;
        public int i;
        public int vi;
        public int dec;
        public int f;
        public int d;
    }


    @Test
    public void testGetLong() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final LongObject nObject = DatastaxMapperFactory.newInstance().mapTo(LongObject.class).iterator(session.execute("select * from test_number")).next();

                assertEquals(1, nObject.bi);
                assertEquals(2, nObject.i);
                assertEquals(3, nObject.vi);
                assertEquals(3, nObject.dec);
                assertEquals(3, nObject.f);
                assertEquals(3, nObject.d);
            }
        });

    }


    public static class LongObject {
        public long bi;
        public long i;
        public long vi;
        public long dec;
        public long f;
        public long d;

    }

    @Test
    public void testGetBigInteger() throws Exception {
        testInSession(new Callback() {
            @Override
            public void call(Session session) throws Exception {
                final BigIntegerObject nObject = DatastaxMapperFactory.newInstance().mapTo(BigIntegerObject.class).iterator(session.execute("select * from test_number")).next();

                assertEquals(1, nObject.bi.longValue());
                assertEquals(2, nObject.i.longValue());
                assertEquals(3, nObject.vi.longValue());
                assertEquals(3, nObject.dec.longValue());
                assertEquals(3, nObject.f.longValue());
                assertEquals(3, nObject.d.longValue());
            }
        });

    }


    public static class BigIntegerObject {
        public BigInteger bi;
        public BigInteger i;
        public BigInteger vi;
        public BigInteger dec;
        public BigInteger f;
        public BigInteger d;

    }

}
