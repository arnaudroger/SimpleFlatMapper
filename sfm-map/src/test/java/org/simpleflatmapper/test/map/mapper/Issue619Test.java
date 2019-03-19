package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.util.ListCollector;

import java.util.List;

//IFJAVA8_START
import java.util.Optional;
//IFJAVA8_END

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Issue619Test {

    private static final Object[][] dataSimple = new Object[][] {
            { 1l, 1l, "bar1" },
            { 2l, null, null },
    };


    //IFJAVA8_START
    @Test
    public void testIssue() throws Exception {
        ClassMeta<Foo> classMeta = reflectionService().getClassMeta(Foo.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Foo> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Foo>(classMeta, getMapperConfig());

        builder.addMapping("id", KeyProperty.DEFAULT);
        builder.addMapping("bar_id", KeyProperty.DEFAULT);
        builder.addMapping("bar_name");


        SetRowMapper<Object[], Object[][],Foo, ?> mapper =
            builder.mapper();

        List<Foo> list = mapper.forEach(dataSimple, new ListCollector<Foo>()).getList();

        assertEquals(2, list.size());
        
        assertEquals(1, list.get(0).id);
        assertEquals(1, list.get(0).getBar().get().id);
        assertEquals("bar1", list.get(0).getBar().get().name);

        assertEquals(2, list.get(1).id);
        assertEquals(null, list.get(1).getBar().orElse(null));

    }


    private ReflectionService reflectionService() {
        return ReflectionService.disableAsm();
    }

    private MapperConfig<SampleFieldKey, Object[]> getMapperConfig() {
        return MapperConfig.fieldMapperConfig();
    }


    public static class Foo {
        private long id;
        private Bar bar;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Optional<Bar> getBar() {
            return Optional.ofNullable(bar);
        }

        public void setBar(Bar bar) {
            this.bar = bar;
        }
    }

    public static class Bar {
        public long id;
        public String name;
    }
    //IFJAVA8_END

}
