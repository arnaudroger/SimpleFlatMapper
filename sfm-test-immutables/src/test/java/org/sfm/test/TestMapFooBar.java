package org.sfm.test;

import org.junit.Test;
import org.sfm.jdbc.JdbcMapperBuilder;
import org.sfm.jdbc.JdbcMapperFactory;

import static org.junit.Assert.assertNotNull;

public class TestMapFooBar {

    @Test
    public void mapFooBar() {
        final JdbcMapperBuilder<FoobarValue> builder = JdbcMapperFactory.newInstance().newBuilder(FoobarValue.class);

        assertNotNull(builder.addKey("foo").addKey("bar").mapper());
    }

}
