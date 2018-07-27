package org.simpleflatmapper.test.map.mapper;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.simpleflatmapper.map.EnumerableMapper;
import org.simpleflatmapper.reflect.ReflectionService;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;


public class GuavaCollectionMapperTest {


    @Test
    public void testImmutableList() throws Exception {

        AbstractMapperBuilderTest.SampleMapperBuilder<ImmutableA> builderA = 
                new AbstractMapperBuilderTest.SampleMapperBuilder<ImmutableA>(ReflectionService.newInstance(false).getClassMeta(ImmutableA.class));

        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], ImmutableA, ?> mapper =
                builderA.mapper();

        ImmutableA a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(ImmutableList.copyOf(Arrays.asList(new B("v1"), new B("v2"))), a.bs);

    }

    @Test
    public void testImmutableWrapperList() throws Exception {

        AbstractMapperBuilderTest.SampleMapperBuilder<ImmutableAWrapper> builderA =
                new AbstractMapperBuilderTest.SampleMapperBuilder<ImmutableAWrapper>(ReflectionService.newInstance(false).getClassMeta(ImmutableAWrapper.class));

        builderA.addKey("a_id").addMapping("a_bs_v");

        EnumerableMapper<Object[][], ImmutableAWrapper, ?> mapper =
                builderA.mapper();

        ImmutableAWrapper a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(ImmutableList.copyOf(Arrays.asList(new B("v1"), new B("v2"))), a.a.bs);

    }
    
    public static class ImmutableAWrapper {
        private final String str;
        private final ImmutableA a;

        public ImmutableAWrapper(String str, ImmutableA a) {
            this.str = str;
            this.a = a;
        }

        public String getStr() {
            return str;
        }

        public ImmutableA getA() {
            return a;
        }
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