package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.util.ListCollector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MapKeyValueTest {

    private static final Object[][] data = new Object[][] {
            { 1, "key1", "value1" },
            { 2, "key2", "value2" },
    };

    @Test
    public void testMapKeyValue() {
        ClassMeta<PojoWithMap> classMeta = reflectionService().getClassMeta(PojoWithMap.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<PojoWithMap> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<PojoWithMap>(classMeta, getMapperConfig());

        builder.addMapping("id");
        builder.addMapping("map_key");
        builder.addMapping("map_value");


        Mapper<Object[], PojoWithMap> rowMapper = builder.mapper();

        SetRowMapper<Object[], Object[][],PojoWithMap, RuntimeException> mapper =
                (SetRowMapper<Object[], Object[][], PojoWithMap, RuntimeException>) rowMapper;

        List<PojoWithMap> list = mapper.forEach(data, new ListCollector<PojoWithMap>()).getList();

        assertEquals(2, list.size());
        
        assertEquals(buildPojo(1, "key1", "value1"), list.get(0));
        assertEquals(buildPojo(2, "key2", "value2"), list.get(1));

    }

    private PojoWithMap buildPojo(int id, String... kvPair) {
        PojoWithMap p = new PojoWithMap();
        p.setId(id);
        Map<String, String> map = new HashMap<String, String>();
        
        for(int i = 0; i< kvPair.length; i+=2) {
            map.put(kvPair[i], kvPair[i + 1]);
        }
        p.setMap(map);
        
        return p;
    }

    private ReflectionService reflectionService() {
        return ReflectionService.disableAsm();
    }

    private MapperConfig<SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> getMapperConfig() {
        return MapperConfig.fieldMapperConfig();
    }

    public static class PojoWithMap {

        private int id;
        private Map<String, String> map;

        public int getId() {
            return id;
        }

        public Map<String, String> getMap() {
            return map;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setMap(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PojoWithMap that = (PojoWithMap) o;

            if (id != that.id) return false;
            return map != null ? map.equals(that.map) : that.map == null;
        }

        @Override
        public int hashCode() {
            int result = id;
            result = 31 * result + (map != null ? map.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "PojoWithMap{" +
                    "id=" + id +
                    ", map=" + map +
                    '}';
        }
    }
}
