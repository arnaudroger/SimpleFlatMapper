package org.simpleflatmapper.reflect.test.getter;

import org.junit.Test;
import org.simpleflatmapper.reflect.getter.ArraySizeGetter;

import static org.junit.Assert.*;

public class ArraySizeGetterTest {


    @Test
    public void test() throws Exception {
        ArraySizeGetter getter = new ArraySizeGetter();
        assertEquals(3, getter.getInt(new String[] {"", "", ""}));

        getter.toString();
    }
}