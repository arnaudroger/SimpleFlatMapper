package org.simpleflatmapper.csv.test.samples;

import org.junit.Test;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.property.CustomReaderProperty;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.util.ListCollector;

import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
    public void csvMapperExtrapolateGetterFromConstructor() {
        CsvMapperFactory.newInstance().newBuilder(FooC.class).addMapping("bar").mapper();
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
    public void csvMapperGetterNotFound() throws Exception {
        try {
            CsvMapper<Foo2> bar = CsvMapperFactory.newInstance().newBuilder(Foo2.class).addMapping("bar").mapper();

            fail();
        } catch(MapperBuildingException e) {
            // expected
            System.out.println("e = " + e);
        }
    }

    /*
     * You then need to specify your own custom getter/reader
     */
    @Test
    public void csvMapperCustomReader() {
        CsvMapperFactory
                .newInstance()
                .addColumnProperty("bar", new CustomReaderProperty(new CellValueReader<Bar2>() {
                    @Override
                    public Bar2 read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                        return new Bar2(new String(chars, offset, length), 2);
                    }
                }))
                .newBuilder(Foo2.class)
                .addMapping("bar").mapper();
    }

}
