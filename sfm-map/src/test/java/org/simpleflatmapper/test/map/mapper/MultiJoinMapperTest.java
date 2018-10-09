package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.annotation.Key;
import org.simpleflatmapper.map.mapper.JoinMapper;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.TypeReference;

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
        @Key
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
    public void testMultiJoin() throws Exception {
        ClassMeta<Root> classMeta = ReflectionService.newInstance().getClassMeta(Root.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Root> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Root>(classMeta, mapperConfig());

        JoinMapper<Object[], Object[][], Root, ?> mapper =
                (JoinMapper<Object[], Object[][], Root, ?>)  
                        builder
                                .addMapping("id")
                                .addKey("ll_id")
                                .addKey("ls_id")
                                .mapper();

        List<Root> list = mapper.forEach(data, new ListCollector<Root>()).getList();

        assertEquals(2, list.size());
        validateRoot(list.get(0));
        validateRoot(list.get(1));
    }

    public static class Root2 {
        @Key
        public int id;
        public List<Element2> ll;
        public List<Element2> ls;
    }

    public static class Element2 {
        public String id;
        public String value;
        public Element2 element;
    }
    @Test
    public void testMultiJoinOnNullNoKey() throws Exception {
        ClassMeta<Root2> classMeta = ReflectionService.newInstance().getClassMeta(Root2.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Root2> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Root2>(classMeta, mapperConfig());

    
        JoinMapper<Object[], Object[][], Root2, ?> mapper =
                (JoinMapper<Object[], Object[][], Root2, ?>) builder
                        .addKey("id")
                        .addKey("ll_id")
                        .addKey("ls_element_id")
                        .mapper();
       Object[][] data = new Object[][] {
                {1, "1", null, null},
                {1, "2", null, null}
        };
        List<Root2> list = mapper.forEach(data, new ListCollector<Root2>()).getList();

        assertEquals(1, list.size());
    }

    private MapperConfig<SampleFieldKey, Object[]> mapperConfig() {
        return MapperConfig.fieldMapperConfig();
    }


    public static Object[][] data_diffvalue = new Object[][] {
            {1, 1, 1, "a"},
            {1, 2, 1, "b"},
            {1, 1, 2, "b"}
    };
    @Test
    public void testMultiJoinSameIdDiffContent() throws Exception {
        ClassMeta<Root> classMeta = ReflectionService.newInstance().getClassMeta(Root.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Root> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Root>(classMeta, mapperConfig());

        JoinMapper<Object[], Object[][], Root, ?> mapper =
                (JoinMapper<Object[], Object[][], Root, ?>)  builder.addMapping("id").addKey("ll_id").addKey("ll_elements_id").addMapping("ll_elements_value").mapper();

        List<Root> list = mapper.forEach(data_diffvalue, new ListCollector<Root>()).getList();

        assertEquals(1, list.size());

        Root root = list.get(0);

        assertEquals(1, root.id);
        assertEquals(2, root.ll.size());
        assertEquals(1, root.ll.get(0).id);
        assertEquals(2, root.ll.get(1).id);


        assertEquals(2, root.ll.get(0).elements.size());
        assertEquals(1, root.ll.get(0).elements.get(0).id);
        assertEquals("a", root.ll.get(0).elements.get(0).value);
        assertEquals(2, root.ll.get(0).elements.get(1).id);
        assertEquals("b", root.ll.get(0).elements.get(1).value);

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

        @Key
        private final int id;
        private final  List<ElementC> ll;
        private final  List<ElementC> ls;

        public RootC(int id, List<ElementC> ll, List<ElementC> ls) {
            this.id = id;
            this.ll = ll;
            this.ls = ls;
        }

        public int getId() {
            return id;
        }

        public List<ElementC> getLl() {
            return ll;
        }

        public List<ElementC> getLs() {
            return ls;
        }
    }

    public static class ElementC {
        private final int id;

        public ElementC(int id) {
            this.id = id;
        }
        @Key
        public int getId() {
            return id;
        }
    }

    @Test
    public void testMultiJoinC() throws Exception {
        ClassMeta<RootC> classMeta = ReflectionService.newInstance(false).getClassMeta(RootC.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<RootC> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<RootC>(classMeta, mapperConfig());

        SetRowMapper<Object[], Object[][], RootC, ?> mapper =
                builder
                        .addMapping("id")
                        .addMapping("ll_id")
                        .addMapping("ls_id")
                        .mapper();

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


    public static Object[][] tdata = new Object[][] {
            {1, 1, 1},
            {1, 1, 2},
            {1, 2, 1},
            {2, 3, 1}
    };

    @Test
    public void testMultiJoinTuples() throws Exception {
        ClassMeta<Tuple2<A, List<Tuple2<B, List<C>>>>> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<Tuple2<A, List<Tuple2<B, List<C>>>>>() {}.getType());

        AbstractMapperBuilderTest.SampleMapperBuilder<Tuple2<A, List<Tuple2<B, List<C>>>>> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Tuple2<A, List<Tuple2<B, List<C>>>>>(classMeta, mapperConfig());

        SetRowMapper<Object[], Object[][], Tuple2<A, List<Tuple2<B, List<C>>>>, ?> mapper =
                
                        builder
                                .addMapping("id", KeyProperty.DEFAULT)
                                .addMapping("elt1_elt0_elt0_id", KeyProperty.DEFAULT)
                                .addMapping("elt1_elt0_elt1_elt0_id", KeyProperty.DEFAULT)
                                .mapper();


        List<Tuple2<A, List<Tuple2<B, List<C>>>>> list = mapper.forEach(tdata, new ListCollector<Tuple2<A, List<Tuple2<B, List<C>>>>>()).getList();

        assertEquals(2, list.size());

        assertEquals(1, list.get(0).first().id);

        assertEquals(2, list.get(0).second().size());

        assertEquals(1, list.get(0).second().get(0).first().id);
        assertEquals(2, list.get(0).second().get(0).second().size());
        assertEquals(1, list.get(0).second().get(0).second().get(0).id);
        assertEquals(2, list.get(0).second().get(0).second().get(1).id);

        assertEquals(2, list.get(0).second().get(1).first().id);
        assertEquals(1, list.get(0).second().get(1).second().size());
        assertEquals(1, list.get(0).second().get(1).second().get(0).id);


        assertEquals(2, list.get(1).first().id);

        assertEquals(1, list.get(1).second().size());

        assertEquals(3, list.get(1).second().get(0).first().id);
        assertEquals(1, list.get(1).second().get(0).second().size());
        assertEquals(1, list.get(1).second().get(0).second().get(0).id);



    }

    public static class A {
        public int id;
    }
    public static class B {
        public int id;
    }
    public static class C {
        public int id;
    }



}
