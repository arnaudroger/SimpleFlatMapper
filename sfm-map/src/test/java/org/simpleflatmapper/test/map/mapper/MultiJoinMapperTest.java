package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.mapper.JoinMapper;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.util.ListCollector;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class MultiJoinMapperTest {


    public static Object[][] data = new Object[][] {
            {1, 1, 1},
            {1, 1, 2},
            {1, 2, 1},
            {1, 2, 2},
            {2, 3, 1},
            {2, 3, 4},
            {2, 4, 1},
            {2, 4, 4}
    };

    public static class Root {
        public int id;
        public List<Element> ll;
        public List<Element> ls;
    }

    public static class Element {
        public int id;
        public String value;
        public List<Element> elements;

    }

    @Test
    public void testMultiJoin() {
        ClassMeta<Root> classMeta = ReflectionService.newInstance().getClassMeta(Root.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Root> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Root>(classMeta, mapperConfig());

        Mapper<Object[], Root> rowMapper = builder.addKey("id").addKey("ll_id").addKey("ls_id").mapper();
        JoinMapper<Object[], Object[][], Root, RuntimeException> mapper =
                (JoinMapper<Object[], Object[][], Root, RuntimeException>) rowMapper;

        List<Root> list = mapper.forEach(data, new ListCollector<Root>()).getList();

        assertEquals(2, list.size());
        validateRoot(list.get(0));
        validateRoot(list.get(1));
    }

    private MapperConfig<SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> mapperConfig() {
        return MapperConfig.fieldMapperConfig();
    }


    public static Object[][] data_diffvalue = new Object[][] {
            {1, 1, 1, "a"},
            {1, 2, 1, "b"}
    };
    @Test
    public void testMultiJoinSameIdDiffContent() {
        ClassMeta<Root> classMeta = ReflectionService.newInstance().getClassMeta(Root.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Root> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Root>(classMeta, mapperConfig());

        Mapper<Object[], Root> rowMapper = builder.addKey("id").addKey("ll_id").addKey("ll_elements_id").addMapping("ll_elements_value").mapper();
        JoinMapper<Object[], Object[][], Root, RuntimeException> mapper =
                (JoinMapper<Object[], Object[][], Root, RuntimeException>) rowMapper;

        List<Root> list = mapper.forEach(data_diffvalue, new ListCollector<Root>()).getList();

        assertEquals(1, list.size());

        Root root = list.get(0);

        assertEquals(1, root.id);
        assertEquals(2, root.ll.size());
        assertEquals(1, root.ll.get(0).id);
        assertEquals(2, root.ll.get(1).id);


        assertEquals(1, root.ll.get(0).elements.size());
        assertEquals(1, root.ll.get(0).elements.get(0).id);
        assertEquals("a", root.ll.get(0).elements.get(0).value);

        assertEquals(1, root.ll.get(1).elements.size());
        assertEquals(1, root.ll.get(1).elements.get(0).id);
        assertEquals("b", root.ll.get(1).elements.get(0).value);
    }


    private static void validateRoot(Root r) {
        assertEquals(2, r.ll.size());
        assertEquals(2, r.ls.size());

        assertEquals(((r.id - 1) * 2) + 1 , r.ll.get(0).id);
        assertEquals(((r.id - 1) * 2) + 2, r.ll.get(1).id);

        assertEquals(1, r.ls.get(0).id);
        assertEquals(2 * r.id, r.ls.get(1).id);
    }



    public static class RootC {
        private final int id;
        private final  List<Element> ll;
        private final  List<Element> ls;

        public RootC(int id, List<Element> ll, List<Element> ls) {
            this.id = id;
            this.ll = ll;
            this.ls = ls;
        }

        public int getId() {
            return id;
        }

        public List<Element> getLl() {
            return ll;
        }

        public List<Element> getLs() {
            return ls;
        }
    }

    public static class ElementC {
        private final int id;

        public ElementC(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    @Test
    public void testMultiJoinC() {
        ClassMeta<RootC> classMeta = ReflectionService.newInstance().getClassMeta(RootC.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<RootC> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<RootC>(classMeta, mapperConfig());

        Mapper<Object[], RootC> rowMapper = builder.addKey("id").addKey("ll_id").addKey("ls_id").mapper();
        JoinMapper<Object[], Object[][], RootC, RuntimeException> mapper =
                (JoinMapper<Object[], Object[][], RootC, RuntimeException>) rowMapper;

        List<RootC> list = mapper.forEach(data, new ListCollector<RootC>()).getList();

        assertEquals(2, list.size());
        validateRootC(list.get(0));
        validateRootC(list.get(1));
    }

    private static void validateRootC(RootC r) {
        assertEquals(2, r.ll.size());
        assertEquals(2, r.ls.size());

        assertEquals(((r.id - 1) * 2) + 1 , r.ll.get(0).id);
        assertEquals(((r.id - 1) * 2) + 2, r.ll.get(1).id);

        assertEquals(1, r.ls.get(0).id);
        assertEquals(2 * r.id, r.ls.get(1).id);
    }



}
