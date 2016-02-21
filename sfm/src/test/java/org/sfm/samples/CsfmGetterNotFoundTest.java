package org.sfm.samples;

import org.junit.Test;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.CsvMapperFactory;
import org.sfm.csv.ParsingContext;
import org.sfm.csv.column.CustomReaderProperty;
import org.sfm.csv.impl.cellreader.StringCellValueReader;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.column.GetterProperty;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;

import static org.junit.Assert.fail;

public class CsfmGetterNotFoundTest {


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

    public static class FooS {
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
        JdbcMapperFactory.newInstance().newBuilder(FooS.class).addMapping("bar").mapper();
        JdbcMapperFactory.newInstance().newBuilder(Crux.class).addMapping("foo").mapper();
    }


    @Test
    public void csvMapperExtrapolateGetterFromConstructor() {
        CsvMapperFactory.newInstance().newBuilder(FooC.class).addMapping("bar").mapper();
        CsvMapperFactory.newInstance().newBuilder(FooS.class).addMapping("bar").mapper();
        CsvMapperFactory.newInstance().newBuilder(Crux.class).addMapping("foo").mapper();
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
            // expected
        }
    }

    @Test
    public void csvMapperGetterNotFound() {
        try {
            CsvMapperFactory.newInstance().newBuilder(Foo2.class).addMapping("bar").mapper();
            fail();
        } catch(MapperBuildingException e) {
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

    @Test
    public void csvMapperCustomReader() {
        CsvMapperFactory
                .newInstance()
                .addColumnProperty("bar", new CustomReaderProperty(new CellValueReader<Bar2>() {
                    @Override
                    public Bar2 read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                        return new Bar2(StringCellValueReader.readString(chars, offset, length), 2);
                    }
                }))
                .newBuilder(Foo2.class)
                .addMapping("bar").mapper();
    }

}
