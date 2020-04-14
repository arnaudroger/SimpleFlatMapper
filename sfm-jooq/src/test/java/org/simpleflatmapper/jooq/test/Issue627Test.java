package org.simpleflatmapper.jooq.test;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.simpleflatmapper.jooq.SfmRecordMapperProviderFactory;
import org.simpleflatmapper.map.annotation.Key;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class Issue627Test {


    @Test
    public void testHsqlDb() throws SQLException {
        Connection conn = DbHelper.objectDb();

        DSLContext dsl = DSL
                .using(new DefaultConfiguration().set(conn)
                        .set(SQLDialect.HSQLDB)
                        .set(SfmRecordMapperProviderFactory.newInstance().ignorePropertyNotFound().newProvider()));

        List<Issue627> list =
                dsl.select(
                    DSL.value("id1").as("id"),
                    DSL.val(null, String.class).as("foo_id"),
                        DSL.val(null, String.class).as("foo_name")
                ).fetchInto(Issue627.class);

        assertEquals(1, list.size());

        Issue627 value = list.get(0);

        assertEquals("id1", value.id);
        assertEquals(null, value.foo);


    }

    public static class Issue627 {
        private String id;
        private Foo foo;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Foo getFoo() {
            return foo;
        }

        public void setFoo(Foo foo) {
            this.foo = foo;
        }
    }

    public static class Foo {
        @Key
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static Field createField(String name, Class<?> aClass) {

        Field field = mock(Field.class);
        when(field.getName()).thenReturn(name);
        when(field.getType()).thenReturn(aClass);
        return field;
    }

}
