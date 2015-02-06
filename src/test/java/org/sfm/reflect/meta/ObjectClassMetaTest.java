package org.sfm.reflect.meta;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;

import static org.junit.Assert.assertArrayEquals;

public class ObjectClassMetaTest {


    @Test
    public void testGenerateHeaders() {
        String[] names = {"id", "name", "email", "creationTime", "typeOrdinal", "typeName"};
        assertArrayEquals(
                names,
                ReflectionService.newInstance().getRootClassMeta(DbFinalObject.class).generateHeaders());
        assertArrayEquals(
                names,
                ReflectionService.newInstance().getRootClassMeta(DbObject.class).generateHeaders());
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
        String[] names = {"id", "o_id", "o_name", "o_email", "o_creationTime", "o_typeOrdinal", "o_typeName"};
        assertArrayEquals(
                names,
                ReflectionService.newInstance().getRootClassMeta(MyClass.class).generateHeaders());
    }
}
