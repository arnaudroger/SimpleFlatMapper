package org.sfm.reflect.meta;

import org.junit.Test;
import org.sfm.reflect.ReflectionService;
import org.sfm.utils.FalsePredicate;

import static org.junit.Assert.assertEquals;

public class SingletonPropertyFinderTest {


    @Test
    public void selectStringConstructorOverCharArray() {
        ClassMeta<String> stringClassMeta =new ObjectClassMeta<String>(String.class, ReflectionService.newInstance());
        SingletonPropertyFinder<String> pf = new SingletonPropertyFinder<String>(stringClassMeta);

        ConstructorPropertyMeta<String, ?> blop = (ConstructorPropertyMeta<String, ?>) pf.findProperty(new DefaultPropertyNameMatcher("value", 0, false, false));

        assertEquals(String.class, blop.getConstructorParameter().getType());

    }
}
