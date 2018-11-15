package org.simpleflatmapper.datastax.test.impl;

import com.datastax.driver.core.DataType;
import org.junit.Test;
import org.simpleflatmapper.datastax.DatastaxMapper;
import org.simpleflatmapper.datastax.DatastaxMapperFactory;
import org.simpleflatmapper.datastax.test.beans.DbObjectsWithSet;

import static org.junit.Assert.assertNotNull;

public class DbObjectWithSetMapperTest {

    @Test
    public void test() {
        final DatastaxMapper<DbObjectsWithSet> mapper =
                DatastaxMapperFactory
                        .newInstance()
                        .newBuilder(DbObjectsWithSet.class)
                        .addMapping("id", 0, DataType.bigint(), new Object[0])
                        .addMapping("emails", 1, DataType.set(DataType.ascii()), new Object[0])
                .mapper();
        System.out.println("mapper = " + mapper);
        
        assertNotNull(mapper);

    }
}
