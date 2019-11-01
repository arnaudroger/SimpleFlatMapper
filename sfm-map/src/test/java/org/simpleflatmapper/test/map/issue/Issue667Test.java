package org.simpleflatmapper.test.map.issue;

import org.junit.Test;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.test.map.mapper.AbstractMapperBuilderTest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class Issue667Test {
    @Test
    public void failing() throws Exception {
        ClassMeta<Root> classMeta = ReflectionService.newInstance().getClassMeta(Root.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Root> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Root>(classMeta, MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig());


        builder.addKey("id");
        builder.addMapping("foos_id");

        SetRowMapper<Object[], Object[][], Root, Exception> mapper = builder.mapper();


        Iterator<Root> iterator = mapper.iterator(new Object[][]{
                {"id", "fid1"},
                {"id", "fid2"},
                {"id2", "fid3"}}
        );

        assertTrue(iterator.hasNext());
        assertEquals(new Root("id", Arrays.asList(new Foo("fid1"), new Foo("fid2"))), iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals(new Root("id2", Arrays.asList(new Foo("fid3"))), iterator.next());
        assertFalse(iterator.hasNext());

    }

    public class Root {
        private String id;
        private List<Foo> foos;

        public Root() {}
        public Root(final String id, final List<Foo> foos) {
            this.id = id;
            this.foos = foos;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Foo> getFoos() {
            return foos;
        }

        public void setFoos(List<Foo> foos) {
            this.foos = foos;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Root root = (Root) o;

            if (id != null ? !id.equals(root.id) : root.id != null) return false;
            return foos != null ? foos.equals(root.foos) : root.foos == null;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (foos != null ? foos.hashCode() : 0);
            return result;
        }
    }

    public class Foo {
        private String id;

        public Foo() {}
        public Foo(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Foo foo = (Foo) o;

            return id != null ? id.equals(foo.id) : foo.id == null;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }
}
