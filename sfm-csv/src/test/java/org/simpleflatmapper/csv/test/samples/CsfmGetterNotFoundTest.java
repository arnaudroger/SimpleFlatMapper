package org.simpleflatmapper.csv.test.samples;

import org.junit.Test;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.property.CustomReaderProperty;

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
    public void csvMapperExtrapolateGetterFromConstructor() {
        CsvMapperFactory.newInstance().newBuilder(Foo.class).addMapping("bar").mapper();
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
    public void csvMapperGetterNotFound() {
        try {
            CsvMapperFactory.newInstance().newBuilder(Foo2.class).addMapping("bar").mapper();
            fail();
        } catch(MapperBuildingException e) {
         //   assertTrue(e.getMessage().contains("CSFM_GETTER"));
            // expected
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
