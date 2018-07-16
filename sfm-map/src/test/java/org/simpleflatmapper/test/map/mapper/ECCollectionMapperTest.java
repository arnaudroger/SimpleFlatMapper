package org.simpleflatmapper.test.map.mapper;

import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.Test;
import org.simpleflatmapper.map.EnumerableMapper;
import org.simpleflatmapper.reflect.ReflectionService;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;


public class ECCollectionMapperTest {

    @Test
    public void testMutableList() throws Exception {

        AbstractMapperBuilderTest.SampleMapperBuilder<MutableA> builderA = new AbstractMapperBuilderTest.SampleMapperBuilder<MutableA>(ReflectionService.newInstance(false).getClassMeta(MutableA.class));

        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], MutableA, ?> mapper =
                builderA.mapper();

        MutableA a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(Lists.mutable.of(new B("v1"), new B("v2")), a.bs);

    }

    @Test
    public void testImmutableList() throws Exception {

//        AbstractMapperBuilderTest.SampleMapperBuilder<ImmutableA> builderA = new AbstractMapperBuilderTest.SampleMapperBuilder<ImmutableA>(ReflectionService.newInstance(false).getClassMeta(ImmutableA.class));
//
//        builderA.addKey("id").addMapping("bs_v");
//
//        EnumerableMapper<Object[][], ImmutableA, ?> mapper =
//                builderA.mapper();
//
//        ImmutableA a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();
//
//        assertEquals(Lists.immutable.of(new B("v1"), new B("v2")), a.bs);

    }

    public static class ImmutableA {
        private final int id;
        private final ImmutableList<B> bs;

        public ImmutableA(int id, ImmutableList<B> bs) {
            this.id = id;
            this.bs = bs;
        }

        public ImmutableList getBs() {
            return bs;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "A{" +
                    "id=" + id +
                    ", bs=" + bs +
                    '}';
        }

    }
 

    public static class MutableA {
        private final int id;
        private final MutableList<B> bs;

        public MutableA(int id, MutableList<B> bs) {
            this.id = id;
            this.bs = bs;
        }

        public MutableList getBs() {
            return bs;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "A{" +
                    "id=" + id +
                    ", bs=" + bs +
                    '}';
        }

    }
    public static class B {
        private final String v;

        public B(String v) {
            this.v = v;
        }


        @Override
        public String toString() {
            return "B{" +
                    "v='" + v + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            B b = (B) o;

            return v != null ? v.equals(b.v) : b.v == null;
        }

        @Override
        public int hashCode() {
            return v != null ? v.hashCode() : 0;
        }
    }

}