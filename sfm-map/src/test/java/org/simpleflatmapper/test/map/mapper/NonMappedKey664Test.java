package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.map.property.OptionalProperty;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.util.ListCollector;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class NonMappedKey664Test {



    @Test
    public void testListStringWithSideKey() throws Exception {
        ClassMeta<Foo> classMeta = reflectionService().getClassMeta(Foo.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Foo> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Foo>(classMeta, getMapperConfig());

        builder.addMapping("id", KeyProperty.DEFAULT);
        builder.addMapping("items_item");
        builder.addMapping("items_seq_nr", KeyProperty.DEFAULT, OptionalProperty.INSTANCE);


        SetRowMapper<Object[], Object[][],Foo, ?> mapper =
            builder.mapper();

        List<Foo> list = mapper.forEach(new Object[][] {
                { "XX", "foo", "0" },
                { "XX", "bar", "1" },
                { "XX", "foo", "2" },
                { "XX", "bar", "1" },
                { "YY", null,  null }
        }, new ListCollector<Foo>()).getList();


        assertEquals(2, list.size());
        assertEquals(Arrays.asList("foo", "bar", "foo"), list.get(0).items);
        assertEquals(Arrays.asList(), list.get(1).items);
        assertEquals("XX", list.get(0).id);
        assertEquals("YY", list.get(1).id);
    }



    @Test
    public void testListItem2WithSideKey() throws Exception {
        ClassMeta<FooItem2> classMeta = reflectionService().getClassMeta(FooItem2.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<FooItem2> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<FooItem2>(classMeta, getMapperConfig());

        builder.addMapping("id", KeyProperty.DEFAULT);
        builder.addMapping("items_x");
        builder.addMapping("items_y");
        builder.addMapping("items_seq_nr", KeyProperty.DEFAULT, OptionalProperty.INSTANCE);


        SetRowMapper<Object[], Object[][], FooItem2, ?> mapper =
                builder.mapper();

        List<FooItem2> list = mapper.forEach(new Object[][] {
                { "XX", "foo", "fooy", "0" },
                { "XX", "bar", "bary", "1" },
                { "XX", "foo", "fooy", "2" },
                { "XX", "bar", "bary", "1" },
                { "YY", null,  null, null }
        }, new ListCollector<FooItem2>()).getList();

        assertEquals(2, list.size());
        assertEquals(Arrays.asList(new Item2("foo", "fooy"), new Item2("bar", "bary"), new Item2("foo", "fooy")), list.get(0).items);
        assertEquals(Arrays.asList(), list.get(1).items);
        assertEquals("XX", list.get(0).id);
        assertEquals("YY", list.get(1).id);

    }


    @Test
    public void testListItem2WithSideKeyBeforeField() throws Exception {
        ClassMeta<FooItem2> classMeta = reflectionService().getClassMeta(FooItem2.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<FooItem2> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<FooItem2>(classMeta, getMapperConfig());

        builder.addMapping("id", KeyProperty.DEFAULT);
        builder.addMapping("items_seq_nr", KeyProperty.DEFAULT, OptionalProperty.INSTANCE);
        builder.addMapping("items_x");
        builder.addMapping("items_y");


        SetRowMapper<Object[], Object[][], FooItem2, ?> mapper =
                builder.mapper();

        List<FooItem2> list = mapper.forEach(new Object[][] {
                { "XX", "0", "foo", "fooy" },
                { "XX", "1", "bar", "bary" },
                { "XX", "2", "foo", "fooy" },
                { "XX", "1", "bar", "bary" },
                { "YY", null,  null, null }
        }, new ListCollector<FooItem2>()).getList();

        assertEquals(2, list.size());
        assertEquals(Arrays.asList(new Item2("foo", "fooy"), new Item2("bar", "bary"), new Item2("foo", "fooy")), list.get(0).items);
        assertEquals(Arrays.asList(), list.get(1).items);
        assertEquals("XX", list.get(0).id);
        assertEquals("YY", list.get(1).id);

    }


    @Test
    public void testListItem1WithSideKey() throws Exception {
        ClassMeta<FooItem1> classMeta = reflectionService().getClassMeta(FooItem1.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<FooItem1> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<FooItem1>(classMeta, getMapperConfig());

        builder.addMapping("id", KeyProperty.DEFAULT);
        builder.addMapping("items_x");
        builder.addMapping("items_seq_nr", KeyProperty.DEFAULT, OptionalProperty.INSTANCE);


        SetRowMapper<Object[], Object[][], FooItem1, ?> mapper =
                builder.mapper();

        List<FooItem1> list = mapper.forEach(new Object[][] {
                { "XX", "foo",  "0" },
                { "XX", "bar",  "1" },
                { "XX", "foo", "2" },
                { "XX", "bar", "1" },
                { "YY", null,   null }
        }, new ListCollector<FooItem1>()).getList();

        assertEquals(2, list.size());
        assertEquals(Arrays.asList(new Item1("foo"), new Item1("bar"), new Item1("foo")), list.get(0).items);
        assertEquals(Arrays.asList(), list.get(1).items);
        assertEquals("XX", list.get(0).id);
        assertEquals("YY", list.get(1).id);

    }


    @Test
    public void testListItem1WithSideKeyBeforeField() throws Exception {
        ClassMeta<FooItem1> classMeta = reflectionService().getClassMeta(FooItem1.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<FooItem1> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<FooItem1>(classMeta, getMapperConfig());

        builder.addMapping("id", KeyProperty.DEFAULT);
        builder.addMapping("items_seq_nr", KeyProperty.DEFAULT, OptionalProperty.INSTANCE);
        builder.addMapping("items_x");


        SetRowMapper<Object[], Object[][], FooItem1, ?> mapper =
                builder.mapper();

        List<FooItem1> list = mapper.forEach(new Object[][] {
                { "XX", "0", "foo" },
                { "XX", "1", "bar" },
                { "XX", "2", "foo" },
                { "XX", "1", "bar" },
                { "YY", null,  null }
        }, new ListCollector<FooItem1>()).getList();

        assertEquals(2, list.size());
        assertEquals(Arrays.asList(new Item1("foo"), new Item1("bar"), new Item1("foo")), list.get(0).items);
        assertEquals(Arrays.asList(), list.get(1).items);
        assertEquals("XX", list.get(0).id);
        assertEquals("YY", list.get(1).id);

    }

    private ReflectionService reflectionService() {
        return ReflectionService.disableAsm();
    }

    private MapperConfig<SampleFieldKey, Object[]> getMapperConfig() {
        return MapperConfig.fieldMapperConfig();
    }


    public static class Foo {
        public String id;
        public List<String> items;

        @Override
        public String toString() {
            return "Foo{" +
                    "id='" + id + '\'' +
                    ", items=" + items +
                    '}';
        }
    }


    public static class Item1 {
        public String x;

        public Item1(String x) {
            this.x = x;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item1 item1 = (Item1) o;

            return x != null ? x.equals(item1.x) : item1.x == null;
        }

        @Override
        public int hashCode() {
            return x != null ? x.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Item1{" +
                    "x='" + x + '\'' +
                    '}';
        }
    }

    public static class FooItem1 {
        public String id;
        public List<Item1> items;

        @Override
        public String toString() {
            return "Foo{" +
                    "id='" + id + '\'' +
                    ", items=" + items +
                    '}';
        }

        public FooItem1(String id, List<Item1> items) {
            this.id = id;
            this.items = items;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FooItem1 fooItem = (FooItem1) o;

            if (id != null ? !id.equals(fooItem.id) : fooItem.id != null) return false;
            return items != null ? items.equals(fooItem.items) : fooItem.items == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (items != null ? items.hashCode() : 0);
            return result;
        }
    }


    public static class FooItem2 {
        public String id;
        public List<Item2> items;

        @Override
        public String toString() {
            return "Foo{" +
                    "id='" + id + '\'' +
                    ", items=" + items +
                    '}';
        }

        public FooItem2(String id, List<Item2> items) {
            this.id = id;
            this.items = items;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FooItem2 fooItem = (FooItem2) o;

            if (id != null ? !id.equals(fooItem.id) : fooItem.id != null) return false;
            return items != null ? items.equals(fooItem.items) : fooItem.items == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (items != null ? items.hashCode() : 0);
            return result;
        }
    }
    public static class Item2 {
        public String x, y;

        public Item2(String x, String y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item2 item = (Item2) o;

            if (x != null ? !x.equals(item.x) : item.x != null) return false;
            return y != null ? y.equals(item.y) : item.y == null;
        }

        @Override
        public int hashCode() {
            int result = x != null ? x.hashCode() : 0;
            result = 31 * result + (y != null ? y.hashCode() : 0);
            return result;
        }
    }


}
