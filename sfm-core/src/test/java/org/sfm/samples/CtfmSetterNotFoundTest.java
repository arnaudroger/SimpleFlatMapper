package org.sfm.samples;

import org.junit.Test;
import org.sfm.csv.CsvWriter;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.column.GetterProperty;
import org.sfm.map.column.IndexedSetterFactoryProperty;
import org.sfm.map.column.IndexedSetterProperty;
import org.sfm.map.column.SetterFactoryProperty;
import org.sfm.map.column.SetterProperty;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.Getter;
import org.sfm.reflect.IndexedSetter;
import org.sfm.reflect.IndexedSetterFactory;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
    public void jdbcMapperExtrapolateGetterFromConstructor() {
        JdbcMapperFactory.newInstance().buildFrom(Foo.class).addColumn("bar").mapper();
        JdbcMapperFactory.newInstance().buildFrom(Crux.class).addColumn("foo").mapper();
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
     * the jdbc setter will fail to be found.
     */
    @Test
    public void jdbcMapperExtrapolateFailToFindSetter() {
        try {
            JdbcMapperFactory.newInstance().buildFrom(Foo2.class).addColumn("bar").mapper();
            fail();
        } catch (MapperBuildingException e) {
            // expected
        }
    }

    /*
     * you will need to provide you own setter via a SetterProperty or a SetterFactoryProperty
     */
    @Test
    public void jdbcMapperExtrapolateOverrideSetter() {
        JdbcMapperFactory
                .newInstance()
                .addColumnProperty("bar", new SetterProperty(new Setter<PreparedStatement, Bar2Prop>() {

                    @Override
                    public void set(PreparedStatement target, Bar2Prop value) throws Exception {
                        target.setString(3, value.getVal());
                        target.setInt(4, value.getI());
                    }
                }))
                .buildFrom(Foo2.class)
                .addColumn("bar")
                .mapper();
    }
    @Test
    public void jdbcMapperExtrapolateOverrideSetterFactory() {
        JdbcMapperFactory
                .newInstance()
                .addColumnProperty("bar",
                        new SetterFactoryProperty(
                            new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>>>() {
                                @Override
                                public <P> Setter<PreparedStatement, P> getSetter(final PropertyMapping<?, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> arg) {
                                    return (Setter<PreparedStatement, P>) new Setter<PreparedStatement, Bar2Prop>() {
                                        @Override
                                        public void set(PreparedStatement target, Bar2Prop value) throws Exception {
                                            target.setString(arg.getColumnKey().getIndex(), value.getVal());
                                            target.setInt(arg.getColumnKey().getIndex() + 1, value.getI());
                                        }
                                    };
                                }
                            }
                ))
                .buildFrom(Foo2.class)
                .addColumn("bar")
                .mapper();
    }

    @Test
    public void jdbcMapperExtrapolateOverrideIndexedSetter() {
        JdbcMapperFactory
                .newInstance()
                .addColumnProperty("bar", new IndexedSetterProperty(new IndexedSetter<PreparedStatement, Bar2Prop>() {

                    @Override
                    public void set(PreparedStatement target, Bar2Prop value, int index) throws Exception {
                        target.setString(index, value.getVal());
                        target.setInt(index + 1, value.getI());
                    }
                }))
                .buildFrom(Foo2.class)
                .addColumn("bar")
                .mapper();
    }
    @Test
    public void jdbcMapperExtrapolateOverrideIndexedSetterFactory() {
        JdbcMapperFactory
                .newInstance()
                .addColumnProperty("bar",
                        new IndexedSetterFactoryProperty(
                                new IndexedSetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>>>() {
                                    @Override
                                    public <P> IndexedSetter<PreparedStatement, P> getIndexedSetter(final PropertyMapping<?, ?, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey>> arg) {
                                        return (IndexedSetter<PreparedStatement, P>) new IndexedSetter<PreparedStatement, Bar2Prop>() {
                                            @Override
                                            public void set(PreparedStatement target, Bar2Prop value, int index) throws Exception {
                                                target.setString(index, value.getVal());
                                                target.setInt(index + 1, value.getI());
                                            }
                                        };
                                    }
                                }
                        ))
                .buildFrom(Foo2.class)
                .addColumn("bar")
                .mapper();
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
