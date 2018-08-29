package org.simpleflatmapper.jdbc.test.samples;

import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jdbc.property.IndexedSetterProperty;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.jdbc.property.IndexedSetterFactoryProperty;
import org.simpleflatmapper.map.property.SetterFactoryProperty;
import org.simpleflatmapper.map.property.SetterProperty;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.reflect.IndexedSetterFactory;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;

import java.sql.PreparedStatement;

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
                            new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey>>() {
                                @SuppressWarnings("unchecked")
                                @Override
                                public <P> Setter<PreparedStatement, P> getSetter(final PropertyMapping<?, ?, JdbcColumnKey> arg) {
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
                .mapper();
    }

}
