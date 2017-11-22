package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;

import java.util.Map;

public class Issue472Test {
    
    
    public static class MyPojo {
        private final String id;
        private final Map<String, String> map;

        public MyPojo(String id, Map<String, String> map) {
            this.id = id;
            this.map = map;
        }

        public String getId() {
            return id;
        }

        public Map<String, String> getMap() {
            return map;
        }
    }
    public static class MyPojo2 {
        private String id;
        private Map<String, String> map;

        public void setId(String id) {
            this.id = id;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }

        public String getId() {
            return id;
        }

        public Map<String, String> getMap() {
            return map;
        }
        
    }
    @Test
    public void testMapConstructor() {
        JdbcMapperFactory.newInstance().addKeys("id").newMapper(MyPojo.class);
    }
    @Test
    public void testMapSG() {
        JdbcMapperFactory.newInstance().addKeys("id").newMapper(MyPojo2.class);
    }
}
