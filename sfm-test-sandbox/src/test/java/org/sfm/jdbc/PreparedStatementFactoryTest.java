package org.sfm.jdbc;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertNotNull;

public class PreparedStatementFactoryTest {

    public static class MyObject {
        public LocalDateTime dateTime;
    }
    @Test
    public void testCanGetJavaTimeGetterWithoutJodaTimeInClassPath() {
        assertNotNull(JdbcMapperFactory.newInstance().buildFrom(MyObject.class).addColumn("dateTime").buildIndexFieldMappers());
    }
}
