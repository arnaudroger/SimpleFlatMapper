package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.ArrayClassMeta;
import org.simpleflatmapper.reflect.meta.ConstructorPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ReflectionServiceTest {
    private PropertyFinder.PropertyFilter isValidPropertyMeta = PropertyFinder.PropertyFilter .trueFilter();

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

        final PropertyMeta<Node, Object> propertyMeta = cm.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("parent_parent_parent"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
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


        assertEquals("Rudolph", meta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("name"), new Object[0], (TypeAffinity)null, isValidPropertyMeta).getGetter().get(value));



    }

    @Test
    public void testDisableAsmResolvedConstructorParamName() {
        ClassMeta<DbFinalObject> classMeta = ReflectionService.disableAsm().getClassMeta(DbFinalObject.class);

        PropertyMeta<DbFinalObject, ?> property = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("id"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
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


    @Test
    public void testClassWithNoDebug() throws ClassNotFoundException, IOException {
        final Class<?> classWithoutDebug = ReflectionInstantiatorDefinitionFactoryTest.CLASS_LOADER.loadClass("p.ClassNoDebug");

        ClassMeta<?> classMeta = ReflectionService.newInstance().getClassMeta(classWithoutDebug);

        PropertyFinder<?> propertyFinder = classMeta.newPropertyFinder();
        PropertyMeta<?, ?> name = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("name"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        PropertyMeta<?, ?> value = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("value"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        PropertyMeta<?, ?> arg0 = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("arg0"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);
        PropertyMeta<?, ?> arg1 = propertyFinder.findProperty(DefaultPropertyNameMatcher.of("arg1"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);

        assertNull(arg0);
        assertNull(arg1);

        assertTrue(name instanceof ConstructorPropertyMeta);
        assertEquals(0, ((ConstructorPropertyMeta)name).getParameter().getIndex());
        assertEquals(String.class, ((ConstructorPropertyMeta)name).getParameter().getType());
        assertNull(((ConstructorPropertyMeta)name).getParameter().getName());

        assertTrue(value instanceof ConstructorPropertyMeta);
        assertEquals(1, ((ConstructorPropertyMeta)value).getParameter().getIndex());
        assertEquals(int.class, ((ConstructorPropertyMeta)value).getParameter().getType());
        assertNull(((ConstructorPropertyMeta)value).getParameter().getName());
    }

}
