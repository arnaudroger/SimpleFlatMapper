package org.simpleflatmapper.csv.test.samples;

import org.junit.Test;
import org.simpleflatmapper.map.property.SetterProperty;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.csv.CsvWriter;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CtfmSetterNotFoundTest {


    /*
     * When we are setting a object with one property we will use that property to inject.
     */

    public static class Foo {
        private final BarOneProp bar;

        public Foo(BarOneProp bar) {
            this.bar = bar;
        }

        public BarOneProp getBar() {
            return bar;
        }
    }


    public static class BarOneProp {
        private final String val;

        public BarOneProp(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

    public static class Crux {
        private final Foo foo;

        public Crux(Foo foo) {
            this.foo = foo;
        }

        public Foo getFoo() {
            return foo;
        }
    }


    @Test
    public void csvMapperExtrapolateGetterFromConstructor() throws IOException {
        StringBuilder sb = new StringBuilder();
        CsvWriter.from(Foo.class).column("bar").to(sb).append(new Foo(new BarOneProp("val")));
        assertEquals("bar\r\nval\r\n", sb.toString());
        sb = new StringBuilder();
        CsvWriter.from(Crux.class).column("foo").to(sb).append(new Crux(new Foo(new BarOneProp("val"))));
        assertEquals("foo\r\nval\r\n", sb.toString());
    }

    /*
     * When we are using a object with more than one property.
     */

    public static class Foo2 {
        private final Bar2Prop bar;

        public Foo2(Bar2Prop bar) {
            this.bar = bar;
        }

        public Bar2Prop getBar() {
            return bar;
        }
    }


    public static class Bar2Prop {
        private final String val;
        private final int i;

        public Bar2Prop(String val, int i) {
            this.val = val;
            this.i = i;
        }

        public int getI() {
            return i;
        }

        public String getVal() {
            return val;
        }

        public String toString() {
            return "toString";
        }
    }


    /*
     * the csv writer will use the toString method of the object.
     */
    @Test
    public void csvMapperExtrapolateCallToString() throws IOException {
        StringBuilder sb = new StringBuilder();
        CsvWriter.from(Foo2.class).column("bar").to(sb).append(new Foo2(new Bar2Prop("val", 3)));
        assertEquals("bar\r\ntoString\r\n", sb.toString());
    }

    @Test
    public void csvMapperOverrideGetter() throws IOException {
        StringBuilder sb = new StringBuilder();
        CsvWriter
                .from(Foo2.class)
                .column("bar", new SetterProperty(new Setter<Appendable, Bar2Prop>() {

                    @Override
                    public void set(Appendable target, Bar2Prop value) throws Exception {
                        target.append(value.getVal()).append(":").append(String.valueOf(value.getI()));
                    }
                }))
                .to(sb)
                .append(new Foo2(new Bar2Prop("val", 3)));
        assertEquals("bar\r\nval:3\r\n", sb.toString());
    }
}
