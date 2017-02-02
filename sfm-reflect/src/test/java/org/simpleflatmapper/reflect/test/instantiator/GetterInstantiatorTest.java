package org.simpleflatmapper.reflect.test.instantiator;

import org.junit.Test;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.instantiator.GetterInstantiator;

import static org.junit.Assert.*;

public class GetterInstantiatorTest {

    @Test
    public void test() throws Exception {
        Object o = new Object();
        GetterInstantiator<Object, Object> getterInstantiator = new GetterInstantiator<Object, Object>(
                new ConstantGetter<Object, Object>(o)
        );

        assertEquals(o, getterInstantiator.newInstance(null));
    }
}