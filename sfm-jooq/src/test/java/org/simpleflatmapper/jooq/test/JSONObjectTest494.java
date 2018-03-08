package org.simpleflatmapper.jooq.test;

import org.jooq.tools.json.JSONObject;
import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JSONObjectTest494 {
    
    @Test
    public void testJsonObject() throws SQLException {
        JdbcMapper<File> details = JdbcMapperFactory.newInstance().newBuilder(File.class).addMapping("details").mapper();

        ResultSet mock = mock(ResultSet.class);
        when(mock.getObject(1)).thenReturn("{}");
        File map = details.map(mock);

        System.out.println("map = " + map.details);
    }
    
    public static class File {
        public final long id;
        public final String name;
        public final JSONObject details;

        public File(long id, String name, JSONObject details) {
            this.id = id;
            this.name = name;
            this.details = details;
        }
    }
}
