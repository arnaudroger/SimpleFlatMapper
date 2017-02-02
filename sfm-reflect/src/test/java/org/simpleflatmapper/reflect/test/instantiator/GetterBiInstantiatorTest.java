package org.simpleflatmapper.reflect.test.instantiator;

import org.junit.Test;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.instantiator.GetterBiInstantiator;
import org.simpleflatmapper.reflect.instantiator.GetterInstantiator;

import static org.junit.Assert.assertEquals;

public class GetterBiInstantiatorTest {

    @Test
    public void test() throws Exception {
        Object o = new Object();
        GetterBiInstantiator<Object,Object, Object> getterInstantiator = new GetterBiInstantiator<Object, Object,Object>(
                new ConstantGetter<Object, Object>(o)
        );

        assertEquals(o, getterInstantiator.newInstance(null, null));
    }
}