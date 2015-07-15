package org.sfm.reflect.meta;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;

import static org.junit.Assert.assertArrayEquals;

public class ObjectClassMetaTest {


    @Test
    public void testGenerateHeaders() {
        String[] names = {"id", "name", "email", "creation_time", "type_ordinal", "type_name"};
        assertArrayEquals(
                names,
                ReflectionService.newInstance().getClassMeta(DbFinalObject.class).generateHeaders());
        assertArrayEquals(
                names,
                ReflectionService.newInstance().getClassMeta(DbObject.class).generateHeaders());
    }

    public static class MyClass{
        private String id;

        private DbObject o;

        public MyClass(String id) {
            this.id = id;
        }
        public MyClass() {

        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public DbObject getO() {
            return o;
        }

        public void setO(DbObject o) {
            this.o = o;
        }
    }
    @Test
    public void testGenerateHeadersWithConstructorAndSetterProperty() {
        String[] names = {"id", "o_id", "o_name", "o_email", "o_creation_time", "o_type_ordinal", "o_type_name"};
        assertArrayEquals(
                names,
                ReflectionService.newInstance().getClassMeta(MyClass.class).generateHeaders());
    }


}
