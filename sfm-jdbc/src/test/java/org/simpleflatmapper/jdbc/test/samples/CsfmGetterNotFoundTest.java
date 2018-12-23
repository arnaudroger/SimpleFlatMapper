package org.simpleflatmapper.jdbc.test.samples;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CsfmGetterNotFoundTest {


    /*
     * When we are building an object with one argument constructor
     * we will try to find a getter that matches the constructor arg.
     */

    public static class Foo {
        private BarOneArgConst bar;

        public BarOneArgConst getBar() {
            return bar;
        }

        public void setBar(BarOneArgConst bar) {
            this.bar = bar;
        }
    }


    public static class BarOneArgConst {
        private final String val;

        public BarOneArgConst(String val) {
            this.val = val;
        }
    }

    public static class Crux {
        private  Foo foo;

        public Foo getFoo() {
            return foo;
        }

        public void setFoo(Foo foo) {
            this.foo = foo;
        }
    }

    @Test
    public void jdbcMapperExtrapolateGetterFromConstructor() {
        JdbcMapperFactory.newInstance().newBuilder(Foo.class).addMapping("bar").mapper();
    }



    /*
     * But that can't be done when there are no on arg constructor.
     */
    public static class Foo2 {
        private Bar2 bar;

        public Bar2 getBar() {
            return bar;
        }

        public void setBar(Bar2 bar) {
            this.bar = bar;
        }
    }

    public static class Bar2 {
        private final String val;
        private final int i;

        public Bar2(String val, int i) {
            this.val = val;
            this.i = i;
        }
    }

    @Test
    public void jdbcMapperGetterNotFound() {
        try {
            JdbcMapperFactory.newInstance().newBuilder(Foo2.class).addKey("bar").mapper();
            fail();
        } catch(MapperBuildingException e) {
//            assertTrue(e.getMessage().contains("CSFM_GETTER"));
            // expected
        }
    }

    /*
     * You then need to specify your own custom getter/reader
     */
    @Test
    public void jdbcMapperCustomGetter() {
       JdbcMapperFactory
               .newInstance()
               .addColumnProperty("bar", new GetterProperty(new Getter<ResultSet, Bar2>() {
                   @Override
                   public Bar2 get(ResultSet target) throws Exception {
                       return new Bar2(target.getString("bar"), 2);
                   }
               }))
               .newBuilder(Foo2.class)
               .addKey("bar").mapper();
    }


}
