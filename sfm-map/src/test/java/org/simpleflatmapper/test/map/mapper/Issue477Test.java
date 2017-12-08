package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.mapper.JoinMapper;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.property.MapTypeProperty;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.util.ListCollector;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class Issue477Test {

    private static final Object[][] dataSimple = new Object[][] {
            { 1, "id1", "id2" },
            { 1, "id11", "id22" },
            { 2, null, null },
            { 3, "id1", null },
            { 4, null, "id2" },
    };


    @Test
    public void testIssue() {
        ClassMeta<ListOfPojo> classMeta = reflectionService().getClassMeta(ListOfPojo.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<ListOfPojo> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<ListOfPojo>(classMeta, getMapperConfig());

        builder.addMapping("id", KeyProperty.DEFAULT);
        builder.addMapping("list_pojoA_id", KeyProperty.DEFAULT);
        builder.addMapping("list_pojoB_id", KeyProperty.DEFAULT);


        Mapper<Object[], ListOfPojo> rowMapper = builder.mapper();

        JoinMapper<Object[], Object[][],ListOfPojo, RuntimeException> mapper =
                (JoinMapper<Object[], Object[][], ListOfPojo, RuntimeException>) rowMapper;

        List<ListOfPojo> list = mapper.forEach(dataSimple, new ListCollector<ListOfPojo>()).getList();

        assertEquals(4, list.size());
        
        assertEquals(list.get(1).list.size(), 0);
        
    }


    private ReflectionService reflectionService() {
        return ReflectionService.disableAsm();
    }

    private MapperConfig<SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> getMapperConfig() {
        return MapperConfig.fieldMapperConfig();
    }

    public static class ListOfPojo {
        public final int id;
        public final List<Pojo> list;

        public ListOfPojo(int id, List<Pojo> list) {
            this.id = id;
            this.list = list;
        }
    }
    
    public static class Pojo {
        public final PojoA pojoA;
        public final PojoB pojoB;

        public Pojo(PojoA pojoA, PojoB pojoB) {
            this.pojoA = pojoA;
            this.pojoB = pojoB;
        }
    }
    
    public static class PojoA {
        public final String id;

        public PojoA(String id) {
            this.id = id;
        }
    }
    public static class PojoB {
        private final String id;

        public PojoB(String id) {
            this.id = id;
        }
    }

}