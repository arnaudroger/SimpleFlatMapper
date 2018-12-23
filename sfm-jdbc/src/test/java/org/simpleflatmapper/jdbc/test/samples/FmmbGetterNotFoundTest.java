package org.simpleflatmapper.jdbc.test.samples;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FmmbGetterNotFoundTest {


    /*
     * When we are building an object with one argument constructor
     * we will try to find a getter that matches the constructor arg.
     */

    public static class FooC {
        private final BarOneArgConst bar;

        public FooC(BarOneArgConst bar) {
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
        private final FooC foo;

        public Crux(FooC foo) {
            this.foo = foo;
        }

        public FooC getFoo() {
            return foo;
        }
    }

    @Test
    public void jdbcMapperExtrapolateGetterFromConstructor() {
        JdbcMapperFactory.newInstance().newBuilder(FooC.class).addMapping("bar").mapper();
        JdbcMapperFactory.newInstance().newBuilder(Crux.class).addMapping("foo").mapper();
    }



    /*
     * But that can't be done when there are no on arg constructor.
     */
    public static class Foo2 {
        private final Bar2 bar;

        public Foo2(Bar2 bar) {
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
