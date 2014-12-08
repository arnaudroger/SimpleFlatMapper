package org.sfm.osgi;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.jdbc.JdbcMapper;

import static org.junit.Assert.*;

public class JdbcMapperServiceImplTest {

    @Test
    public void testNewFactory() throws Exception {

        JdbcMapper<DbObject> dbObjectJdbcMapper = new JdbcMapperServiceImpl().newFactory().newMapper(DbObject.class);

        assertNotNull(dbObjectJdbcMapper);

    }
}