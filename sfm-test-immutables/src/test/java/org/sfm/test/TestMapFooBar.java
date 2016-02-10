package org.sfm.test;

import org.junit.Test;
import org.sfm.jdbc.JdbcMapperBuilder;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.jdbc.PreparedStatementMapperBuilder;

import static org.junit.Assert.assertNotNull;

public class TestMapFooBar {

    @Test
    public void mapFooBar() {
        final JdbcMapperBuilder<FoobarValue> builder = JdbcMapperFactory.newInstance().newBuilder(FoobarValue.class);

        assertNotNull(builder.addKey("foo").addKey("bar").addKey("crux").mapper());

        final PreparedStatementMapperBuilder<FoobarValue> buildFrom = JdbcMapperFactory.newInstance().buildFrom(FoobarValue.class);
        assertNotNull(buildFrom.addColumn("foo").addColumn("bar").addColumn("crux").buildIndexFieldMappers());
    }

}
