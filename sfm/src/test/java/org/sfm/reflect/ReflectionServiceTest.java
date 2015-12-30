package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.reflect.meta.ArrayClassMeta;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.ConstructorPropertyMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.tuples.Tuple2;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ReflectionServiceTest {


    @Test
    public void testClassMetaCache() {
        final ReflectionService reflectionService = ReflectionService.newInstance();

        assertSame(
                reflectionService.getClassMeta(new TypeReference<Tuple2<String, String>>() {}.getType()),
                reflectionService.getClassMeta(new TypeReference<Tuple2<String, String>>() {}.getType())
        );
        assertNotSame(
                reflectionService.getClassMeta(new TypeReference<Tuple2<String, String>>() {
                }.getType()),
                reflectionService.getClassMeta(new TypeReference<Tuple2<String, Long>>() {
                }.getType())
        );
    }


    @Test
    public void testSelfReferringClass() {
        final ReflectionService reflectionService = ReflectionService.newInstance();

        ClassMeta<Node> cm = reflectionService.getClassMeta(Node.class);

        final PropertyMeta<Node, Object> propertyMeta = cm.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("parent_parent_parent"));
        assertNotNull(propertyMeta);
        assertNotNull(cm);
    }


    @Test
    public void testListSubClass() {
        final ClassMeta<?> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<StringList>() {
        }.getType());

        ArrayClassMeta<?, ?> acm = (ArrayClassMeta<?, ?>) classMeta;

        assertEquals(String.class, acm.getElementTarget());
    }

    public static class StringList extends ArrayList<String> {

    }

    public static class Node {
        public Node parent;
    }

    @Test
    public void testGetterOnInterfaceCall() throws Exception {
        IGetters value = new IGetters();
        value.setId(1223);
        value.setName("Rudolph");

        ClassMeta<IGetters> meta = ReflectionService.newInstance().getClassMeta(IGetters.class);


        assertEquals("Rudolph", meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("name")).getGetter().get(value));



    }

    @Test
    public void testDisableAsmResolvedConstructorParamName() {
        ClassMeta<DbFinalObject> classMeta = ReflectionService.disableAsm().getClassMeta(DbFinalObject.class);

        PropertyMeta<DbFinalObject, ?> property = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("id"));
        assertTrue(property.isConstructorProperty());
    }



    public interface Named<T> {
        T getName();
    }


    public static class IGetters implements Named<String> {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
