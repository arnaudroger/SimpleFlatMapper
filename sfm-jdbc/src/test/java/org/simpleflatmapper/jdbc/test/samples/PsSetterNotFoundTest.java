package org.simpleflatmapper.jdbc.test.samples;

import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.EmptyContext;
import org.simpleflatmapper.converter.EmptyContextFactoryBuilder;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;
import org.simpleflatmapper.jdbc.property.IndexedSetterProperty;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.jdbc.property.IndexedSetterFactoryProperty;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.reflect.IndexedSetterFactory;
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
                JdbcMapperFactory.newInstance().buildFrom(Foo.class).addColumn("bar").buildIndexFieldMappers(EmptyContextFactoryBuilder.INSTANCE);


        assertEquals(1, fieldMappers.length);
        PreparedStatement ps = mock(PreparedStatement.class);
        fieldMappers[0].map(ps, new Foo(new BarOneProp("val")), 0, EmptyContext.INSTANCE);
        verify(ps).setString(1, "val");


        JdbcMapperFactory.newInstance().buildFrom(Crux.class).addColumn("foo").buildIndexFieldMappers(EmptyContextFactoryBuilder.INSTANCE);
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
                    JdbcMapperFactory.newInstance().buildFrom(Foo2.class).addColumn("bar").buildIndexFieldMappers(EmptyContextFactoryBuilder.INSTANCE);
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
                .buildIndexFieldMappers(EmptyContextFactoryBuilder.INSTANCE);
    }
    @Test
    public void jdbcMapperExtrapolateOverrideSetterFactory() {
        JdbcMapperFactory
                .newInstance()
                .addColumnProperty("bar",
                        new IndexedSetterFactoryProperty(
                            new IndexedSetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey>>() {
                                @SuppressWarnings("unchecked")
                                @Override
                                public <P> IndexedSetter<PreparedStatement, P> getIndexedSetter(final PropertyMapping<?, ?, JdbcColumnKey> arg, Object... properties) {
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
                .buildIndexFieldMappers(EmptyContextFactoryBuilder.INSTANCE);
    }
}
