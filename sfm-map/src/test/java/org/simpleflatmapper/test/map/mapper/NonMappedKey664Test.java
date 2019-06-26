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
    public void testListItemWithSideKey() throws Exception {
        ClassMeta<FooItem> classMeta = reflectionService().getClassMeta(FooItem.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<FooItem> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<FooItem>(classMeta, getMapperConfig());

        builder.addMapping("id", KeyProperty.DEFAULT);
        builder.addMapping("items_x");
        builder.addMapping("items_y");
        builder.addMapping("items_seq_nr", KeyProperty.DEFAULT, OptionalProperty.INSTANCE);


        SetRowMapper<Object[], Object[][],FooItem, ?> mapper =
                builder.mapper();

        List<FooItem> list = mapper.forEach(new Object[][] {
                { "XX", "foo", "fooy", "0" },
                { "XX", "bar", "bary", "1" },
                { "XX", "foo", "fooy", "2" },
                { "XX", "bar", "bary", "1" },
                { "YY", null,  null, null }
        }, new ListCollector<FooItem>()).getList();

        assertEquals(2, list.size());
        assertEquals(Arrays.asList(new Item("foo", "fooy"), new Item("bar", "bary"), new Item("foo", "fooy")), list.get(0).items);
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

    public static class FooItem {
        public String id;
        public List<Item> items;

        @Override
        public String toString() {
            return "Foo{" +
                    "id='" + id + '\'' +
                    ", items=" + items +
                    '}';
        }

        public FooItem(String id, List<Item> items) {
            this.id = id;
            this.items = items;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FooItem fooItem = (FooItem) o;

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
    public static class Item {
        public String x, y;

        public Item(String x, String y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

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
