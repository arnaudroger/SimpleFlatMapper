package org.simpleflatmapper.reflect.test.getter;

import org.junit.Test;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.getter.StringUUIDGetter;

import java.util.UUID;

import static org.junit.Assert.*;

public class StringUUIDGetterTest {

    @Test
    public void test() throws Exception {
        UUID uuid = UUID.randomUUID();

        StringUUIDGetter<Object> getter = new StringUUIDGetter<Object>(new ConstantGetter<Object, String>(uuid.toString()));

        assertEquals(uuid, getter.get(null));

        getter.toString();


        getter = new StringUUIDGetter<Object>(new ConstantGetter<Object, String>(null));

        assertNull(getter.get(null));

    }
}