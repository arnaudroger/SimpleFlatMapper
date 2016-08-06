package org.simpleflatmapper.reflect.getter;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ListSizeGetterTest {


    @Test
    public void test() throws Exception {
        ListSizeGetter getter = new ListSizeGetter();
        assertEquals(3, getter.getInt(Arrays.asList("", "", "")));
        getter.toString();
    }

}