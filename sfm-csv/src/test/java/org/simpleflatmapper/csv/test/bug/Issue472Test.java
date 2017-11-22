package org.simpleflatmapper.csv.test.bug;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.util.ListCollector;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MyPojo myPojo = (MyPojo) o;

            if (id != null ? !id.equals(myPojo.id) : myPojo.id != null) return false;
            return map != null ? map.equals(myPojo.map) : myPojo.map == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (map != null ? map.hashCode() : 0);
            return result;
        }
    }
    @Test
    public void testMapConstructor() throws IOException {
        CsvMapper<MyPojo> mapper = CsvMapperFactory.newInstance().addKeys("id").newMapper(MyPojo.class);

        List<MyPojo> list = mapper.forEach(new StringReader("id,map_v1,map_v2\ni1,c1,c2"), new ListCollector<MyPojo>()).getList();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("v1", "c1");
        map.put("v2", "c2");
        assertEquals(list.get(0), new MyPojo("i1", map));
    }
}
