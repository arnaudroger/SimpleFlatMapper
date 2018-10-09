package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.EnumerableMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.mapper.JoinMapper;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.property.MapTypeProperty;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.ListCollector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MapKeyValueTest {

    private static final Object[][] dataSimple = new Object[][] {
            { 1, "key1", "value1" },
            { 2, "key2", "value2" },
    };

    private static final Object[][] dataComplex = new Object[][] {
            { 1, "key11", "key12", "value11", "value12" },
            { 2, "key21", "key22", "value21", "value22" },
    };

    private static final Object[][] dataSimpleJoin = new Object[][] {
            { 1, "key1", "value1" },
            { 1, "key11", "value11" },
            { 2, "key2", "value2" },
            { 2, "key22", "value22" },
    };

    private static final Object[][] dataComplexJoin = new Object[][] {
            { 1, "key11", "key12", "value11", "value12" },
            { 1, "key111", "key121", "value111", "value121" },
            { 2, "key21", "key22", "value21", "value22" },
            { 2, "key211", "key221", "value211", "value221" },
    };


    @Test
    public void testMapKeyValue() throws Exception {
        ClassMeta<PojoWithMap> classMeta = reflectionService().getClassMeta(PojoWithMap.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<PojoWithMap> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<PojoWithMap>(classMeta, getMapperConfig());

        builder.addMapping("id");
        builder.addMapping("map_key", MapTypeProperty.KEY_VALUE);
        builder.addMapping("map_value");


        EnumerableMapper<Object[][], PojoWithMap, ?> mapper = builder.mapper();

        List<PojoWithMap> list = mapper.forEach(dataSimple, new ListCollector<PojoWithMap>()).getList();

        assertEquals(2, list.size());
        
        assertEquals(buildPojo(1, "key1", "value1"), list.get(0));
        assertEquals(buildPojo(2, "key2", "value2"), list.get(1));
    }

    @Test
    public void testMapKeyValueJoin() throws Exception {
        ClassMeta<PojoWithMap> classMeta = reflectionService().getClassMeta(PojoWithMap.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<PojoWithMap> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<PojoWithMap>(classMeta, getMapperConfig());

        builder.addKey("id");
        builder.addMapping("map_key", MapTypeProperty.KEY_VALUE);
        builder.addMapping("map_value");



        JoinMapper<Object[], Object[][],PojoWithMap, ?> mapper =
                (JoinMapper<Object[], Object[][], PojoWithMap, ?>) builder.mapper();

        List<PojoWithMap> list = mapper.forEach(dataSimpleJoin, new ListCollector<PojoWithMap>()).getList();

        assertEquals(2, list.size());

        assertEquals(buildPojo(1, "key1", "value1", "key11", "value11"), list.get(0));
        assertEquals(buildPojo(2, "key2", "value2", "key22", "value22"), list.get(1));
    }
    

    @Test
    public void testMapComplexKeyValue() throws Exception {
        ClassMeta<ComplexPojoWithMap> classMeta = reflectionService().getClassMeta(ComplexPojoWithMap.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<ComplexPojoWithMap> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<ComplexPojoWithMap>(classMeta, getMapperConfig());

        builder.addMapping("id");
        builder.addMapping("map_key_elt0");
        builder.addMapping("map_key_elt1");
        builder.addMapping("map_value_elt0");
        builder.addMapping("map_value_elt1");



        SetRowMapper<Object[], Object[][],ComplexPojoWithMap, ?> mapper =
                (SetRowMapper<Object[], Object[][], ComplexPojoWithMap, ?>)  builder.mapper();

        List<ComplexPojoWithMap> list = mapper.forEach(dataComplex, new ListCollector<ComplexPojoWithMap>()).getList();

        assertEquals(2, list.size());

        assertEquals(buildComplexPojo(1, "key11", "key12", "value11", "value12"), list.get(0));
        assertEquals(buildComplexPojo(2, "key21", "key22", "value21", "value22"), list.get(1));
    }

    @Test
    public void testMapComplexKeyValueJoin() throws Exception {
        ClassMeta<ComplexPojoWithMap> classMeta = reflectionService().getClassMeta(ComplexPojoWithMap.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<ComplexPojoWithMap> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<ComplexPojoWithMap>(classMeta, getMapperConfig());

        builder.addKey("id");
        builder.addMapping("map_key_elt0");
        builder.addMapping("map_key_elt1");
        builder.addMapping("map_value_elt0");
        builder.addMapping("map_value_elt1");



        JoinMapper<Object[], Object[][],ComplexPojoWithMap, ?> mapper =
                (JoinMapper<Object[], Object[][], ComplexPojoWithMap, ?>) builder.mapper();

        List<ComplexPojoWithMap> list = mapper.forEach(dataComplexJoin, new ListCollector<ComplexPojoWithMap>()).getList();

        assertEquals(2, list.size());

        assertEquals(buildComplexPojo(1, "key11", "key12", "value11", "value12", "key111", "key121", "value111", "value121"), list.get(0));
        assertEquals(buildComplexPojo(2, "key21", "key22", "value21", "value22", "key211", "key221", "value211", "value221"), list.get(1));
    }

    @Test
    public void testListComplexKeyValueJoin() throws Exception {
        ClassMeta<ComplexPojoWithList> classMeta = reflectionService().getClassMeta(ComplexPojoWithList.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<ComplexPojoWithList> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<ComplexPojoWithList>(classMeta, getMapperConfig());

        builder.addKey("id");
        builder.addMapping("map_elt0_elt0_elt0");
        builder.addMapping("map_elt0_elt0_elt1");
        builder.addMapping("map_elt0_elt1_elt0");
        builder.addMapping("map_elt0_elt1_elt1");


        JoinMapper<Object[], Object[][],ComplexPojoWithList, ?> mapper =
                (JoinMapper<Object[], Object[][], ComplexPojoWithList, ?>) builder.mapper();

        List<ComplexPojoWithList> list = mapper.forEach(dataComplexJoin, new ListCollector<ComplexPojoWithList>()).getList();

        assertEquals(2, list.size());

        assertEquals(buildComplexListPojo(1, "key11", "key12", "value11", "value12", "key111", "key121", "value111", "value121"), list.get(0));
        assertEquals(buildComplexListPojo(2, "key21", "key22", "value21", "value22", "key211", "key221", "value211", "value221"), list.get(1));
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

    private ComplexPojoWithMap buildComplexPojo(int id, String... kvPair) {
        ComplexPojoWithMap p = new ComplexPojoWithMap();
        p.setId(id);
        Map<Tuple2<String, String>, Tuple2<String, String>> map = new HashMap<Tuple2<String, String>, Tuple2<String, String>>();

        for(int i = 0; i< kvPair.length; i+=4) {
            map.put(new Tuple2<String, String>(kvPair[i], kvPair[i + 1]), new Tuple2<String, String>(kvPair[i + 2], kvPair[i + 3]));
        }
        p.setMap(map);

        return p;
    }

    private ComplexPojoWithList buildComplexListPojo(int id, String... kvPair) {
        ComplexPojoWithList p = new ComplexPojoWithList();
        p.setId(id);
        List<Tuple2<Tuple2<String, String>, Tuple2<String, String>>> map = new ArrayList<Tuple2<Tuple2<String, String>, Tuple2<String, String>>>();

        for(int i = 0; i< kvPair.length; i+=4) {
            map.add(new Tuple2<Tuple2<String, String>, Tuple2<String, String>>(new Tuple2<String, String>(kvPair[i], kvPair[i + 1]), new Tuple2<String, String>(kvPair[i + 2], kvPair[i + 3])));
        }
        p.setMap(map);

        return p;
    }

    private ReflectionService reflectionService() {
        return ReflectionService.disableAsm();
    }

    private MapperConfig<SampleFieldKey, Object[]> getMapperConfig() {
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


    public static class ComplexPojoWithMap {

        private int id;
        private Map<Tuple2<String, String>, Tuple2<String, String>> map;

        public int getId() {
            return id;
        }

        public Map<Tuple2<String, String>, Tuple2<String, String>> getMap() {
            return map;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setMap(Map<Tuple2<String, String>, Tuple2<String, String>> map) {
            this.map = map;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ComplexPojoWithMap that = (ComplexPojoWithMap) o;

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
            return "ComplexPojoWithMap{" +
                    "id=" + id +
                    ", map=" + map +
                    '}';
        }
    }

    public static class ComplexPojoWithList {

        private int id;
        private List<Tuple2<Tuple2<String, String>, Tuple2<String, String>>> map;

        public int getId() {
            return id;
        }

        public List<Tuple2<Tuple2<String, String>, Tuple2<String, String>>> getMap() {
            return map;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setMap(List<Tuple2<Tuple2<String, String>, Tuple2<String, String>>> map) {
            this.map = map;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ComplexPojoWithList that = (ComplexPojoWithList) o;

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
            return "ComplexPojoWithList{" +
                    "id=" + id +
                    ", map=" + map +
                    '}';
        }
    }
}
