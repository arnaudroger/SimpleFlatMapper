package org.sfm.samples;

import org.junit.Test;
import org.sfm.csv.CsvWriter;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.jdbc.MultiIndexFieldMapper;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.column.IndexedSetterFactoryProperty;
import org.sfm.map.column.IndexedSetterProperty;
import org.sfm.map.column.SetterFactoryProperty;
import org.sfm.map.column.SetterProperty;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.IndexedSetter;
import org.sfm.reflect.IndexedSetterFactory;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;

import java.io.IOException;
import java.sql.PreparedStatement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PsSetterNotFoundTest {


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
    public void jdbcMapperExtrapolateGetterFromConstructor() throws Exception {
        final MultiIndexFieldMapper<Foo>[] fieldMappers =
                JdbcMapperFactory.newInstance().buildFrom(Foo.class).addColumn("bar").buildIndexFieldMappers();


        assertEquals(1, fieldMappers.length);
        PreparedStatement ps = mock(PreparedStatement.class);
        fieldMappers[0].map(ps, new Foo(new BarOneProp("val")), 0);
        verify(ps).setString(1, "val");


        JdbcMapperFactory.newInstance().buildFrom(Crux.class).addColumn("foo").buildIndexFieldMappers();
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
            final MultiIndexFieldMapper<Foo2>[] indexFieldMappers =
                    JdbcMapperFactory.newInstance().buildFrom(Foo2.class).addColumn("bar").buildIndexFieldMappers();
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
                .addColumnProperty("bar", new IndexedSetterProperty(new IndexedSetter<PreparedStatement, Bar2Prop>() {

                    @Override
                    public void set(PreparedStatement target, Bar2Prop value, int index) throws Exception {
                        target.setString(index, value.getVal());
                        target.setInt(index + 1, value.getI());
                    }
                }))
                .buildFrom(Foo2.class)
                .addColumn("bar")
                .buildIndexFieldMappers();
    }
    @Test
    public void jdbcMapperExtrapolateOverrideSetterFactory() {
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
                .buildIndexFieldMappers();
    }
}
