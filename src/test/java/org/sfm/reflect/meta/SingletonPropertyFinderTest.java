package org.sfm.reflect.meta;

import org.junit.Test;
import org.sfm.reflect.ReflectionService;

import static org.junit.Assert.assertEquals;

public class SingletonPropertyFinderTest {


    public static class  MyClass {
        public MyClass(char[] value) {
        }
        public MyClass(String value) {
        }
    }
    @Test
    public void selectStringConstructorOverCharArray() {
        ClassMeta<MyClass> stringClassMeta =new ObjectClassMeta<MyClass>(MyClass.class, ReflectionService.newInstance());
        SingletonPropertyFinder<MyClass> pf = new SingletonPropertyFinder<MyClass>(stringClassMeta);

        ConstructorPropertyMeta<MyClass, ?> blop = (ConstructorPropertyMeta<MyClass, ?>) pf.findProperty(new DefaultPropertyNameMatcher("blop", 0, false, false));

        assertEquals(String.class, blop.getConstructorParameter().getType());

    }
}
